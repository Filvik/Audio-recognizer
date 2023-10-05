package com.audiorecognizer.backend.service.transcribe;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.audiorecognizer.backend.config.YandexCloudSettings;
import com.audiorecognizer.backend.model.LongRunningRecognizeRequest;
import com.audiorecognizer.backend.model.LongRunningRecognizeResponse;
import com.audiorecognizer.backend.model.TaskConditionEnum;
import com.audiorecognizer.backend.model.TaskTranscribe;
import com.audiorecognizer.backend.service.record.RecordAudioResultNotificator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static com.audiorecognizer.backend.model.TaskConditionEnum.*;

@Component
@RequiredArgsConstructor
public class TranscribeComponent {
    private final Logger log = LogManager.getLogger(this.getClass());

    private final YandexCloudSettings yandexCloudSettings;
    private final RecordAudioResultNotificator recordAudioResultNotificator;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Загрузка файла в стораж
     *
     * @param taskTranscribe задание на распознавание
     * @param amazonS3       подключение к сторажу
     * @return истина, при успешной загрузке файла
     */
    public boolean uploadFile(TaskTranscribe taskTranscribe, AmazonS3 amazonS3) {
        taskTranscribe.setTaskConditionEnum(TaskConditionEnum.CLOUD_SENDING);
        String objectKey = taskTranscribe.getFile().getName();

        // Загружаем объект
        PutObjectResult putObjectResult = amazonS3.putObject(yandexCloudSettings.getStorage().bucketName(), objectKey, taskTranscribe.getFile());
        return !putObjectResult.getETag().isEmpty();
    }

    /**
     * генерируем ссылку на объект
     *
     * @param taskTranscribe задание на распознавание
     * @param amazonS3       подключение к сторажу
     */
    public void generateLinkS3(TaskTranscribe taskTranscribe, AmazonS3 amazonS3) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            cal.add(Calendar.DATE, 1);
            Date date = cal.getTime();
            String objectKey = taskTranscribe.getFile().getName();
            URL url = amazonS3.generatePresignedUrl(yandexCloudSettings.getStorage().bucketName(), objectKey, date);
            taskTranscribe.setUrl(url);
            taskTranscribe.setTaskConditionEnum(TaskConditionEnum.CLOUD_UPLOAD);
        } catch (RuntimeException e) {
            log.error("Ошибка при генерации ссылки на объект");
            taskTranscribe.setTaskConditionEnum(SEND_TRANSCRIBE_ERROR);
            recordAudioResultNotificator.sendRecordAudioResult(taskTranscribe);
        }
    }

    /**
     * Отправление запроса на распознавание и обработка ответа
     *
     * @param taskTranscribe задание на распознавание
     */
    public void getTranscribeRequest(TaskTranscribe taskTranscribe, URL url) {

        LongRunningRecognizeRequest request = createLongRunningRecognizeRequest(taskTranscribe);
        try {
            if (request.getAudio() == null) {
                throw new RuntimeException();
            }
            LongRunningRecognizeResponse longRunningRecognizeResponse = sendReq(request, url);
            taskTranscribe.setLongRunningRecognizeResponse(longRunningRecognizeResponse);
            taskTranscribe.setTaskConditionEnum(TRANSCRIBE_SENDING);
        } catch (IOException e) {
            taskTranscribe.setTaskConditionEnum(SEND_TRANSCRIBE_ERROR);
            recordAudioResultNotificator.sendRecordAudioResult(taskTranscribe);
            log.error("Ошибка при запросе на перевод");
        } catch (RuntimeException e) {
            taskTranscribe.setTaskConditionEnum(FORMAT_ERROR);
            recordAudioResultNotificator.sendRecordAudioResult(taskTranscribe);
        }
    }


    /**
     * Отправление и получение тела ответа запроса на распознавание, формирование запроса
     *
     * @param request тело запроса
     * @return тело ответа
     * @throws IOException исключение при проблеме с буферами чтения/записи
     */
    private LongRunningRecognizeResponse sendReq(LongRunningRecognizeRequest request, URL url) throws IOException {
        String body = asJsonString(request);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestProperty(yandexCloudSettings.getTranscribe().authorization(), yandexCloudSettings.getKey().apiKey());
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpURLConnection.setRequestProperty("Content-Length", "" + body.getBytes().length);
        httpURLConnection.setRequestProperty("Content-Language", "en-US");
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        // Записываем тело запроса в поток вывода
        OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
        writer.write(body);
        writer.flush();

        // Считываем ответ от сервера
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        // Выводим ответ от сервера
        return mapper.readValue(response.toString(), LongRunningRecognizeResponse.class);
    }

    /**
     * Формирование тела запроса на распознавание
     *
     * @param taskTranscribe сущность запроса
     * @return сущность задания на распознавание
     */
    private LongRunningRecognizeRequest createLongRunningRecognizeRequest(TaskTranscribe taskTranscribe) {
        LongRunningRecognizeRequest.Specification specification = new LongRunningRecognizeRequest.Specification();

        specification.setModel(yandexCloudSettings.getSpecification().getModel());
        try {
            specification.setAudioEncoding(taskTranscribe.getAudioEncoding());
        } catch (RuntimeException e) {
            log.error("Неподдерживаемый формат аудио!");
            return new LongRunningRecognizeRequest(new LongRunningRecognizeRequest.Config(specification), null);
        }
        specification.setLanguageCode(yandexCloudSettings.getSpecification().getLanguageCode());
        specification.setProfanityFilter(yandexCloudSettings.getSpecification().isProfanityFilter());
        specification.setSampleRateHertz(yandexCloudSettings.getSpecification().getSampleRateHertz());
        specification.setAudioChannelCount(yandexCloudSettings.getSpecification().getAudioChannelCount());
        specification.setLiteratureText(yandexCloudSettings.getSpecification().isLiteratureText());

        LongRunningRecognizeRequest.Audio audio = new LongRunningRecognizeRequest.Audio();
        try {
            audio.setUri(taskTranscribe.getUrl().toURI().toString());
        } catch (URISyntaxException e) {
            log.error("Ошибка при получении файла на загрузку в storage");
        }
        return new LongRunningRecognizeRequest(new LongRunningRecognizeRequest.Config(specification), audio);
    }

    /**
     * Преобразование объекта в строку json
     *
     * @param obj объект
     * @return строка json
     */
    private String asJsonString(final Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

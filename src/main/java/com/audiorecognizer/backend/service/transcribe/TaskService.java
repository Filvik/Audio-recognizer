package com.audiorecognizer.backend.service.transcribe;

import com.audiorecognizer.backend.config.YandexCloudSettings;
import com.audiorecognizer.backend.model.OperationStatusResponse;
import com.audiorecognizer.backend.model.TaskConditionEnum;
import com.audiorecognizer.backend.model.TaskTranscribe;
import com.audiorecognizer.backend.service.GenerateNameService;
import com.audiorecognizer.backend.service.record.RecordAudioResultNotificator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.audiorecognizer.backend.model.TaskConditionEnum.*;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final Logger log = LogManager.getLogger(this.getClass());
    private final Map<String, TaskTranscribe> tasks = new ConcurrentHashMap<>();
    private final GenerateNameService generateNameService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final RecordAudioResultNotificator recordAudioResultNotificator;
    private final YandexCloudSettings yandexCloudSettings;
    private final TranscribeService transcribeService;

    public TaskTranscribe addNewTask(String extension, boolean isRecord) {
        TaskTranscribe taskTranscribe = new TaskTranscribe();
        String taskId = generateNameService.getName();
        taskTranscribe.setTaskConditionEnum(TaskConditionEnum.NEW);
        taskTranscribe.setTaskId(taskId);
        taskTranscribe.setExtension(extension);
        taskTranscribe.setRecord(isRecord);
        tasks.put(taskId, taskTranscribe);
        return taskTranscribe;
    }

    @Scheduled(fixedDelay = 2000)
    public void checkResult() {
        tasks.entrySet().stream()
                .filter(entry -> entry.getValue().getTaskConditionEnum() == TRANSCRIBE_SENDING)
                .peek(entry -> checkResultTask(entry.getValue()))
                .filter(entry -> entry.getValue().getTaskConditionEnum() == COMPLETED)
                .forEach(entry -> recordAudioResultNotificator.sendRecordAudioResult(entry.getValue()));

        deleteCompletedTask();
    }

    private void checkResultTask(TaskTranscribe taskTranscribe) {
        URL url = null;
        try {
            url = new URL(yandexCloudSettings.getTranscribe().urlOperation() + taskTranscribe.getLongRunningRecognizeResponse().getId());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        try {
            OperationStatusResponse operationStatusResponse = getCheckRequest(url);
            if (operationStatusResponse.isDone()) {
                if (operationStatusResponse.getError() != null) {
                    createErrorResponse(taskTranscribe, operationStatusResponse.getError().getMessage());
                } else if (operationStatusResponse.getStatusResponse().getChunk() == null
                        || operationStatusResponse.getStatusResponse().getChunk().get(0).getAlternative() == null) {
                    createErrorResponse(taskTranscribe, "Не удалось распознать аудио.");
                } else {
                    taskTranscribe.setResultMessage(getText(operationStatusResponse));
                    taskTranscribe.setTaskConditionEnum(COMPLETED);
                }
            }
        } catch (IOException e) {
            log.error(TRANSCRIBE_ERROR.getDescription());
        }
    }

    /**
     * Отправление сообщения на проверку готовности задачи
     *
     * @param url адрес запроса
     * @return тело ответа
     * @throws IOException в случае проблем со считыванием тела ответа
     */
    private OperationStatusResponse getCheckRequest(URL url) throws IOException {
        HttpURLConnection myURLConnection = (HttpURLConnection) url.openConnection();
        myURLConnection.setRequestProperty(yandexCloudSettings.getTranscribe().authorization(), yandexCloudSettings.getKey().apiKey());
        myURLConnection.setRequestMethod("GET");
        myURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        myURLConnection.setRequestProperty("Content-Language", "en-US");
        myURLConnection.setUseCaches(false);
        myURLConnection.setDoOutput(true);

        // Считываем ответ от сервера
        BufferedReader reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        // Выводим ответ от сервера
        return mapper.readValue(response.toString(), OperationStatusResponse.class);
    }

    /**
     * Формирование распознанного текста
     *
     * @param operationStatusResponse сообщение с ответом
     * @return распознанный текст
     */
    private String getText(OperationStatusResponse operationStatusResponse) {
        StringBuilder stringBuilder = new StringBuilder();
        operationStatusResponse.getStatusResponse().getChunk()
                .forEach(chunk -> {
                    chunk.getAlternative()
                            .forEach(alternative ->
                                    stringBuilder.append(alternative.getText()));
                    stringBuilder.append(" ");
                });
        return stringBuilder.toString();
    }

    private void createErrorResponse(TaskTranscribe taskTranscribe, String additionalMessage) {
        taskTranscribe.setErrorDescription(additionalMessage);
        taskTranscribe.setTaskConditionEnum(TRANSCRIBE_ERROR);
        recordAudioResultNotificator.sendRecordAudioResult(taskTranscribe);
    }

    private void deleteCompletedTask() {
        tasks.values().stream()
                .filter(task -> TaskConditionEnum.COMPLETED_TASK.contains(task.getTaskConditionEnum()))
                .peek(transcribeService::deleteFileFromBucket)
                .forEach(task -> tasks.remove(task.getTaskId()));


    }


}

package com.audiorecognizer.backend.service.transcribe;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.audiorecognizer.backend.config.YandexCloudSettings;
import com.audiorecognizer.backend.model.TaskConditionEnum;
import com.audiorecognizer.backend.model.TaskTranscribe;
import com.audiorecognizer.backend.service.record.RecordAudioResultNotificator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;

import java.net.*;



@Service
@RequiredArgsConstructor
public class TranscribeService {
    private final Logger log = LogManager.getLogger(this.getClass());

    private final TranscribeComponent transcribeComponent;
    private final YandexCloudSettings yandexCloudSettings;
    private final RecordAudioResultNotificator recordAudioResultNotificator;
    private AmazonS3 amazonS3;
    private URL url = null;


    @PostConstruct
    public void initComponent() {
        amazonS3Init();
        try {
            url = new URL(yandexCloudSettings.getTranscribe().urlTranscriber());
            log.info("Сервис распознавания проинициализирован.");
        } catch (IOException e) {
            log.error("Ошибка при инициализации сервиса распознавания.");
        }
    }

    /**
     * Загрузка файла в storage и отправление задания на распознавание
     * @param taskTranscribe задание на распознавание
     */
    public void downloadAudioOnBucket(TaskTranscribe taskTranscribe) {
        if (transcribeComponent.uploadFile(taskTranscribe, amazonS3)){
            taskTranscribe.setTaskConditionEnum(TaskConditionEnum.SEND_TRANSCRIBE_ERROR);
            recordAudioResultNotificator.sendRecordAudioResult(taskTranscribe);
        }
        else {
            transcribeComponent.generateLinkS3(taskTranscribe, amazonS3);
            transcribeComponent.getTranscribeRequest(taskTranscribe, url);
        }
    }


    /**
     * Инициализация крединтала для использования сервиса в запросах s3
     */
    private void amazonS3Init() {
        AWSCredentials credentials = new BasicAWSCredentials(yandexCloudSettings.getKey().accessKeyId(), yandexCloudSettings.getKey().secretAccessKey());
        amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(
                        new AmazonS3ClientBuilder.EndpointConfiguration(
                                yandexCloudSettings.getStorage().endPoint(), yandexCloudSettings.getStorage().region()
                        )
                )
                .build();
    }
}

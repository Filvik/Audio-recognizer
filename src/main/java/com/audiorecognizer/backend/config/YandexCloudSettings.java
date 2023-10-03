package com.audiorecognizer.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "yandex-cloud-setting")
public class YandexCloudSettings {

    private YandexTranscribeKeys key;
    private YandexTranscribeSpecification specification;
    private Transcribe transcribe;
    private Storage storage;

    @Data
    public static class YandexTranscribeSpecification {

        private String languageCode;
        private String model;
        private boolean profanityFilter;
        private boolean literatureText;
        private String audioEncoding;
        private Integer sampleRateHertz;
        private Integer audioChannelCount;
    }

    public record YandexTranscribeKeys(String apiKey,
                                       String accessKeyId,
                                       String secretAccessKey) {
    }


    public record Transcribe(String authorization,
                             String urlTranscriber,
                             String urlOperation) {
    }

    public record Storage(String endPoint,
                          String region,
                          String bucketName) {
    }
}

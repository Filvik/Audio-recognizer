package com.audiorecognizer.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class LongRunningRecognizeRequest {

    @JsonProperty
    private Config config;
    @JsonProperty
    private Audio audio;

    public LongRunningRecognizeRequest(Config config, Audio audio) {
        this.config = config;
        this.audio = audio;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @NoArgsConstructor
    public static class Config {
        @JsonProperty
        private Specification specification;

        public Config(Specification specification) {
            this.specification = specification;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @NoArgsConstructor
    public static class Specification {
        @JsonProperty
        private String languageCode;
        @JsonProperty
        private String model;
        @JsonProperty
        private boolean profanityFilter;
        @JsonProperty("literature_text")
        private boolean literatureText = true;
        @JsonProperty
        private String audioEncoding;
        @JsonProperty
        private Integer sampleRateHertz;
        @JsonProperty
        private Integer audioChannelCount;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @NoArgsConstructor
    public static class Audio {
        @JsonProperty
        private String uri;
    }
}

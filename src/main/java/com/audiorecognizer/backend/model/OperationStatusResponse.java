package com.audiorecognizer.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class OperationStatusResponse {

    @JsonProperty("response")
    private ResponseTranscribe statusResponse;
    @JsonProperty("done")
    private boolean done;
    @JsonProperty
    private String id;
    @JsonProperty
    private String createdAt;
    @JsonProperty
    private String createdBy;
    @JsonProperty
    private String modifiedAt;
    @JsonProperty
    private Error error;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @NoArgsConstructor
    public static class ResponseTranscribe {

        @JsonProperty("@type")
        private String type;
        @JsonProperty("chunks")
        private List<ChunksList> chunk;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @NoArgsConstructor
    public static class ChunksList {

        @JsonProperty("alternatives")
        private List<AlternativesList> alternative;

        @JsonProperty()
        private String channelTag;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @NoArgsConstructor
    public static class AlternativesList {

        @JsonProperty()
        private List<WordsList> word;
        @JsonProperty
        private String text;
        @JsonProperty
        private Integer confidence;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @NoArgsConstructor
    public static class WordsList {

        @JsonProperty
        private String startTime;
        @JsonProperty
        private String endTime;
        @JsonProperty
        private String word;
        @JsonProperty
        private Integer confidence;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @NoArgsConstructor
    public static class Error {
        @JsonProperty
        private Integer code;
        @JsonProperty
        private String message;
    }
}

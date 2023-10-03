package com.audiorecognizer.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class LongRunningRecognizeResponse {

    @JsonProperty
    private boolean done;
    @JsonProperty
    private String id;
    @JsonProperty
    private String createdAt;
    @JsonProperty
    private String createdBy;
    @JsonProperty
    private String modifiedAt;

}

package com.audiorecognizer.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@NoArgsConstructor
public class GetAudioControllerResponse {

    @JsonProperty
    private boolean done;
    @JsonProperty
    private String id;
    @JsonProperty
    private String message;
    @JsonProperty
    private String descriptionError;

}

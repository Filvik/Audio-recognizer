package com.audiorecognizer.backend.model;

import lombok.Data;

import java.io.File;
import java.net.URL;

@Data
public class TaskTranscribe {
    String taskId;
    private File file;
    private TaskConditionEnum taskConditionEnum;
    private String extension;
    private URL url;
    private LongRunningRecognizeResponse longRunningRecognizeResponse;
    private String errorDescription;
    private String resultMessage;
    private boolean isRecord;

}

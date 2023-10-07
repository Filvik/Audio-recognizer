package com.audiorecognizer.backend.model;

import lombok.Data;

import java.io.File;
import java.net.URL;

@Data
public class TaskTranscribe {
    private String taskId;
    private File file;
    private TaskConditionEnum taskConditionEnum;
    private String extension;
    private URL url;
    private LongRunningRecognizeResponse longRunningRecognizeResponse;
    private String errorDescription;
    private String resultMessage;
    private boolean isRecord;
    private boolean sourceApi;


    public String getAudioEncoding(){
        if (extension.equals("mp3")) {
            return "MP3";
        } else if (extension.equals("wav")) {
            return "LINEAR16_PCM";
        } else {
            throw new RuntimeException("Неподдерживаемый формат аудио!");
        }
    }
}

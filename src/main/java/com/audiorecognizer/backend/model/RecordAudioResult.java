package com.audiorecognizer.backend.model;

import lombok.Data;

import java.io.File;
@Data
public class RecordAudioResult {

    private String errorDescription;
    private File file;
    private boolean status;

    public RecordAudioResult(String errorDescription) {
        this.errorDescription = errorDescription;
        status = false;
    }

    public RecordAudioResult(File file) {
        this.file = file;
        status = true;
    }
}

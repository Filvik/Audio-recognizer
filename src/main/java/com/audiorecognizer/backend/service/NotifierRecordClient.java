package com.audiorecognizer.backend.service;

import com.audiorecognizer.backend.model.TaskTranscribe;

public interface NotifierRecordClient {
    void getRecordResult(TaskTranscribe taskTranscribe);
}

package com.audiorecognizer.backend.service;

import com.audiorecognizer.backend.model.RecordAudioResult;

public interface NotifierRecordClient {
    void getRecordResult(RecordAudioResult recordAudioResult);
}

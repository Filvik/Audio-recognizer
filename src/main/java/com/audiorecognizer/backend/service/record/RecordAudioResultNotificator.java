package com.audiorecognizer.backend.service.record;

import com.audiorecognizer.backend.model.RecordAudioResult;
import com.audiorecognizer.backend.service.NotifierRecordClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RecordAudioResultNotificator {
    private final List<NotifierRecordClient> clients = new ArrayList<>();

    public void addClient(NotifierRecordClient client){
        clients.add(client);
    }

    public void sendRecordAudioResult(RecordAudioResult recordAudioResult){
        clients.forEach(s -> s.getRecordResult(recordAudioResult));
    }
}

package com.audiorecognizer.backend.service.ui;


import com.audiorecognizer.backend.form.FormUI;
import com.audiorecognizer.backend.service.record.RecordAudioResultNotificator;
import com.audiorecognizer.backend.service.record.RecordAudioService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "application", name = "ui-visible")
public class UiService {

    private final FormUI formUI;
    private final RecordAudioResultNotificator audioResultNotificator;

    public UiService(RecordAudioService recordAudioService, RecordAudioResultNotificator audioResultNotificator) {
        this.audioResultNotificator = audioResultNotificator;
        System.setProperty("java.awt.headless", "false");
        formUI = new FormUI();
        RecordAudioEvenListener recordAudioEvenListener = new RecordAudioEvenListener(formUI.getStartRecordButton(), recordAudioService, formUI.getLabel());
        // подписываемся на события нажатия кнопки
        formUI.getStartRecordButton().addActionListener(recordAudioEvenListener);
        // подписываемся на уведомления с результатами записи
        this.audioResultNotificator.addClient(recordAudioEvenListener);
        formUI.setVisible(true);

    }
}

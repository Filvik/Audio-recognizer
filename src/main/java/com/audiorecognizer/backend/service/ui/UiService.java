package com.audiorecognizer.backend.service.ui;


import com.audiorecognizer.backend.form.FormUI;
import com.audiorecognizer.backend.service.transcribe.TaskService;
import com.audiorecognizer.backend.service.record.RecordAudioResultNotificator;
import com.audiorecognizer.backend.service.record.RecordAudioService;
import com.audiorecognizer.backend.service.transcribe.TranscribeService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "application", name = "ui-visible")
public class UiService {

    private final FormUI formUI;
    private final RecordAudioResultNotificator audioResultNotificator;
    private final TranscribeService transcribeService;
    private final TaskService taskService;

    public UiService(RecordAudioService recordAudioService, RecordAudioResultNotificator audioResultNotificator, TranscribeService transcribeService, TaskService taskService) {
        this.audioResultNotificator = audioResultNotificator;
        this.transcribeService = transcribeService;
        this.taskService = taskService;
        System.setProperty("java.awt.headless", "false");
        formUI = new FormUI();
        RecordAudioEvenListener recordAudioEvenListener = new RecordAudioEvenListener(
                formUI.getStartRecordButton(),
                recordAudioService, formUI.getLabelInfo(),
                formUI.getjTextAreaForResponse(),
                formUI.getLoadingButton());
        OpenFile openFile = new OpenFile(formUI.getLoadingButton(), formUI.getStartRecordButton(), formUI, this.transcribeService, this.taskService, formUI.getLabelInfo());
        // подписываемся на события нажатия кнопки
        formUI.getStartRecordButton().addActionListener(recordAudioEvenListener);
        formUI.getLoadingButton().addActionListener(openFile);
        // подписываемся на уведомления с результатами записи
        this.audioResultNotificator.addClient(recordAudioEvenListener);
        formUI.setVisible(true);

    }
}

package com.audiorecognizer.backend.service.ui;

import com.audiorecognizer.backend.model.RecordAudioResult;
import com.audiorecognizer.backend.service.NotifierRecordClient;
import com.audiorecognizer.backend.service.RecordAudioService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.audiorecognizer.backend.form.FormUI.BUTTON_NAME_START;
import static com.audiorecognizer.backend.form.FormUI.BUTTON_NAME_STOP;

class RecordAudioEvenListener implements ActionListener, NotifierRecordClient {

    private final JButton startRecord;
    private final RecordAudioService recordAudioService;
    private final JLabel label;
    public final static String TEXT_IN_CONSUL_STOP = "Идёт запись! Нажмите, чтобы остановить!";
    public final static String TEXT_IN_CONSUL_START = "Нажмите, чтобы начать запись!";

    RecordAudioEvenListener(JButton startRecord, RecordAudioService recordAudioService, JLabel label) {
        this.startRecord = startRecord;
        this.recordAudioService = recordAudioService;
        this.label = label;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (startRecord.isDefaultCapable()) {
            switch (startRecord.getText()) {
                case BUTTON_NAME_START -> clickStart();
                case BUTTON_NAME_STOP -> clickStop();
            }
        }
    }

    @Override
    public void getRecordResult(RecordAudioResult recordAudioResult){
        if (startRecord.getText().equals(BUTTON_NAME_STOP) && !recordAudioResult.isStatus()){
            clickStop();
            label.setForeground(Color.RED);
            label.setText(recordAudioResult.getErrorDescription());
        }
    }

    private void clickStart() {
        if (recordAudioService.startRecording().isStatus()) {
            startRecord.setText(BUTTON_NAME_STOP);
            label.setForeground(Color.BLACK);
            label.setText(TEXT_IN_CONSUL_STOP);
            startRecord.setToolTipText(TEXT_IN_CONSUL_STOP);
        }
    }

    private void clickStop() {
        recordAudioService.stopRecording();
        startRecord.setText(BUTTON_NAME_START);
        label.setForeground(Color.BLACK);
        label.setText(TEXT_IN_CONSUL_START);
        startRecord.setToolTipText(TEXT_IN_CONSUL_START);
    }
}

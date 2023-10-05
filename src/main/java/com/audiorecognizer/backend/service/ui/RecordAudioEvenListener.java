package com.audiorecognizer.backend.service.ui;

import com.audiorecognizer.backend.model.TaskTranscribe;
import com.audiorecognizer.backend.service.NotifierRecordClient;
import com.audiorecognizer.backend.service.record.RecordAudioService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.audiorecognizer.backend.form.FormUI.*;

class RecordAudioEvenListener implements ActionListener, NotifierRecordClient {

    private final JButton startRecord;
    private final RecordAudioService recordAudioService;
    private final JLabel label;
    private final JTextArea labelForResponse;
    private final JButton loadingButton;
    public final static String TEXT_IN_CONSUL_STOP = "Идёт запись! Нажмите, чтобы остановить!";
    public final static String TEXT_IN_CONSUL_START = "Нажмите, чтобы начать запись!";
    public final static String TEXT_IN_CONSUL_WAIT = "Ожидание ответа!";

    RecordAudioEvenListener(JButton startRecord, RecordAudioService recordAudioService, JLabel label, JTextArea labelForResponse, JButton loadingButton) {
        this.startRecord = startRecord;
        this.recordAudioService = recordAudioService;
        this.label = label;
        this.labelForResponse = labelForResponse;
        this.loadingButton = loadingButton;
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
    public void getRecordResult(TaskTranscribe taskTranscribe) {
        switch (taskTranscribe.getTaskConditionEnum()) {

            case NEW -> {
            }
            case RECORD_STARTED -> {
            }
            case RECORD_COMPLETED -> {
                label.setText(taskTranscribe.getTaskConditionEnum().getDescription());
                break;
            }
            case RECORD_ERROR, SEND_TRANSCRIBE_ERROR, TRANSCRIBE_ERROR, FORMAT_ERROR -> {
                startValue();
                label.setForeground(Color.RED);
                label.setText(getErrorText(taskTranscribe));
                getSettings1();
                break;
            }
            case CLOUD_SENDING -> {
            }
            case CLOUD_UPLOAD -> {
            }
            case TRANSCRIBE_SENDING -> {
            }
            case COMPLETED -> {
                labelForResponse.setText(taskTranscribe.getResultMessage());
                label.setForeground(Color.BLACK);
                label.setText(TEXT_IN_CONSUL_START);
                getSettings1();
                getSettings2();
            }
        }
    }

    private void startValue() {
        getSettings2();
        startRecord.setEnabled(true);
    }

    private void clickStart() {
        if (recordAudioService.startRecording().isStatus()) {
            getSettings3();
            if (!labelForResponse.equals(TEXT_IN_CONSUL_FOR_RESPONSE)) {
                labelForResponse.setText(TEXT_IN_CONSUL_FOR_RESPONSE);
            }
        }
    }

    private void clickStop() {
        recordAudioService.stopRecording();
        startRecord.setEnabled(false);
        startRecord.setText(TEXT_IN_CONSUL_WAIT);
    }

    private String getErrorText(TaskTranscribe taskTranscribe) {
        return taskTranscribe.getErrorDescription() != null && !taskTranscribe.getErrorDescription().isEmpty() ?
                taskTranscribe.getTaskConditionEnum().getDescription() + ": " + taskTranscribe.getErrorDescription() :
                taskTranscribe.getTaskConditionEnum().getDescription();
    }

    private void getSettings1() {
        startRecord.setEnabled(true);
        loadingButton.setEnabled(true);
        loadingButton.setText(BUTTON_NAME_LOADING);
    }

    private void getSettings2() {
        startRecord.setText(BUTTON_NAME_START);
        startRecord.setToolTipText(TEXT_IN_CONSUL_START);
    }

    private void getSettings3() {
        loadingButton.setEnabled(false);
        startRecord.setText(BUTTON_NAME_STOP);
        label.setForeground(Color.BLACK);
        label.setText(TEXT_IN_CONSUL_STOP);
        startRecord.setToolTipText(TEXT_IN_CONSUL_STOP);
    }
}

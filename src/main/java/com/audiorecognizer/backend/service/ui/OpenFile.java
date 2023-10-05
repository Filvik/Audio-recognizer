package com.audiorecognizer.backend.service.ui;

import com.audiorecognizer.backend.form.FormUI;
import com.audiorecognizer.backend.model.TaskTranscribe;
import com.audiorecognizer.backend.service.transcribe.TaskService;
import com.audiorecognizer.backend.service.transcribe.TranscribeService;
import com.google.common.io.Files;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static com.audiorecognizer.backend.form.FormUI.BUTTON_NAME_LOADING;
import static com.audiorecognizer.backend.form.FormUI.TEXT_IN_CONSUL;
import static com.audiorecognizer.backend.service.ui.RecordAudioEvenListener.TEXT_IN_CONSUL_WAIT;

public class OpenFile implements ActionListener {

    private final JButton loadingButton;
    private final JButton startRecord;
    private final FormUI formUI;
    private final Mp3Filter filterMp3 = new Mp3Filter("mp3", "MPEG Audio Layer III");
    private final Mp3Filter filterWave = new Mp3Filter("wav", "Waveform Audio File Format");
    private final TranscribeService transcribeService;
    private final TaskService taskService;
    private final JLabel labelInfo;

    static class Mp3Filter extends FileFilter {

        private final String EXTENSION;
        private final String DESCRIPTION;

        public Mp3Filter(String extension, String description) {
            EXTENSION = extension;
            DESCRIPTION = description;
        }

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            return f.getName().toLowerCase().endsWith(EXTENSION);
        }

        @Override
        public String getDescription() {
            return DESCRIPTION + String.format(" *%s", EXTENSION);
        }
    }

    public OpenFile(JButton loadingButton, JButton startRecord, FormUI formUI, TranscribeService transcribeService, TaskService taskService, JLabel labelInfo) {
        this.loadingButton = loadingButton;
        this.startRecord = startRecord;
        this.formUI = formUI;
        this.transcribeService = transcribeService;
        this.taskService = taskService;
        this.labelInfo = labelInfo;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (loadingButton.isDefaultCapable()) {
            getSettingsBefore();
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileFilter(filterMp3);
            jFileChooser.addChoosableFileFilter(filterWave);
            jFileChooser.showOpenDialog(formUI);
            File file = jFileChooser.getSelectedFile();
            if (file != null) {
                TaskTranscribe taskTranscribe = taskService.addNewTask(Files.getFileExtension(file.getAbsolutePath()), false);
                taskTranscribe.setFile(file);
                transcribeService.downloadAudioOnBucket(taskTranscribe);
            } else {
             getSettingsAfter();
            }
        }
    }

    private void getSettingsBefore() {
        startRecord.setEnabled(false);
        loadingButton.setEnabled(false);
        loadingButton.setText(TEXT_IN_CONSUL_WAIT);
        labelInfo.setForeground(Color.BLACK);
        labelInfo.setText("...");
    }
    private void getSettingsAfter() {
        startRecord.setEnabled(true);
        loadingButton.setEnabled(true);
        loadingButton.setText(BUTTON_NAME_LOADING);
        labelInfo.setText(TEXT_IN_CONSUL);
    }
}

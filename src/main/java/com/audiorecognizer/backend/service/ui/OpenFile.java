package com.audiorecognizer.backend.service.ui;

import com.audiorecognizer.backend.form.FormUI;
import com.audiorecognizer.backend.model.TaskTranscribe;
import com.audiorecognizer.backend.service.transcribe.TaskService;
import com.audiorecognizer.backend.service.transcribe.TranscribeService;
import com.google.common.io.Files;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class OpenFile implements ActionListener {

    private final JButton loadingButton;
    private final FormUI formUI;
    private final Mp3Filter filter = new Mp3Filter();
    private final TranscribeService transcribeService;
    private final TaskService taskService;

    static class Mp3Filter extends FileFilter {

        private final static String EXTENSION = "mp3";
        private final static String DESCRIPTION = "music file";

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

    public OpenFile(JButton loadingButton, FormUI formUI, TranscribeService transcribeService, TaskService taskService) {
        this.loadingButton = loadingButton;
        this.formUI = formUI;
        this.transcribeService = transcribeService;
        this.taskService = taskService;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (loadingButton.isDefaultCapable()) {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileFilter(filter);
            jFileChooser.showOpenDialog(formUI);
            File file = jFileChooser.getSelectedFile();
            TaskTranscribe taskTranscribe = taskService.addNewTask(Files.getFileExtension(file.getAbsolutePath()), false);
            taskTranscribe.setFile(file);
            transcribeService.downloadAudioOnBucket(taskTranscribe);
        }
    }

}

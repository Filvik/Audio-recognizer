package com.audiorecognizer.backend.service;


import com.audiorecognizer.backend.form.FormUI;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "application", name = "ui-visible")
public class UiService {
    private final FormUI formUI;

    public UiService() {
        System.setProperty("java.awt.headless", "false");
        formUI = new FormUI();
        formUI.setVisible(true);

    }
}

package com.audiorecognizer.backend.form;

import org.springframework.stereotype.Component;

import java.awt.*;

import javax.swing.*;
public class FormUI extends JFrame {

    public final static String BUTTON_NAME_START = "Start";
    public final static String BUTTON_NAME_STOP= "Stop";
    public final static String TEXT_IN_CONSUL = "Терминал готов к работе!";

    private final JButton startRecordButton = new JButton(BUTTON_NAME_START);

    private final JLabel label = new JLabel(TEXT_IN_CONSUL, SwingConstants.CENTER);

    public FormUI() {
        super("Form for record");
        this.setBounds(500, 300, 500, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container container = this.getContentPane();
        container.setLayout(new GridLayout(2, 2, 2, 2));
        startRecordButton.setToolTipText("Нажмите, чтобы начать запись!");
        container.add(startRecordButton);
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        label.setForeground(Color.BLACK);
        container.add(label);
    }

    public JButton getStartRecordButton() {
        return startRecordButton;
    }
    public JLabel getLabel() {
        return label;
    }
}

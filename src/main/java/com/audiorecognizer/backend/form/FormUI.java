package com.audiorecognizer.backend.form;

import java.awt.*;

import javax.swing.*;
public class FormUI extends JFrame {

    public final static String BUTTON_NAME_START = "Start";
    public final static String BUTTON_NAME_STOP= "Stop";
    public final static String BUTTON_NAME_LOADING= "Open";
    public final static String TEXT_IN_CONSUL = "Терминал готов к работе!";
    public final static String TEXT_IN_CONSUL_FOR_RESPONSE = "Здесь будет ответ!";

    private final JButton startRecordButton = new JButton(BUTTON_NAME_START);

    private final JButton loadingButton = new JButton(BUTTON_NAME_LOADING);

    private final JLabel labelInfo = new JLabel(TEXT_IN_CONSUL, SwingConstants.CENTER);

    private final JLabel labelForResponse = new JLabel(TEXT_IN_CONSUL_FOR_RESPONSE, SwingConstants.CENTER);


    public FormUI() {
        super("Form for record");
        this.setBounds(500, 300, 500, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container container = this.getContentPane();
        JPanel jPanel = new JPanel(new GridLayout(3, 1, 2, 2));
        JPanel jPanel1 = new JPanel(new GridLayout(1, 2, 2, 2));
        jPanel1.add(startRecordButton);
        jPanel1.add(loadingButton);
        jPanel.add(jPanel1);
        jPanel.add(labelInfo);
        jPanel.add(labelForResponse);
        container.add(jPanel);
        labelInfo.setOpaque(true);
        labelInfo.setBackground(Color.WHITE);
        labelInfo.setForeground(Color.BLACK);
    }

    public JButton getStartRecordButton() {
        return startRecordButton;
    }
    public JLabel getLabelInfo() {
        return labelInfo;
    }
    public JLabel getLabelForResponse() {
        return labelForResponse;
    }
    public JButton getLoadingButton() {
        return loadingButton;
    }
}

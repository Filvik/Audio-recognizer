package com.audiorecognizer.backend.form;

import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.event.*;
import java.util.Objects;
import javax.swing.*;
public class FormUI extends JFrame {

    private final JButton startRecord = new JButton("Start");

    public FormUI() {
        super("Form for record");
        this.setBounds(500, 300, 500, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container container = this.getContentPane();
        container.setLayout(new GridLayout(1, 1, 1, 1));
        startRecord.addActionListener(new ButtonEvenListener());
        startRecord.setToolTipText("Нажмите, чтобы начать запись!");
        container.add(startRecord);
    }

    class ButtonEvenListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (startRecord.isDefaultCapable()) {
                if (Objects.equals(startRecord.getText(), "Stop")) {
                    startRecord.setText("Start");
                    startRecord.setToolTipText("Нажмите, чтобы начать запись!");
                } else if (Objects.equals(startRecord.getText(), "Start")) {
                    startRecord.setText("Stop");
                    startRecord.setToolTipText("Идёт запись! Нажмите, чтобы остановить!");
                }
            }
        }
    }
}

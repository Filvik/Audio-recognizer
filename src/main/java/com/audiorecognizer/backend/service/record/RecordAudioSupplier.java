package com.audiorecognizer.backend.service.record;

import com.audiorecognizer.backend.config.RecordAudioProperty;
import com.audiorecognizer.backend.model.TaskConditionEnum;
import com.audiorecognizer.backend.model.TaskTranscribe;
import lombok.AllArgsConstructor;
import javax.sound.sampled.*;
import java.io.IOException;
import java.util.function.Supplier;

@AllArgsConstructor
public class RecordAudioSupplier implements Supplier<TaskTranscribe> {

    private final AudioFormat format;
    private  TargetDataLine microphone;
    private final TaskTranscribe taskTranscribe;
    private final RecordAudioProperty recordAudioProperty;
    private final DataLine.Info info;

    @Override
    public TaskTranscribe get() {
        if (!AudioSystem.isLineSupported(info)) {
            taskTranscribe.setTaskConditionEnum(TaskConditionEnum.RECORD_ERROR);
            taskTranscribe.setErrorDescription("Линия не поддерживается!");
           return taskTranscribe;
        }
        try {
            taskTranscribe.setTaskConditionEnum(TaskConditionEnum.RECORD_STARTED);
            // открываем линию соединения с указанным форматом и размером буфера
            microphone.open(format, microphone.getBufferSize());
            // поток микрофона
            AudioInputStream sound = new AudioInputStream(microphone);
            // запустить линию соединения
            microphone.start();
            // записать содержимое потока в файл
            AudioSystem.write(sound, recordAudioProperty.getFileType(), taskTranscribe.getFile());

        } catch (LineUnavailableException ex) {
            taskTranscribe.setTaskConditionEnum(TaskConditionEnum.RECORD_ERROR);
            taskTranscribe.setErrorDescription("Линия не доступна!");
            return taskTranscribe;
        } catch (IOException ex) {
            taskTranscribe.setTaskConditionEnum(TaskConditionEnum.RECORD_ERROR);
            taskTranscribe.setErrorDescription("Ошибка ввода параметров!");
            return taskTranscribe;
        }
        taskTranscribe.setTaskConditionEnum(TaskConditionEnum.RECORD_COMPLETED);
        return taskTranscribe;
    }
}

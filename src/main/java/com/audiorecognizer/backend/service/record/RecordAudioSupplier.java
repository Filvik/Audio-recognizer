package com.audiorecognizer.backend.service.record;

import com.audiorecognizer.backend.config.RecordAudioProperty;
import com.audiorecognizer.backend.model.RecordAudioResult;
import lombok.AllArgsConstructor;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

@AllArgsConstructor
public class RecordAudioSupplier implements Supplier<RecordAudioResult> {

    private final AudioFormat format;
    private  TargetDataLine microphone;
    private final File file;
    private final RecordAudioProperty recordAudioProperty;
    private final DataLine.Info info;

    @Override
    public RecordAudioResult get() {
        if (!AudioSystem.isLineSupported(info)) {
           return new RecordAudioResult("Линия не поддерживается!");
        }
        try {
            // открываем линию соединения с указанным форматом и размером буфера
            microphone.open(format, microphone.getBufferSize());
            // поток микрофона
            AudioInputStream sound = new AudioInputStream(microphone);
            // запустить линию соединения
            microphone.start();
            // записать содержимое потока в файл
            AudioSystem.write(sound, recordAudioProperty.getFileType(), file);

        } catch (LineUnavailableException ex) {
            return new RecordAudioResult("Линия не доступна!");
        } catch (IOException ex) {
            return new RecordAudioResult("Ошибка ввода параметров!");
        }
        return new RecordAudioResult(file);
    }
}

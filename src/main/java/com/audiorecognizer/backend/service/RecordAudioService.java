package com.audiorecognizer.backend.service;

import com.audiorecognizer.backend.config.RecordAudioProperty;
import com.audiorecognizer.backend.model.RecordAudioResult;
import com.audiorecognizer.backend.service.record.RecordAudioSupplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.File;
import java.util.concurrent.*;

@Service
public class RecordAudioService {

    private final RecordAudioProperty recordAudioProperty;
    private final RecordAudioResultNotificator recordAudioResultNotificator;

    // номер файла
    private int suffix = 0;
    // определение формата аудио данных
    private final AudioFormat format;
    // микрофонный вход
    private TargetDataLine microphone;

    private static final Logger log = LogManager.getLogger(RecordAudioService.class);

    private boolean flagRecord;

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    public RecordAudioService(RecordAudioProperty recordAudioProperty, RecordAudioResultNotificator recordAudioResultNotificator) {
        this.recordAudioProperty = recordAudioProperty;
        this.recordAudioResultNotificator = recordAudioResultNotificator;
        format = new AudioFormat(
                recordAudioProperty.getAudioFormat(),
                recordAudioProperty.getSampleRate(),
                recordAudioProperty.getSampleSizeInBits(),
                recordAudioProperty.getMONO(),
                recordAudioProperty.getFrameSize(),
                recordAudioProperty.getFrameRate(),
                recordAudioProperty.isBigEndian());
    }

    /**
     * Создание нового файла
     * @return созданный файл
     */
    private File getNewFile() {
        String soundFileName = "";
        try {
            soundFileName = recordAudioProperty.getFileName() + (suffix++) + "." + recordAudioProperty.getFileType().getExtension();
            return new File(soundFileName);
        } catch (Exception exception) {
            log.error("Ошибка создания аудио файла " + soundFileName + "/n" + exception);
            throw new RuntimeException();
        }
    }

    /**
     * Запуск записи аудио
     * @return результат записи аудио
     */
    public RecordAudioResult startRecording() {
        if (flagRecord) {
            return new RecordAudioResult("Идёт запись файла! Не возможно запустить ещё одну запись!");
        }
        flagRecord = true;
        try {
            // получить подходящую линию
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            RecordAudioSupplier recordAudioSupplier = new RecordAudioSupplier(format, microphone, getNewFile(), recordAudioProperty,info);

            CompletableFuture.supplyAsync(recordAudioSupplier, executorService)
                    .thenAccept(recordAudioResultNotificator::sendRecordAudioResult)
                    .exceptionally(e -> {
                        log.error(e.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new RecordAudioResult(getNewFile());
    }

    /**
     * Остановка записи
     */
    public void stopRecording() {
        microphone.stop();
        microphone.close();
        flagRecord = false;
    }

}

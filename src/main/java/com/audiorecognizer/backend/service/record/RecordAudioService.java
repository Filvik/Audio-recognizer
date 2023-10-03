package com.audiorecognizer.backend.service.record;

import com.audiorecognizer.backend.config.RecordAudioProperty;
import com.audiorecognizer.backend.model.RecordAudioResult;
import com.audiorecognizer.backend.model.TaskTranscribe;
import com.audiorecognizer.backend.service.GenerateNameService;
import com.audiorecognizer.backend.service.TaskService;
import com.audiorecognizer.backend.service.transcribe.TranscribeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.util.concurrent.*;

@Service
public class RecordAudioService {
    private static final Logger log = LogManager.getLogger(RecordAudioService.class);

    private final RecordAudioProperty recordAudioProperty;
    private final RecordAudioResultNotificator recordAudioResultNotificator;
    private final TaskService taskService;
    private final GenerateNameService generateNameService;
    private final TranscribeService transcribeService;

    // определение формата аудио данных
    private final AudioFormat format;
    // микрофонный вход
    private TargetDataLine microphone;
    private boolean flagRecord;
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    public RecordAudioService(RecordAudioProperty recordAudioProperty, RecordAudioResultNotificator recordAudioResultNotificator,
                              TaskService taskService, GenerateNameService generateNameService, TranscribeService transcribeService) {
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
        this.taskService = taskService;
        this.generateNameService = generateNameService;
        this.transcribeService = transcribeService;
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
        TaskTranscribe taskTranscribe = taskService.addNewTask(recordAudioProperty.getFileType().getExtension());
        generateNameService.createFileInTaskTranscribe(recordAudioProperty.getFileName(), taskTranscribe);
        try {
            // получить подходящую линию
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            RecordAudioSupplier recordAudioSupplier = new RecordAudioSupplier(format, microphone, taskTranscribe, recordAudioProperty,info);

            CompletableFuture.supplyAsync(recordAudioSupplier, executorService)
                    .thenAccept(task -> {
                        recordAudioResultNotificator.sendRecordAudioResult(task);
                        transcribeService.downloadAudioOnBucket(task);
                    }
                    )
                    .exceptionally(e -> {
                        log.error(e.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new RecordAudioResult(taskTranscribe.getFile());
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

package com.audiorecognizer.backend.service;

import com.audiorecognizer.backend.model.TaskTranscribe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class GenerateNameService {
    private final Logger log = LogManager.getLogger(this.getClass());

    private final int INDEX_MIN = 100;
    private final int INDEX_MAX = 999;
    private final AtomicInteger index = new AtomicInteger(INDEX_MIN);


    public String getName(){
        if (index.get()>=INDEX_MAX)
            index.set(INDEX_MIN);
        return System.currentTimeMillis() + "_" + index.incrementAndGet();
    }

    /**
     * Создание нового файла
     */
    public void createFileInTaskTranscribe(String filePrefix, TaskTranscribe taskTranscribe) {
        String soundFileName = "";
        try {
            soundFileName = filePrefix + taskTranscribe.getTaskId() + "." + taskTranscribe.getExtension();
            taskTranscribe.setFile(new File(soundFileName));
        } catch (Exception exception) {
            log.error("Ошибка создания аудио файла " + soundFileName + "/n" + exception);
            throw new RuntimeException();
        }
    }
}

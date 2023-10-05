package com.audiorecognizer.backend.model;

import java.util.HashSet;
import java.util.Set;

public enum TaskConditionEnum {
    NEW("Задание на распознавание создано"),
    RECORD_STARTED("Идет запись"),
    RECORD_COMPLETED("Запись завершена"),
    RECORD_ERROR("Ошибка при записи аудио"),
    CLOUD_SENDING("Отправка аудио в storage"),
    CLOUD_UPLOAD("Аудио загружено в storage"),
    TRANSCRIBE_SENDING("Аудио отправлено на распознавание"),
    SEND_TRANSCRIBE_ERROR("Ошибка при отправке аудио на распознавание"),
    FORMAT_ERROR("Ошибка выбора формата аудио"),
    TRANSCRIBE_ERROR("Ошибка при распознавание аудио"),
    COMPLETED("Распознавание завершено");

    private final String description;

    public final static Set<TaskConditionEnum> COMPLETED_TASK = new HashSet<>(){{
        add(RECORD_ERROR);
        add(SEND_TRANSCRIBE_ERROR);
        add(TRANSCRIBE_ERROR);
        add(COMPLETED);
        add(FORMAT_ERROR);
    }};

    TaskConditionEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

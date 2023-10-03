package com.audiorecognizer.backend.model;

public enum TaskConditionEnum {
    NEW("Задание на распознавание создано"),
    RECORD_STARTED("Идет запись"),
    RECORD_COMPLETED("Запись завершена"),
    RECORD_ERROR("Ошибка при записи аудио"),
    CLOUD_SENDING("Отправка аудио в storage"),
    CLOUD_UPLOAD("Аудио загружено в storage"),
    TRANSCRIBE_SENDING("Аудио отправлено на распознавание"),
    SEND_TRANSCRIBE_ERROR("Ошибка при отправке аудио на распознавание"),
    TRANSCRIBE_ERROR("Ошибка при распознавание аудио"),
    COMPLETED("Распознавание завершено");

    private final String description;

    TaskConditionEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

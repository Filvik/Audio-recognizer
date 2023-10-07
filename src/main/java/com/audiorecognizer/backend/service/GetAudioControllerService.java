package com.audiorecognizer.backend.service;

import com.audiorecognizer.backend.controller.DownloadAudioController;
import com.audiorecognizer.backend.model.GetAudioControllerResponse;
import com.audiorecognizer.backend.model.OperationStatusResponse;
import com.audiorecognizer.backend.model.TaskTranscribe;
import com.audiorecognizer.backend.service.transcribe.TaskService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class GetAudioControllerService {

    private static final Logger log = LogManager.getLogger(DownloadAudioController.class);
    private final TaskService taskService;

    public GetAudioControllerResponse getAnswer(String id) {
        GetAudioControllerResponse getAudioControllerResponse = new GetAudioControllerResponse();
        try {
            TaskTranscribe response = taskService.getResponse(id);
            getAudioControllerResponse.setId(id);
            getAudioControllerResponse.setDone(true);
            getAudioControllerResponse.setMessage(response.getResultMessage());
            response.setSourceApi(false);
        } catch (RuntimeException exception) {
            getAudioControllerResponse.setDone(false);
            getAudioControllerResponse.setDescriptionError("Задача с данным id не найдена!");
            log.info("Задача с данным id не найдена!");
            return getAudioControllerResponse;
        }
        return getAudioControllerResponse;
    }
}


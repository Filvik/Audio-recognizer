package com.audiorecognizer.backend.controller;

import com.audiorecognizer.backend.model.GetAudioControllerResponse;
import com.audiorecognizer.backend.model.SendAudioControllerResponse;
import com.audiorecognizer.backend.service.GetAudioControllerService;
import com.audiorecognizer.backend.service.SendAudioControllerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class DownloadAudioController {

    private static final Logger log = LogManager.getLogger(DownloadAudioController.class);
    private final GetAudioControllerService getAudioControllerService;
    private final SendAudioControllerService sendAudioControllerService;

    @PostMapping(value = "/sendAudio/")
    @Operation(summary = "Отправка аудио на распознавание", tags = "Контроллер для отправка аудио")
    public SendAudioControllerResponse sendAudio(@RequestPart ("file") @Parameter(description = "Аудио файл") MultipartFile file) {
        log.info("Вызван метод sendAudio");
        if (file != null){
            return sendAudioControllerService.sendAudio(file);
        }
        else {
            SendAudioControllerResponse send = new SendAudioControllerResponse();
            send.setDone(false);
            send.setDescriptionError("Файл является пустым!");
            return send;
        }
    }

    @GetMapping(value = "/getAnswer/{id}")
    @Operation(summary = "Получение ответа распознанного аудио", tags = "Контроллер получение ответа")
    public GetAudioControllerResponse getAnswer(@PathVariable @Parameter(description = "Идентификатор задачи на распознавание") String id) {
        if(id != null){
            log.info("Вызван метод getAnswer с id = " + id);
            return getAudioControllerService.getAnswer(id);
        }
        else {
          GetAudioControllerResponse response = new GetAudioControllerResponse();
          response.setDone(false);
          response.setDescriptionError("Идентификатор задачи задан не корректно!");
          return response;
        }
    }
}

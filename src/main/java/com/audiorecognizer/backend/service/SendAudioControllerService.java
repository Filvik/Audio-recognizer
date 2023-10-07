package com.audiorecognizer.backend.service;

import com.audiorecognizer.backend.model.GetAudioControllerResponse;
import com.audiorecognizer.backend.model.SendAudioControllerResponse;
import com.audiorecognizer.backend.model.TaskConditionEnum;
import com.audiorecognizer.backend.model.TaskTranscribe;
import com.audiorecognizer.backend.service.transcribe.TaskService;
import com.audiorecognizer.backend.service.transcribe.TranscribeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SendAudioControllerService {

    private final TranscribeService transcribeService;
    private final TaskService taskService;

    public SendAudioControllerResponse sendAudio(MultipartFile file) {

        SendAudioControllerResponse response = new SendAudioControllerResponse();

        TaskTranscribe taskTranscribe = taskService.addNewTask(getFileExtension(Objects.requireNonNull(file.getOriginalFilename())), true);
        taskTranscribe.setSourceApi(true);
        response.setId(taskTranscribe.getTaskId());
        File folder = new File("audio");
        if (!folder.exists()) {
            folder.mkdir();
        }
        String name = "audio/" + taskTranscribe.getTaskId() + "." + taskTranscribe.getExtension();
        File fileSave = new File(name);
        taskTranscribe.setFile(fileSave);
        byte[] bytes;

        try {
            bytes = file.getBytes();
            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(fileSave));
            stream.write(bytes);
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        transcribeService.downloadAudioOnBucket(taskTranscribe);
        if (!TaskConditionEnum.COMPLETED_TASK.contains(taskTranscribe.getTaskConditionEnum())
                || taskTranscribe.getTaskConditionEnum() == TaskConditionEnum.COMPLETED){
            response.setDone(true);
        }
        else {
            response.setDescriptionError(taskTranscribe.getTaskConditionEnum().getDescription());
        }
        return response;
    }

    private static String getFileExtension(String nameFile) {
        int index = nameFile.indexOf('.');
        return index == -1 ? null : nameFile.substring(index+1);
    }
}

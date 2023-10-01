package com.audiorecognizer.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

@Data
@Configuration
@ConfigurationProperties(prefix = "setting-record")
public class RecordAudioProperty {
    private int MONO;
    private AudioFormat.Encoding audioFormat;
    private float sampleRate;
    private int sampleSizeInBits;
    private int channels;
    private int frameSize;
    private float frameRate;
    private boolean bigEndian;
    private String fileTypeFormat;
    private AudioFileFormat.Type fileType;
    private String fileName;

    private AudioFileFormat.Type getConverterHelperForFileType(String s) {
        return switch (s) {
            case "WAVE" -> AudioFileFormat.Type.WAVE;
            case "AU" -> AudioFileFormat.Type.AU;
            case "AIFF" -> AudioFileFormat.Type.AIFF;
            case "AIFF-C" -> AudioFileFormat.Type.AIFC;
            case "SND" -> AudioFileFormat.Type.SND;
            default -> throw new IllegalStateException("Unexpected value: " + getFileTypeFormat());
        };
    }

    public AudioFileFormat.Type getFileType() {
       return getConverterHelperForFileType(fileTypeFormat);
    }
}

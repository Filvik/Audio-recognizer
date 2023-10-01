package com.audiorecognizer.backend;

import com.audiorecognizer.backend.config.RecordAudioProperty;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RecordAudioProperty.class)
public class AudioRecognizerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AudioRecognizerApplication.class, args);
	}

}

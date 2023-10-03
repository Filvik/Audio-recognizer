package com.audiorecognizer.backend;

import com.audiorecognizer.backend.config.RecordAudioProperty;
import com.audiorecognizer.backend.config.YandexCloudSettings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties({RecordAudioProperty.class, YandexCloudSettings.class})
@EnableScheduling
public class AudioRecognizerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AudioRecognizerApplication.class, args);
	}

}

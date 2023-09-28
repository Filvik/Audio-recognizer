package com.audiorecognizer.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("file:external.properties")
public class AppConfig {
}

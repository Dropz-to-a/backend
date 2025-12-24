package com.jobmanager.job_manager.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "apick")
public class ApickProperties {
    private String baseUrl;
    private String authKey;
}

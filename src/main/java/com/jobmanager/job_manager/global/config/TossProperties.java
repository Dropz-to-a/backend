package com.jobmanager.job_manager.global.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "toss")
public class TossProperties {
    private String baseUrl = "https://api.tosspayments.com/v1";
    private String secretKey; // 단일결제는 secretKey만 필요
}

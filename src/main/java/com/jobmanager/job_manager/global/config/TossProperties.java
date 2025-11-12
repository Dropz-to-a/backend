package com.jobmanager.job_manager.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "toss")
public class TossProperties {
    private String baseUrl = "https://api.tosspayments.com/v1";
    private String secretKey;  // Spring이 여기에 setter로 값 주입함
}

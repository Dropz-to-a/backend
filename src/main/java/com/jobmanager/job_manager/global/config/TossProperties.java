package com.jobmanager.job_manager.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "toss")
public class TossProperties {
    private String secretKey;   // 토스 시크릿키
    private String baseUrl;     // https://api.tosspayments.com
    private String successUrl;  // 예: https://localhost:5173/pay/success
    private String failUrl;     // 예: https://localhost:5173/pay/fail
}
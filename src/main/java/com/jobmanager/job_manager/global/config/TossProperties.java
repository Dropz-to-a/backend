package com.jobmanager.job_manager.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** application.yml 의 toss.* 매핑 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "toss")
public class TossProperties {
    private String baseUrl;   // https://api.tosspayments.com/v1
    private String clientKey; // 프론트 위젯용 공개키
    private String secretKey; // 서버 승인/취소 호출용 시크릿키
}


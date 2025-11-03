package com.jobmanager.job_manager.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/** 토스 API 호출 등에 사용할 RestClient 등록 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }
}

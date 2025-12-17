package com.jobmanager.job_manager.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (SpringBoot)")
                .build();
    }
}
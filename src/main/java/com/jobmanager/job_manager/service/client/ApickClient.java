package com.jobmanager.job_manager.service.client;

import com.jobmanager.job_manager.global.config.ApickProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ApickClient {

    private final ApickProperties properties;
    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> sendOneWon(
            String bankCode,
            String account,
            String holder
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + properties.getAuthKey());

        Map<String, Object> body = Map.of(
                "bank_code", bankCode,
                "account_number", account,
                "holder_name", holder
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        return restTemplate.postForObject(
                properties.getBaseUrl() + "/v1/account/onewon/request",
                request,
                Map.class
        );
    }

    public Map<String, Object> verifyOneWon(
            String bankCode,
            String account,
            String authCode
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + properties.getAuthKey());

        Map<String, Object> body = Map.of(
                "bank_code", bankCode,
                "account_number", account,
                "auth_code", authCode
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        return restTemplate.postForObject(
                properties.getBaseUrl() + "/v1/account/onewon/verify",
                request,
                Map.class
        );
    }
}

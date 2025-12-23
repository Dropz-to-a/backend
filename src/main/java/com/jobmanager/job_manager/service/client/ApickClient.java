package com.jobmanager.job_manager.service.client;

import com.jobmanager.job_manager.global.config.ApickProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * APICK 1원 인증 연동 클라이언트
 */
@Component
@RequiredArgsConstructor
public class ApickClient {

    private final ApickProperties properties;
    private final RestTemplate restTemplate = new RestTemplate();

    // 1원 송금
    public void sendOneWon(String bankCode, String account, String holder) {

        Map<String, Object> body = Map.of(
                "bank_code", bankCode,
                "account_number", account,
                "holder_name", holder
        );

        restTemplate.postForObject(
                properties.getBaseUrl() + "/v1/account/onewon/request",
                body,
                Void.class
        );
    }

    // 인증번호 검증
    public void verifyOneWon(String bankCode, String account, String code) {

        Map<String, Object> body = Map.of(
                "bank_code", bankCode,
                "account_number", account,
                "auth_code", code
        );

        restTemplate.postForObject(
                properties.getBaseUrl() + "/v1/account/onewon/verify",
                body,
                Void.class
        );
    }
}

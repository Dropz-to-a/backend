package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.global.config.TossProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * 토스 결제 서비스 (네 구조: service 패키지)
 * - RestClient + TossProperties만 사용
 * - DB 연동/저장 로직 없이 "승인 신호 중계"만 우선 구현
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RestClient restClient;          // global.config.RestClientConfig 에서 등록됨
    private final TossProperties tossProperties;  // global.config.TossProperties

    /**
     * 결제 승인: 서버→토스 승인 API 호출
     * @param paymentKey successUrl에서 전달된 키
     * @param orderId    서버가 발급한 주문 ID
     * @param amount     결제 금액(원)
     * @return           토스 승인 API 응답(JSON) 그대로 반환
     */
    public Map<String, Object> confirm(String paymentKey, String orderId, Long amount) {
        String url = tossProperties.getBaseUrl() + "/payments/confirm";

        // Basic {base64(secretKey:)} 헤더 구성
        String basic = Base64.getEncoder()
                .encodeToString((tossProperties.getSecretKey() + ":").getBytes(StandardCharsets.UTF_8));

        Map<String, Object> body = Map.of(
                "paymentKey", paymentKey,
                "orderId", orderId,
                "amount", amount
        );

        return restClient.post()
                .uri(url)
                .headers(h -> {
                    h.set(HttpHeaders.AUTHORIZATION, "Basic " + basic);
                    h.setContentType(MediaType.APPLICATION_JSON);
                })
                .body(body)
                .retrieve()
                .body(Map.class);
    }
}

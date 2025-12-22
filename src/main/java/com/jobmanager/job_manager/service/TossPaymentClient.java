package com.jobmanager.job_manager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobmanager.job_manager.dto.payment.ConfirmPaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.*;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TossPaymentClient {

    private final ObjectMapper objectMapper;

    @Value("${toss.secret-key}")
    private String secretKey;

    private String authorization() {
        return "Basic " + Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes());
    }

    public HttpResponse<String> requestConfirm(
            ConfirmPaymentRequest req
    ) throws Exception {

        String body = objectMapper.writeValueAsString(Map.of(
                "paymentKey", req.paymentKey(),
                "orderId", req.orderId(),
                "amount", req.amount()
        ));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
                .header("Authorization", authorization())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> requestCancel(
            String paymentKey,
            String reason
    ) throws Exception {

        String body = """
            { "cancelReason": "%s" }
        """.formatted(reason);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        "https://api.tosspayments.com/v1/payments/"
                                + paymentKey + "/cancel"))
                .header("Authorization", authorization())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
    }
}

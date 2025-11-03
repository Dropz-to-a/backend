package com.jobmanager.job_manager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobmanager.job_manager.dto.payments.CancelRequest;
import com.jobmanager.job_manager.dto.payments.PaymentSnapshot;
import com.jobmanager.job_manager.dto.payments.SinglePayRequest;
import com.jobmanager.job_manager.global.config.TossProperties;
import com.jobmanager.job_manager.repository.PaymentOrderStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TossSinglePaymentService {

    private final RestClient restClient;
    private final TossProperties toss;
    private final PaymentOrderStore store;

    private static final DateTimeFormatter ORD_ID_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Transactional
    public PaymentSnapshot requestSinglePayment(SinglePayRequest req) {
        // 1) orderId 없으면 생성 + PENDING 저장
        String orderId = Optional.ofNullable(req.getOrderId())
                .filter(s -> !s.isBlank())
                .orElseGet(() -> "ORD-" + OffsetDateTime.now().format(ORD_ID_FMT)
                        + "-" + UUID.randomUUID().toString().substring(0, 6));

        if (!store.existsByOrderId(orderId)) {
            store.createPendingOrder(orderId, req.getAmount(), req.getOrderName(), null);
        }

        // 2) Toss 결제(REST /v1/payments) 호출
        String url = toss.getBaseUrl() + "/payments";
        String basic = Base64.getEncoder().encodeToString((toss.getSecretKey() + ":").getBytes(StandardCharsets.UTF_8));

        Map<String, Object> body = Map.of(
                "amount", req.getAmount(),
                "orderId", orderId,
                "orderName", req.getOrderName(),
                "cardNumber", req.getCardNumber(),
                "cardExpirationYear", req.getCardExpYear(),
                "cardExpirationMonth", req.getCardExpMonth(),
                "cardPassword", req.getCardPw(),
                "customerIdentityNumber", req.getCustomerIdentityNumber()
        );

        String res = restClient.post()
                .uri(url)
                .headers(h -> {
                    h.set(HttpHeaders.AUTHORIZATION, "Basic " + basic);
                    h.setContentType(MediaType.APPLICATION_JSON);
                })
                .body(body)
                .retrieve()
                .body(String.class);

        // 3) 응답 파싱 + 저장
        try {
            ObjectMapper om = new ObjectMapper();
            JsonNode n = om.readTree(res);

            String status = n.path("status").asText();          // DONE / CANCELED 등
            String method = n.path("method").asText(null);      // CARD
            String approvedAt = n.path("approvedAt").asText(null);
            String receiptUrl = n.path("receipt").path("url").asText(null);
            String paymentKey = n.path("paymentKey").asText(null);

            if ("DONE".equalsIgnoreCase(status) || "PAID".equalsIgnoreCase(status)) {
                store.savePaymentKey(orderId, paymentKey);
                store.markPaid(orderId,
                        method != null ? method : "CARD",
                        approvedAt != null ? approvedAt : OffsetDateTime.now().format(ISO),
                        receiptUrl);
            } else {
                store.savePaymentKey(orderId, paymentKey);
                store.markFailed(orderId, "STATUS=" + status);
            }

            return store.findSnapshot(orderId).orElse(
                    PaymentSnapshot.builder()
                            .orderId(orderId)
                            .amount(req.getAmount())
                            .status("PAID")
                            .method(method)
                            .paidAt(approvedAt)
                            .receiptUrl(receiptUrl)
                            .build()
            );
        } catch (Exception e) {
            throw new IllegalStateException("TOSS 응답 파싱 실패: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public PaymentSnapshot get(String orderId) {
        return store.findSnapshot(orderId).orElseThrow(() -> new IllegalArgumentException("ORDER_NOT_FOUND"));
    }

    @Transactional
    public PaymentSnapshot cancel(String orderId, CancelRequest req) {
        var brief = store.findBrief(orderId).orElseThrow(() -> new IllegalArgumentException("ORDER_NOT_FOUND"));
        if (!"PAID".equalsIgnoreCase(brief.status())) {
            throw new IllegalStateException("ORDER_NOT_CANCELABLE");
        }

        String paymentKey = store.findPaymentKey(orderId).orElseThrow(() -> new IllegalStateException("PAYMENT_KEY_NOT_FOUND"));

        String url = toss.getBaseUrl() + "/payments/" + paymentKey + "/cancel";
        String basic = Base64.getEncoder().encodeToString((toss.getSecretKey() + ":").getBytes(StandardCharsets.UTF_8));

        Map<String, Object> body = (req.getCancelAmount() == null)
                ? Map.of("cancelReason", req.getCancelReason())
                : Map.of("cancelReason", req.getCancelReason(), "cancelAmount", req.getCancelAmount());

        String res = restClient.post()
                .uri(url)
                .headers(h -> {
                    h.set(HttpHeaders.AUTHORIZATION, "Basic " + basic);
                    h.setContentType(MediaType.APPLICATION_JSON);
                })
                .body(body)
                .retrieve()
                .body(String.class);

        try {
            ObjectMapper om = new ObjectMapper();
            JsonNode n = om.readTree(res);
            String canceledAt = n.path("canceledAt").asText(OffsetDateTime.now().format(ISO));
            Long cancelAmount = req.getCancelAmount() == null ? brief.amount() : req.getCancelAmount();

            store.markCanceled(orderId, canceledAt, cancelAmount);
            return store.findSnapshot(orderId).orElseThrow(() -> new IllegalStateException("ORDER_NOT_FOUND"));
        } catch (Exception e) {
            throw new IllegalStateException("TOSS 취소 응답 파싱 실패: " + e.getMessage());
        }
    }
}

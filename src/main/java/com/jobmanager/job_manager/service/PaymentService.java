package com.jobmanager.job_manager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobmanager.job_manager.dto.payments.*;
import com.jobmanager.job_manager.global.config.TossProperties;
import com.jobmanager.job_manager.repository.PaymentOrderStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RestClient restClient;
    private final TossProperties tossProps;
    private final PaymentOrderStore orderStore;

    private static final DateTimeFormatter ORD_ID_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    /* ---------------- 주문 생성 / 체크아웃 ---------------- */

    @Transactional
    public PaymentSnapshot createOrder(Long accountId, CreateOrderRequest req) {
        String orderId = "ORD-" + OffsetDateTime.now().format(ORD_ID_FMT)
                + "-" + UUID.randomUUID().toString().substring(0, 6);

        if (orderStore.existsByOrderId(orderId)) throw new IllegalStateException("ORDER_DUPLICATED");

        orderStore.createPendingOrder(orderId, req.getTotalAmount(), req.getOrderName(), accountId);
        return PaymentSnapshot.builder()
                .orderId(orderId)
                .amount(req.getTotalAmount())
                .status("PENDING")
                .build();
    }

    @Transactional(readOnly = true)
    public CheckoutInfoResponse getCheckoutInfo(String orderId, String customerName, String customerEmail) {
        var brief = orderStore.findBrief(orderId)
                .orElseThrow(() -> new IllegalArgumentException("ORDER_NOT_FOUND"));

        if (!"PENDING".equals(brief.status())) throw new IllegalStateException("ORDER_NOT_PENDING");

        return CheckoutInfoResponse.builder()
                .clientKey(tossProps.getClientKey())
                .orderId(orderId)
                .amount(brief.amount())
                .orderName("주문 " + orderId)
                .customer(CheckoutInfoResponse.Customer.builder()
                        .name(Optional.ofNullable(customerName).orElse("고객"))
                        .email(Optional.ofNullable(customerEmail).orElse("unknown@example.com"))
                        .build())
                .build();
    }

    /* ---------------- 결제 승인 ---------------- */

    @Transactional
    public PaymentSnapshot confirm(ConfirmRequest req) {
        var brief = orderStore.findBrief(req.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("ORDER_NOT_FOUND"));

        // 멱등: 이미 결제 완료면 현재 스냅샷 반환
        if (!"PENDING".equals(brief.status())) {
            if ("PAID".equals(brief.status())) {
                return orderStore.findSnapshot(req.getOrderId())
                        .orElseThrow(() -> new IllegalStateException("ORDER_NOT_FOUND"));
            }
            throw new IllegalStateException("ORDER_NOT_PENDING");
        }

        // 금액 교차 검증(프론트 금액은 신뢰X)
        if (!brief.amount().equals(req.getAmount()))
            throw new IllegalArgumentException("ORDER_AMOUNT_MISMATCH");

        // 토스 승인 API 호출
        Map<String, Object> res = callTossConfirm(req.getPaymentKey(), req.getOrderId(), req.getAmount());

        // 응답에서 필요한 값 추출
        String method     = (String) res.getOrDefault("method", "UNKNOWN");
        String approvedAt = (String) res.getOrDefault("approvedAt", OffsetDateTime.now().format(ISO));
        String receiptUrl = (String) res.getOrDefault("receiptUrl", null);

        // 결제키 저장(취소용)
        orderStore.savePaymentKey(req.getOrderId(), req.getPaymentKey());

        // DB 상태 반영
        orderStore.markPaid(req.getOrderId(), method, approvedAt, receiptUrl);

        // 최신 스냅샷 반환
        return orderStore.findSnapshot(req.getOrderId())
                .orElse(PaymentSnapshot.builder()
                        .orderId(req.getOrderId())
                        .amount(req.getAmount())
                        .status("PAID")
                        .method(method)
                        .paidAt(approvedAt)
                        .receiptUrl(receiptUrl)
                        .build());
    }

    @Transactional(readOnly = true)
    public PaymentSnapshot get(String orderId) {
        return orderStore.findSnapshot(orderId)
                .orElseThrow(() -> new IllegalArgumentException("ORDER_NOT_FOUND"));
    }

    @Transactional
    public PaymentSnapshot cancel(String orderId, CancelRequest req) {
        var brief = orderStore.findBrief(orderId)
                .orElseThrow(() -> new IllegalArgumentException("ORDER_NOT_FOUND"));

        if (!"PAID".equals(brief.status())) throw new IllegalStateException("ORDER_NOT_CANCELABLE");

        String paymentKey = orderStore.findPaymentKey(orderId)
                .orElseThrow(() -> new IllegalStateException("PAYMENT_KEY_NOT_FOUND"));

        Map<String, Object> res = callTossCancel(paymentKey, req.getCancelReason(), req.getCancelAmount());
        String canceledAt = (String) res.getOrDefault("canceledAt", OffsetDateTime.now().format(ISO));
        Long cancelAmount = (req.getCancelAmount() == null) ? brief.amount() : req.getCancelAmount();

        orderStore.markCanceled(orderId, canceledAt, cancelAmount);
        return orderStore.findSnapshot(orderId)
                .orElseThrow(() -> new IllegalStateException("ORDER_NOT_FOUND"));
    }

    /* ---------------- 웹훅 (컨트롤러에서 호출) ---------------- */

    // 컨트롤러에 이미 receive()가 있으므로, 아래 2개 유틸만 노출해도 됨.
    public Map<String, Object> verifyAndAcknowledgeWebhook(String signatureHeader, String rawBody) {
        if (signatureHeader == null || signatureHeader.isBlank())
            throw new IllegalArgumentException("WEBHOOK_SIGNATURE_MISSING");

        String providedSig = extractV1(signatureHeader);
        String computedSig = computeHmac256Hex(tossProps.getSecretKey(), rawBody);

        if (!providedSig.equalsIgnoreCase(computedSig))
            throw new IllegalArgumentException("WEBHOOK_SIGNATURE_INVALID");

        // 이벤트 최소 파싱: 상태 반영은 필요에 따라 확장 가능
        try {
            ObjectMapper om = new ObjectMapper();
            JsonNode root = om.readTree(rawBody);
            String eventType = root.path("eventType").asText(null);
            String orderId   = root.path("orderId").asText(null);
            if ("PAYMENT_APPROVED".equals(eventType)) {
                // approvedAt/method/receiptUrl이 포함됐다면 추가 반영 가능
                String approvedAt = root.path("approvedAt").asText(null);
                String method     = root.path("method").asText(null);
                orderStore.markPaid(orderId, method, approvedAt, null);
            } else if ("PAYMENT_CANCELED".equals(eventType)) {
                String canceledAt = root.path("canceledAt").asText(null);
                orderStore.markCanceled(orderId, canceledAt, null);
            }
            return Map.of("received", true, "eventType", eventType, "orderId", orderId);
        } catch (Exception e) {
            return Map.of("received", true);
        }
    }

    /* ---------------- Toss HTTP ---------------- */

    private Map<String, Object> callTossConfirm(String paymentKey, String orderId, Long amount) {
        String url = tossProps.getBaseUrl() + "/payments/confirm";
        String basic = base64Basic(tossProps.getSecretKey());

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

    private Map<String, Object> callTossCancel(String paymentKey, String reason, Long cancelAmount) {
        String url = tossProps.getBaseUrl() + "/payments/" + paymentKey + "/cancel";
        String basic = base64Basic(tossProps.getSecretKey());

        Map<String, Object> body = (cancelAmount == null)
                ? Map.of("cancelReason", reason)
                : Map.of("cancelReason", reason, "cancelAmount", cancelAmount);

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

    private String base64Basic(String secret) {
        return Base64.getEncoder().encodeToString((secret + ":").getBytes(StandardCharsets.UTF_8));
    }

    private String extractV1(String signatureHeader) {
        String[] parts = signatureHeader.split(",");
        for (String p : parts) {
            String s = p.trim();
            if (s.startsWith("v1=")) return s.substring(3).trim();
        }
        return signatureHeader.trim();
    }

    private String computeHmac256Hex(String secret, String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] bytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("WEBHOOK_HMAC_ERROR");
        }
    }
}

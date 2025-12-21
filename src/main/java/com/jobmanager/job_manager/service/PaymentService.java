package com.jobmanager.job_manager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobmanager.job_manager.config.TossProperties;
import com.jobmanager.job_manager.dto.payments.*;
import com.jobmanager.job_manager.entity.payment.*;
import com.jobmanager.job_manager.repository.PaymentOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentOrderRepository orderRepo;
    private final TossProperties toss;
    private final ObjectMapper om; // Bean 주입 권장

    private final RestTemplate rt = new RestTemplate();

    /**
     * STEP 1) 주문 생성
     * - 중복 orderId 방지
     * - DB에 READY로 저장
     */
    @Transactional
    public CreatePaymentOrderResponse createOrder(Long companyId, CreatePaymentOrderRequest req) {
        if (req.getOrderId() == null || req.getOrderId().isBlank()) {
            throw new IllegalArgumentException("orderId는 필수입니다.");
        }
        if (req.getAmount() <= 0) {
            throw new IllegalArgumentException("amount는 0보다 커야 합니다.");
        }
        if (orderRepo.existsByOrderId(req.getOrderId())) {
            throw new IllegalStateException("이미 존재하는 orderId 입니다: " + req.getOrderId());
        }

        PaymentOrder order = PaymentOrder.builder()
                .orderId(req.getOrderId())
                .companyId(companyId)
                .employeeId(req.getEmployeeId())
                .contractId(req.getContractId())
                .amount(BigDecimal.valueOf(req.getAmount()))
                .currency("KRW")
                .status(PaymentOrderStatus.READY)
                .method(PaymentMethod.CARD)
                .requestedAt(LocalDateTime.now())
                .build();

        orderRepo.save(order);

        return CreatePaymentOrderResponse.builder()
                .orderId(order.getOrderId())
                .orderName(req.getOrderName())
                .amount(req.getAmount())
                .currency(order.getCurrency())
                .successUrl(toss.getSuccessUrl())
                .failUrl(toss.getFailUrl())
                .build();
    }

    /**
     * STEP 2) 결제 승인(Confirm)
     * - DB 주문 존재/소유자(companyId)/금액 검증
     * - 토스 confirm 호출
     * - 성공/실패 DB 반영
     */
    public TossPayResponse confirm(Long companyId, PaymentConfirmRequest req) throws Exception {

        PaymentOrder order = orderRepo.findByOrderId(req.getOrderId())
                .orElseThrow(() -> new IllegalStateException("주문이 존재하지 않습니다: " + req.getOrderId()));

        // (보안) 회사 소유 주문인지 체크
        if (!order.getCompanyId().equals(companyId)) {
            throw new IllegalStateException("해당 주문에 대한 권한이 없습니다.");
        }

        // (검증) 금액 불일치 방지
        int dbAmount = order.getAmount().intValue();
        if (dbAmount != req.getAmount()) {
            throw new IllegalStateException("결제 금액 불일치 (db=" + dbAmount + ", req=" + req.getAmount() + ")");
        }

        // 중복 confirm 방지(이미 결제 완료면 그대로 반환해도 됨)
        if (order.getStatus() == PaymentOrderStatus.PAID) {
            // 이미 결제 완료된 주문이면 idempotent 하게 처리
            TossPayResponse cached = new TossPayResponse(); // 간단히 반환하기 애매하면 별도 response DTO로 바꿔도 됨
            // 여기선 토스 응답을 저장해두니 raw에서 파싱해서 내려줘도 됨(확장 포인트)
            return cached;
        }

        // 1) CONFIRMING 으로 먼저 바꿔서 동시요청 방지
        markConfirming(order);

        // 2) 토스 confirm 호출 (TX 밖)
        TossPayResponse tossResp;
        try {
            tossResp = callTossConfirm(req);
        } catch (Exception e) {
            // 3) 실패 반영 (TX)
            markFailed(order, e);
            throw e;
        }

        // 4) 토스 응답 검증 + 성공 반영 (TX)
        markPaid(order, tossResp);

        return tossResp;
    }

    /**
     * 결제 취소
     */
    public TossPayResponse cancel(Long companyId, String orderId, PaymentCancelRequest req) throws Exception {
        PaymentOrder order = orderRepo.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalStateException("주문이 존재하지 않습니다: " + orderId));

        if (!order.getCompanyId().equals(companyId)) {
            throw new IllegalStateException("해당 주문에 대한 권한이 없습니다.");
        }
        if (order.getTossPaymentKey() == null || order.getTossPaymentKey().isBlank()) {
            throw new IllegalStateException("paymentKey가 없어 취소할 수 없습니다.");
        }

        TossPayResponse tossResp;
        try {
            tossResp = callTossCancel(order.getTossPaymentKey(), req);
        } catch (Exception e) {
            log.error("Toss cancel error: {}", e.getMessage());
            throw e;
        }

        markCancelled(order, tossResp);
        return tossResp;
    }

    // -------------------------
    // DB 상태 변경은 TX로 분리
    // -------------------------

    @Transactional
    protected void markConfirming(PaymentOrder order) {
        order.setStatus(PaymentOrderStatus.CONFIRMING);
        orderRepo.save(order);
    }

    @Transactional
    protected void markPaid(PaymentOrder order, TossPayResponse resp) throws Exception {
        // 토스 totalAmount 검증(위변조 방지)
        int dbAmount = order.getAmount().intValue();
        if (resp == null || resp.getTotalAmount() != dbAmount) {
            order.setStatus(PaymentOrderStatus.FAILED);
            orderRepo.save(order);
            throw new IllegalStateException("토스 응답 금액 불일치 또는 응답 없음");
        }

        order.setStatus(PaymentOrderStatus.PAID);
        order.setTossPaymentKey(resp.getPaymentKey());
        order.setPaidAt(LocalDateTime.now());
        order.setTossRawResponse(om.writeValueAsString(resp));
        orderRepo.save(order);
    }

    @Transactional
    protected void markFailed(PaymentOrder order, Exception e) {
        order.setStatus(PaymentOrderStatus.FAILED);
        // 실패 raw도 남기고 싶으면 e 메시지를 json으로 감싸 저장하는 식으로 확장 가능
        orderRepo.save(order);
    }

    @Transactional
    protected void markCancelled(PaymentOrder order, TossPayResponse resp) throws Exception {
        order.setStatus(PaymentOrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        order.setTossRawResponse(om.writeValueAsString(resp));
        orderRepo.save(order);
    }

    // -------------------------
    // Toss API 호출부
    // -------------------------

    private TossPayResponse callTossConfirm(PaymentConfirmRequest req) throws Exception {
        String url = toss.getBaseUrl() + "/v1/payments/confirm";

        TossConfirmRequest body = TossConfirmRequest.builder()
                .paymentKey(req.getPaymentKey())
                .orderId(req.getOrderId())
                .amount(req.getAmount())
                .build();

        HttpEntity<String> entity = new HttpEntity<>(om.writeValueAsString(body), tossAuthHeaders());
        try {
            ResponseEntity<TossPayResponse> resp = rt.exchange(url, HttpMethod.POST, entity, TossPayResponse.class);
            return resp.getBody();
        } catch (HttpStatusCodeException e) {
            // 토스가 내려준 에러바디를 로그로 남기면 디버깅이 쉬움
            log.error("Toss confirm failed: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    private TossPayResponse callTossCancel(String paymentKey, PaymentCancelRequest req) throws Exception {
        String url = toss.getBaseUrl() + "/v1/payments/" + paymentKey + "/cancel";

        TossCancelRequest body = TossCancelRequest.builder()
                .cancelReason(req.getCancelReason() == null ? "사용자 요청" : req.getCancelReason())
                .build();

        HttpEntity<String> entity = new HttpEntity<>(om.writeValueAsString(body), tossAuthHeaders());
        try {
            ResponseEntity<TossPayResponse> resp = rt.exchange(url, HttpMethod.POST, entity, TossPayResponse.class);
            return resp.getBody();
        } catch (HttpStatusCodeException e) {
            log.error("Toss cancel failed: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    private HttpHeaders tossAuthHeaders() {
        String encoded = Base64.getEncoder().encodeToString((toss.getSecretKey() + ":").getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + encoded);
        return headers;
    }
}
package com.jobmanager.job_manager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobmanager.job_manager.config.TossProperties;
import com.jobmanager.job_manager.dto.payments.*;
import com.jobmanager.job_manager.entity.payment.*;
import com.jobmanager.job_manager.repository.PaymentOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentOrderRepository orderRepo;
    private final TossProperties toss;
    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 결제 주문 생성
     */
    @Transactional
    public CreatePaymentOrderResponse createOrder(CreatePaymentOrderRequest req) {

        Long companyId = getCompanyIdFromToken();

        PaymentOrder order = PaymentOrder.builder()
                .companyId(companyId)
                .orderId(UUID.randomUUID().toString())
                .orderName(req.getOrderName())
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
                .orderName(order.getOrderName())
                .amount(order.getAmount().intValue())
                .currency(order.getCurrency())
                .build();
    }

    /**
     * 결제 승인 (Confirm)
     */
    public TossPayResponse confirm(PaymentConfirmRequest req) throws Exception {

        Long companyId = getCompanyIdFromToken();

        PaymentOrder order = orderRepo.findByOrderId(req.getOrderId())
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 주문입니다."));

        if (!order.getCompanyId().equals(companyId)) {
            throw new IllegalStateException("결제 권한이 없습니다.");
        }

        if (order.getAmount().intValue() != req.getAmount()) {
            throw new IllegalStateException("결제 금액 불일치");
        }

        TossPayResponse response = callTossConfirm(req);

        order.setStatus(PaymentOrderStatus.PAID);
        order.setTossPaymentKey(response.getPaymentKey());
        order.setPaidAt(LocalDateTime.now());
        order.setTossRawResponse(objectMapper.writeValueAsString(response));

        orderRepo.save(order);
        return response;
    }

    /**
     * 결제 취소
     */
    public TossPayResponse cancel(String orderId, PaymentCancelRequest req) throws Exception {

        Long companyId = getCompanyIdFromToken();

        PaymentOrder order = orderRepo.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 주문입니다."));

        if (!order.getCompanyId().equals(companyId)) {
            throw new IllegalStateException("결제 취소 권한이 없습니다.");
        }

        TossPayResponse response = callTossCancel(order.getTossPaymentKey(), req);

        order.setStatus(PaymentOrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        order.setTossRawResponse(objectMapper.writeValueAsString(response));

        orderRepo.save(order);
        return response;
    }

    /* ===================== 내부 유틸 ===================== */

    private Long getCompanyIdFromToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Long)) {
            throw new IllegalStateException("인증 정보가 유효하지 않습니다.");
        }
        return (Long) auth.getPrincipal();
    }

    private TossPayResponse callTossConfirm(PaymentConfirmRequest req) {
        String url = toss.getBaseUrl() + "/v1/payments/confirm";

        HttpHeaders headers = tossHeaders();
        HttpEntity<PaymentConfirmRequest> entity = new HttpEntity<>(req, headers);

        return restTemplate.postForObject(url, entity, TossPayResponse.class);
    }

    private TossPayResponse callTossCancel(String paymentKey, PaymentCancelRequest req) {
        String url = toss.getBaseUrl() + "/v1/payments/" + paymentKey + "/cancel";

        HttpHeaders headers = tossHeaders();
        HttpEntity<PaymentCancelRequest> entity = new HttpEntity<>(req, headers);

        return restTemplate.postForObject(url, entity, TossPayResponse.class);
    }

    private HttpHeaders tossHeaders() {
        String encodedKey = Base64.getEncoder()
                .encodeToString((toss.getSecretKey() + ":").getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + encodedKey);
        return headers;
    }
}

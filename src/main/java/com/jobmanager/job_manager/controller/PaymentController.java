package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.payments.CreatePaymentOrderRequest;
import com.jobmanager.job_manager.dto.payments.CreatePaymentOrderResponse;
import com.jobmanager.job_manager.dto.payments.PaymentCancelRequest;
import com.jobmanager.job_manager.dto.payments.PaymentConfirmRequest;
import com.jobmanager.job_manager.dto.payments.TossPayResponse;
import com.jobmanager.job_manager.service.PaymentService;
import com.jobmanager.job_manager.util.AuthAccountIdResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final AuthAccountIdResolver accountIdResolver;

    /**
     * STEP 1) 결제 주문 생성 (DB에 READY로 저장)
     * - 프론트는 응답 받은 orderId/amount/orderName 으로 토스 결제창/위젯 실행
     */
    @PostMapping("/orders")
    public ResponseEntity<CreatePaymentOrderResponse> createOrder(
            @RequestBody CreatePaymentOrderRequest req,
            @RequestHeader(value = "companyId", required = false) Long companyIdHeader
    ) {
        Long companyId = accountIdResolver.resolveCompanyIdOrThrow(companyIdHeader);
        return ResponseEntity.ok(paymentService.createOrder(companyId, req));
    }

    /**
     * STEP 2) 결제 승인(Confirm)
     * - 프론트가 토스 결제 완료 후 paymentKey/orderId/amount를 서버로 보내면
     *   서버가 토스 confirm 호출 + DB 검증/업데이트 수행
     */
    @PostMapping("/confirm")
    public ResponseEntity<TossPayResponse> confirm(
            @RequestBody PaymentConfirmRequest req,
            @RequestHeader(value = "companyId", required = false) Long companyIdHeader
    ) throws Exception {
        Long companyId = accountIdResolver.resolveCompanyIdOrThrow(companyIdHeader);
        return ResponseEntity.ok(paymentService.confirm(companyId, req));
    }

    /**
     * 결제 취소
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<TossPayResponse> cancel(
            @PathVariable String orderId,
            @RequestBody PaymentCancelRequest req,
            @RequestHeader(value = "companyId", required = false) Long companyIdHeader
    ) throws Exception {
        Long companyId = accountIdResolver.resolveCompanyIdOrThrow(companyIdHeader);
        return ResponseEntity.ok(paymentService.cancel(companyId, orderId, req));
    }
}

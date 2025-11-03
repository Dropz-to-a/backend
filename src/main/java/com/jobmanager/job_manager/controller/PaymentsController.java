package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.payments.CancelRequest;
import com.jobmanager.job_manager.dto.payments.CheckoutInfoResponse;
import com.jobmanager.job_manager.dto.payments.ConfirmRequest;
import com.jobmanager.job_manager.dto.payments.CreateOrderRequest;
import com.jobmanager.job_manager.dto.payments.PaymentSnapshot;
import com.jobmanager.job_manager.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentsController {

    private final PaymentService paymentService;

    /** 1) 주문 생성: 서버가 orderId 발급 + PENDING 저장 */
    @PostMapping("/orders")
    public PaymentSnapshot createOrder(@RequestHeader(value = "X-Account-Id", required = false) Long accountId,
                                       @RequestBody @Valid CreateOrderRequest req) {
        return paymentService.createOrder(accountId, req);
    }

    /** 2) 체크아웃 정보: 프론트 위젯 렌더에 필요한 값 */
    @GetMapping("/checkout-info")
    public CheckoutInfoResponse checkoutInfo(@RequestParam String orderId,
                                             @RequestParam(required = false) String customerName,
                                             @RequestParam(required = false) String customerEmail) {
        return paymentService.getCheckoutInfo(orderId, customerName, customerEmail);
    }

    /** 3) 결제 승인: successUrl에서 받은 값을 서버로 → 토스 승인 API 호출 */
    @PostMapping("/confirm")
    public PaymentSnapshot confirm(@RequestBody @Valid ConfirmRequest req) {
        return paymentService.confirm(req);
    }

    /** 4) 결제 단건 조회 */
    @GetMapping("/{orderId}")
    public PaymentSnapshot get(@PathVariable String orderId) {
        return paymentService.get(orderId);
    }

    /** 5) 결제 취소(전체/부분) */
    @PostMapping("/{orderId}/cancel")
    public PaymentSnapshot cancel(@PathVariable String orderId,
                                  @RequestBody @Valid CancelRequest req) {
        return paymentService.cancel(orderId, req);
    }
}

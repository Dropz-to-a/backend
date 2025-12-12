package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.payments.SinglePayRequest;
import com.jobmanager.job_manager.entity.payment.PaymentOrder;
import com.jobmanager.job_manager.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /** 단일 결제 요청 생성 */
    @PostMapping("/single-pay")
    public ResponseEntity<?> singlePay(
            @RequestBody SinglePayRequest req,
            @RequestHeader("companyId") Long companyId  // 임시. JWT 연동 후 변경 예정
    ) {
        PaymentOrder order = paymentService.createOrder(companyId, req);
        return ResponseEntity.ok(order);
    }
}

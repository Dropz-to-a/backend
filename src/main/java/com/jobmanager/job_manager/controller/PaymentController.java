package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.payments.SinglePayRequest;
import com.jobmanager.job_manager.dto.payments.TossPayResponse;
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

    @PostMapping("/single-pay")
    public ResponseEntity<?> singlePay(
            @RequestBody SinglePayRequest req,
            @RequestHeader("companyId") Long companyId
    ) throws Exception {

        TossPayResponse result = paymentService.requestPay(companyId, req);
        return ResponseEntity.ok(result);
    }

}

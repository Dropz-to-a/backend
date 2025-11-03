package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.service.PaymentService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 토스 결제 관련 컨트롤러 (네 구조: controller 패키지)
 * - 불필요한 의존/패키지 없이 Service만 호출
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Validated
public class PaymentsController {

    private final PaymentService paymentService;

    /**
     * 1) 결제 승인 (successUrl에서 받은 값으로 호출)
     * Body: { paymentKey, orderId, amount }
     * 응답: 토스 승인 API 응답 그대로(최소한) 전달
     */
    @PostMapping("/confirm")
    public Map<String, Object> confirm(@RequestBody @Validated ConfirmRequest body) {
        return paymentService.confirm(body.paymentKey(), body.orderId(), body.amount());
    }

    /* ---- 최소 DTO: 파일/패키지 추가 없이 컨트롤러 안에 둠 ---- */
    public record ConfirmRequest(
            @NotBlank String paymentKey,
            @NotBlank String orderId,
            @Min(1)    Long amount
    ) {}
}


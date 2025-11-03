package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.payments.CancelRequest;
import com.jobmanager.job_manager.dto.payments.PaymentSnapshot;
import com.jobmanager.job_manager.dto.payments.SinglePayRequest;
import com.jobmanager.job_manager.service.TossSinglePaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Toss Single Payment (REST)", description = "위젯 없이 단일결제 테스트용 API")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class TossSinglePaymentController {

    private final TossSinglePaymentService service;

    @Operation(summary = "단일 결제 요청(테스트)", description = "사업자 없이 테스트 가능. 카드정보는 토스 테스트카드 사용.")
    @PostMapping("/single-pay")
    public PaymentSnapshot singlePay(@RequestBody @Valid SinglePayRequest req) {
        return service.requestSinglePayment(req);
    }

    @Operation(summary = "결제 상태 조회", description = "orderId 기준으로 현재 저장된 결제 상태 조회")
    @GetMapping("/{orderId}")
    public PaymentSnapshot get(@PathVariable String orderId) {
        return service.get(orderId);
    }

    @Operation(summary = "결제 취소", description = "전액/부분 취소 요청")
    @PostMapping("/{orderId}/cancel")
    public PaymentSnapshot cancel(@PathVariable String orderId, @RequestBody @Valid CancelRequest req) {
        return service.cancel(orderId, req);
    }
}

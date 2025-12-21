package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.payments.*;
import com.jobmanager.job_manager.service.PaymentService;
import com.jobmanager.job_manager.util.AuthAccountIdResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "결제 API",
        description = "토스 결제 주문 생성, 결제 승인(confirm), 결제 취소를 처리하는 API"
)
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final AuthAccountIdResolver accountIdResolver;

    @Operation(
            summary = "결제 주문 생성",
            description = """
                    결제 주문을 생성합니다.
                    
                    - DB에 결제 주문을 READY 상태로 저장합니다.
                    - 프론트엔드는 응답으로 받은 orderId, amount, orderName을 이용해
                      토스 결제창(위젯)을 실행해야 합니다.
                    """
    )
    @PostMapping("/orders")
    public ResponseEntity<CreatePaymentOrderResponse> createOrder(
            @RequestBody CreatePaymentOrderRequest req,
            @RequestHeader(value = "companyId", required = false) Long companyIdHeader
    ) {
        Long companyId = accountIdResolver.resolveCompanyIdOrThrow(companyIdHeader);
        return ResponseEntity.ok(paymentService.createOrder(companyId, req));
    }

    @Operation(
            summary = "결제 승인 (Confirm)",
            description = """
                    토스 결제 완료 후 결제를 승인합니다.
                    
                    - paymentKey, orderId, amount를 검증합니다.
                    - 토스 결제 승인 API(confirm)를 호출합니다.
                    - 결제 성공 시 상태를 PAID로 변경합니다.
                    - 결제 실패 시 FAILED로 변경됩니다.
                    """
    )
    @PostMapping("/confirm")
    public ResponseEntity<TossPayResponse> confirm(
            @RequestBody PaymentConfirmRequest req,
            @RequestHeader(value = "companyId", required = false) Long companyIdHeader
    ) throws Exception {
        Long companyId = accountIdResolver.resolveCompanyIdOrThrow(companyIdHeader);
        return ResponseEntity.ok(paymentService.confirm(companyId, req));
    }

    @Operation(
            summary = "결제 취소",
            description = """
                    이미 승인된 결제를 취소합니다.
                    
                    - 토스 paymentKey를 기준으로 결제를 취소합니다.
                    - 취소 성공 시 상태는 CANCELLED로 변경됩니다.
                    """
    )
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

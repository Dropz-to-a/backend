package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.payments.*;
import com.jobmanager.job_manager.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "결제 API",
        description = "토스 결제 주문 생성, 결제 승인(confirm), 결제 취소 API"
)
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "결제 주문 생성",
            description = """
                    결제 주문을 생성합니다.
                    
                    - companyId는 JWT 인증 정보에서 자동 추출됩니다.
                    - orderId는 서버에서 UUID로 생성됩니다.
                    - 생성된 orderId를 사용해 프론트에서 토스 결제창을 호출합니다.
                    """
    )
    @PostMapping("/orders")
    public ResponseEntity<CreatePaymentOrderResponse> createOrder(
            @RequestBody CreatePaymentOrderRequest req
    ) {
        return ResponseEntity.ok(paymentService.createOrder(req));
    }

    @Operation(
            summary = "결제 승인 (Confirm)",
            description = """
                    토스 결제 완료 후 결제를 승인합니다.
                    
                    - paymentKey / orderId / amount 검증
                    - 토스 결제 승인 API(confirm) 호출
                    - 성공 시 결제 상태를 PAID로 변경
                    """
    )
    @PostMapping("/confirm")
    public ResponseEntity<TossPayResponse> confirm(
            @RequestBody PaymentConfirmRequest req
    ) throws Exception {
        return ResponseEntity.ok(paymentService.confirm(req));
    }

    @Operation(
            summary = "결제 취소",
            description = """
                    승인된 결제를 취소합니다.
                    
                    - JWT 기준 company 소유 주문만 취소 가능
                    """
    )
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<TossPayResponse> cancel(
            @PathVariable String orderId,
            @RequestBody PaymentCancelRequest req
    ) throws Exception {
        return ResponseEntity.ok(paymentService.cancel(orderId, req));
    }
}

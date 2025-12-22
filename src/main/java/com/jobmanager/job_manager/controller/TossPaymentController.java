package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.payment.*;
import com.jobmanager.job_manager.service.TossPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Toss Payments API",
        description = """
                Toss Payments 결제 처리 API입니다.

                ✔ 결제 금액 무결성 검증
                ✔ 결제 승인 요청 (서버 → Toss)
                ✔ 결제 조회
                ✔ 결제 취소

                ※ 보안상 결제 승인 요청은 반드시 백엔드를 거쳐 처리됩니다.
                """
)
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class TossPaymentController {

    private final TossPaymentService tossPaymentService;

    @Operation(
            summary = "결제 금액 임시 저장",
            description = """
                    결제 시작 전 orderId와 결제 금액을 세션에 임시 저장합니다.

                    ✔ 프론트엔드에서 결제 위젯 호출 전에 반드시 실행되어야 합니다.
                    ✔ 결제 과정 중 금액 변조 여부를 검증하기 위한 용도입니다.
                    """
    )
    @PostMapping("/saveAmount")
    public ResponseEntity<?> saveAmount(
            HttpSession session,
            @RequestBody SaveAmountRequest request
    ) {
        session.setAttribute(request.orderId(), request.amount());
        return ResponseEntity.ok("Payment temp save successful");
    }

    @Operation(
            summary = "결제 금액 검증",
            description = """
                    Toss Payments 결제 완료 후 전달받은 금액을 검증합니다.

                    ✔ 세션에 저장된 금액과 결제 완료 후 전달된 금액이 동일한지 확인합니다.
                    ✔ 검증이 끝난 세션 데이터는 즉시 삭제됩니다.
                    ✔ 금액이 일치하지 않을 경우 결제는 유효하지 않습니다.
                    """
    )
    @PostMapping("/verifyAmount")
    public ResponseEntity<?> verifyAmount(
            HttpSession session,
            @RequestBody SaveAmountRequest request
    ) {
        String saved = (String) session.getAttribute(request.orderId());

        if (saved == null || !saved.equals(request.amount())) {
            return ResponseEntity.badRequest()
                    .body("결제 금액 정보가 유효하지 않습니다.");
        }

        session.removeAttribute(request.orderId());
        return ResponseEntity.ok("Payment is valid");
    }

    @Operation(
            summary = "결제 승인 요청",
            description = """
                    Toss Payments에 결제 승인을 요청합니다.

                    ✔ 프론트엔드에서 전달받은 paymentKey, orderId, amount를 사용합니다.
                    ✔ 승인 요청은 보안을 위해 반드시 백엔드 서버에서 수행됩니다.
                    ✔ 승인 성공 시 결제 정보를 데이터베이스에 저장합니다.
                    ✔ DB 저장 실패 시 결제를 즉시 취소 처리합니다.
                    """
    )
    @PostMapping("/confirm")
    public ResponseEntity<?> confirm(
            @RequestBody ConfirmPaymentRequest request
    ) throws Exception {
        return ResponseEntity.ok(
                tossPaymentService.confirmPayment(request)
        );
    }

    @Operation(
            summary = "결제 정보 조회",
            description = """
                    내부에서 사용하는 orderId를 기준으로 결제 정보를 조회합니다.

                    ✔ Toss Payments API를 호출하지 않습니다.
                    ✔ 서버에 저장된 결제 정보를 그대로 반환합니다.
                    """
    )
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getPayment(
            @PathVariable String orderId
    ) {
        return ResponseEntity.ok(
                tossPaymentService.getPayment(orderId)
        );
    }

    @Operation(
            summary = "결제 취소",
            description = """
                    이미 승인된 결제를 취소합니다.

                    ✔ Toss Payments 결제 취소 API를 호출합니다.
                    ✔ 취소 성공 시 결제 상태를 CANCELED로 변경합니다.
                    ✔ 부분 취소가 필요한 경우 확장 가능합니다.
                    """
    )
    @PostMapping("/cancel")
    public ResponseEntity<?> cancel(
            @RequestBody CancelPaymentRequest request
    ) throws Exception {
        tossPaymentService.cancelPayment(request);
        return ResponseEntity.ok("Payment canceled");
    }
}

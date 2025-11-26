package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.payments.CancelRequest;
import com.jobmanager.job_manager.dto.payments.PaymentSnapshot;
import com.jobmanager.job_manager.dto.payments.SinglePayRequest;
import com.jobmanager.job_manager.service.TossSinglePaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Toss Single Payment (REST)",
        description = """
            토스페이먼츠 단일 결제(테스트용) API입니다.
            - 위젯 없이 서버에서 직접 토스 API를 호출하는 구조입니다.
            - 실제 운영보다는 기능 검증/학습용에 초점을 둡니다.
            """
)
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class TossSinglePaymentController {

    private final TossSinglePaymentService service;

    @Operation(
            summary = "단일 결제 요청(테스트)",
            description = """
                토스 API를 이용해 단일 결제 요청을 수행합니다.
                
                 특징
                - 사업자 등록 없이 토스 테스트 키로만 호출하는 시나리오를 가정합니다.
                - 카드정보는 토스에서 제공하는 테스트 카드 정보를 사용해야 합니다.
                
                 흐름 예시
                1) 클라이언트에서 SinglePayRequest를 구성하여 이 API 호출
                2) 서버에서 토스 API 승인/실패 결과를 받아 PaymentSnapshot에 저장
                3) 클라이언트는 응답을 바탕으로 결제 성공/실패 화면을 표시
                """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = SinglePayRequest.class),
                            examples = @ExampleObject(
                                    name = "요청 예시",
                                    value = """
                                        {
                                          "orderId": "ORDER-20251126-0001",
                                          "orderName": "알바 급여 정산",
                                          "amount": 150000,
                                          "cardNumber": "1234-1234-1234-1234",
                                          "cardExpiration": "12/27",
                                          "cardPassword": "00",
                                          "customerIdentityNumber": "860101"
                                        }
                                        """
                            )
                    )
            )
    )
    @ApiResponse(
            responseCode = "200",
            description = "결제 요청/처리 성공",
            content = @Content(
                    schema = @Schema(implementation = PaymentSnapshot.class)
            )
    )
    @PostMapping("/single-pay")
    public PaymentSnapshot singlePay(@RequestBody @Valid SinglePayRequest req) {
        return service.requestSinglePayment(req);
    }

    @Operation(
            summary = "결제 상태 조회",
            description = """
                orderId 기준으로 현재 저장된 결제 상태를 조회합니다.
                - 토스 API의 응답을 내부 DB에 저장해두고, 그 스냅샷을 그대로 반환하는 구조입니다.
                """
    )
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PaymentSnapshot.class))
    )
    @GetMapping("/{orderId}")
    public PaymentSnapshot get(@PathVariable String orderId) {
        return service.get(orderId);
    }

    @Operation(
            summary = "결제 취소",
            description = """
                이미 승인된 결제를 전액 또는 부분 취소합니다.
                
                 사용 시나리오
                - 알바 급여 정산 후 오류 발생 시 정정
                - 환불 정책에 따른 부분 취소 등
                
                request body에는 취소 사유 및 취소 금액(부분 취소 시)을 포함합니다.
                """
    )
    @ApiResponse(
            responseCode = "200",
            description = "취소 성공",
            content = @Content(schema = @Schema(implementation = PaymentSnapshot.class))
    )
    @PostMapping("/{orderId}/cancel")
    public PaymentSnapshot cancel(
            @PathVariable String orderId,
            @RequestBody @Valid CancelRequest req
    ) {
        return service.cancel(orderId, req);
    }
}

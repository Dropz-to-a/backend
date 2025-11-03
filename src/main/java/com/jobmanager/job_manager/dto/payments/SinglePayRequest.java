package com.jobmanager.job_manager.dto.payments;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** 단일결제(REST) 요청 DTO — 테스트용 카드 정보 포함 */
@Data
public class SinglePayRequest {
    private String orderId;                 // null이면 서버가 생성

    @NotNull @Min(1)
    private Long amount;

    @NotBlank
    private String orderName;

    // 테스트 카드 정보
    @NotBlank private String cardNumber;       // ex) 4111111111111111
    @NotBlank private String cardExpYear;      // "25"
    @NotBlank private String cardExpMonth;     // "12"
    @NotBlank private String cardPw;           // "11"
    @NotBlank private String customerIdentityNumber; // "900101" 등(6자리/10자리)
}

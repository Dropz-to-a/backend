package com.jobmanager.job_manager.dto.payments;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SinglePayRequest {

    private String orderId;
    private String orderName;

    private int amount;

    private String cardNumber;           // 1234-5678-0000-1111
    private String cardExpiration;       // MM/YY 또는 MM/YYYY
    private String cardPassword;         // 앞 2자리
    private String customerIdentityNumber; // 생년월일 6자리 등
}

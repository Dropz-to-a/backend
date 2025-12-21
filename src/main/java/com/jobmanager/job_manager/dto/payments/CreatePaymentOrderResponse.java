package com.jobmanager.job_manager.dto.payments;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreatePaymentOrderResponse {
    private String orderId;
    private String orderName;
    private int amount;
    private String currency;

    // 프론트가 결제창/위젯에 넣기 편하게 안내값으로 내려줌(선택)
    private String successUrl;
    private String failUrl;
}
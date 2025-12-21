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
}

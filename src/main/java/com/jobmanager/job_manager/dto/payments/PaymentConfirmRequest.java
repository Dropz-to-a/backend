package com.jobmanager.job_manager.dto.payments;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentConfirmRequest {

    private String paymentKey;
    private String orderId;
    private int amount;
}

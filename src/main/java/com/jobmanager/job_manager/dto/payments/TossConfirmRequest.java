package com.jobmanager.job_manager.dto.payments;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TossConfirmRequest {
    private String paymentKey;
    private String orderId;
    private int amount;
}
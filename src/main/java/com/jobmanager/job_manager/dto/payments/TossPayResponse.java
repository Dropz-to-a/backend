package com.jobmanager.job_manager.dto.payments;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TossPayResponse {
    private String paymentKey;
    private String orderId;
    private String orderName;
    private int totalAmount;
    private String status; // DONE, CANCELED, ABORTED 등 (토스 표준)
}
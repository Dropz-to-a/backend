package com.jobmanager.job_manager.dto.payments;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentOrderRequest {
    private String orderId;
    private String orderName;
    private int amount;

    private Long employeeId;
    private Long contractId;
}
package com.jobmanager.job_manager.dto.payment;

public record SaveAmountRequest(
        String orderId,
        String amount
) {}

package com.jobmanager.job_manager.dto.payment;

public record ConfirmPaymentRequest(
        String paymentKey,
        String orderId,
        String amount,
        String backendOrderId
) {}

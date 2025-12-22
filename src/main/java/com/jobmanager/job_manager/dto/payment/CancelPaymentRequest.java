package com.jobmanager.job_manager.dto.payment;

public record CancelPaymentRequest(
        String paymentKey,
        String cancelReason
) {}

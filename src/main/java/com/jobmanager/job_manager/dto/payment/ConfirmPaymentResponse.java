package com.jobmanager.job_manager.dto.payment;

import com.jobmanager.job_manager.entity.payment.TossPaymentStatus;
import lombok.Builder;

@Builder
public record ConfirmPaymentResponse(
        String backendOrderId,
        TossPaymentStatus tossPaymentStatus,
        long totalAmount
) {}

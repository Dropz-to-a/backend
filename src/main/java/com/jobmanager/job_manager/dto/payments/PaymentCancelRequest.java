package com.jobmanager.job_manager.dto.payments;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentCancelRequest {
    private String cancelReason; // "사용자 요청" 등
}
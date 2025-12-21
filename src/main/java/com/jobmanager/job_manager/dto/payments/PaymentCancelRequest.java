package com.jobmanager.job_manager.dto.payments;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentCancelRequest {
    private String cancelReason;
}

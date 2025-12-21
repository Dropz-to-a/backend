package com.jobmanager.job_manager.dto.payments;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TossCancelRequest {
    private String cancelReason;
}
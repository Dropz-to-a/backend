package com.jobmanager.job_manager.dto.payments;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CancelRequest {
    @NotBlank
    private String cancelReason;
    @Min(1)
    private Long cancelAmount; // null이면 전액 취소 (Toss는 null 허용)
}

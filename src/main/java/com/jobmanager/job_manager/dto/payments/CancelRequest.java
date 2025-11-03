package com.jobmanager.job_manager.dto.payments;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** 전체/부분 취소 요청(부분취소면 cancelAmount 필수) */
@Data
public class CancelRequest {
    @NotBlank
    private String cancelReason;

    @Min(1)
    private Long cancelAmount; // null이면 전액 취소
}

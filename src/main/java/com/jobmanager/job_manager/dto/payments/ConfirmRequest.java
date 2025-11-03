package com.jobmanager.job_manager.dto.payments;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** successUrl에서 받은 값을 서버로 전달하여 승인 */
@Data
public class ConfirmRequest {
    @NotBlank
    private String paymentKey;

    @NotBlank
    private String orderId;

    @NotNull @Min(1)
    private Long amount;
}


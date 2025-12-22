package com.jobmanager.job_manager.dto.bankonboarding;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBankOnboardingRequest {

    @Schema(description = "은행명", example = "국민은행")
    private String bankName;

    @Schema(description = "계좌번호", example = "123456-01-123456")
    private String bankAccountNumber;
}
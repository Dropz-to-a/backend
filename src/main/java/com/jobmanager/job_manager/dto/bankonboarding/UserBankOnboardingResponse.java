package com.jobmanager.job_manager.dto.bankonboarding;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserBankOnboardingResponse {

    private Long accountId;
    private String bankName;
    private String bankAccountNumber;
}
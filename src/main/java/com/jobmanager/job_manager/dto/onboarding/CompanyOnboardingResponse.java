// src/main/java/com/jobmanager/job_manager/dto/onboarding/CompanyOnboardingResponse.java
package com.jobmanager.job_manager.dto.onboarding;

import com.jobmanager.job_manager.entity.Company;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompanyOnboardingResponse {

    private Long accountId;
    private String companyName;
    private String zonecode;
    private String address;
    private String detailAddress;
    private String businessNumber;

    public static CompanyOnboardingResponse from(Company c) {
        return CompanyOnboardingResponse.builder()
                .accountId(c.getAccountId())
                .companyName(c.getCompanyName())
                .zonecode(c.getZonecode())
                .address(c.getAddress())
                .detailAddress(c.getDetailAddress())
                .businessNumber(c.getBusinessNumber())
                .build();
    }
}
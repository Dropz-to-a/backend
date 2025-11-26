// src/main/java/com/jobmanager/job_manager/dto/onboarding/CompanyOnboardingResponse.java
package com.jobmanager.job_manager.dto.onboarding;

import com.jobmanager.job_manager.entity.Company;
import lombok.Builder;
import lombok.Getter;

/**
 * 회사 온보딩 저장 결과 응답
 */
@Getter
@Builder
public class CompanyOnboardingResponse {

    private Long accountId;
    private String companyName;
    private String description;
    private String location;
    private String logoUrl;

    public static CompanyOnboardingResponse from(Company c) {
        return CompanyOnboardingResponse.builder()
                .accountId(c.getAccountId())
                .companyName(c.getCompanyName())
                .description(c.getDescription())
                .location(c.getLocation())
                .logoUrl(c.getLogoUrl())
                .build();
    }
}
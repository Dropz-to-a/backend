// src/main/java/com/jobmanager/job_manager/dto/onboarding/CompanyOnboardingRequest.java
package com.jobmanager.job_manager.dto.onboarding;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 회사 온보딩에서 입력받는 정보
 * - 회사 이름
 * - 설명
 * - 위치
 * - 로고 URL
 * accountId는 JWT에서 가져온다.
 */
@Getter
@Setter
public class CompanyOnboardingRequest {

    @Schema(description = "회사 이름", example = "잡매니저 주식회사")
    private String companyName;

    @Schema(description = "회사 소개/설명", example = "채용/근태/결제 통합 관리 플랫폼")
    private String description;

    @Schema(description = "회사 위치", example = "서울특별시 마포구")
    private String location;

    @Schema(description = "로고 이미지 URL", example = "https://example.com/logo.png")
    private String logoUrl;
}
package com.jobmanager.job_manager.dto.onboarding;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyOnboardingRequest {

    @Schema(description = "회사 이름", example = "잡매니저 주식회사")
    private String companyName;

    @Schema(description = "우편번호", example = "06236")
    private String zonecode;

    @Schema(description = "기본 주소", example = "서울특별시 강남구 역삼동")
    private String address;

    @Schema(description = "상세 주소", example = "301호")
    private String detailAddress;

    @Schema(description = "사업자 등록 번호", example = "123-45-67890")
    private String businessNumber;
}
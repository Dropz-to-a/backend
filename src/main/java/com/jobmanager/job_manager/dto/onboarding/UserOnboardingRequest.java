package com.jobmanager.job_manager.dto.onboarding;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserOnboardingRequest {

    @Schema(description = "실명", example = "홍길동")
    private String realName;

    @Schema(description = "생년월일 (yyyy-MM-dd)", example = "2000-01-01")
    private String birth;

    @Schema(description = "기본 주소", example = "서울특별시 강남구 역삼동")
    private String address;

    @Schema(description = "상세 주소", example = "301호")
    private String detailAddress;

    @Schema(description = "우편번호", example = "06236")
    private String zonecode;
}
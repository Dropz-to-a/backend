// src/main/java/com/jobmanager/job_manager/dto/onboarding/UserOnboardingRequest.java
package com.jobmanager.job_manager.dto.onboarding;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 유저 온보딩에서 입력받는 최소 정보
 * - 실명
 * - 생년월일
 * - 거주지 주소
 * accountId는 JWT에서 가져오기 때문에 포함하지 않는다.
 */
@Getter
@Setter
public class UserOnboardingRequest {

    @Schema(description = "실명", example = "홍길동")
    private String realName;

    @Schema(description = "생년월일 (yyyy-MM-dd)", example = "2000-01-01")
    private String birth;

    @Schema(description = "거주지 주소", example = "서울특별시 강남구 역삼동")
    private String address;
}
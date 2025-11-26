// src/main/java/com/jobmanager/job_manager/controller/OnboardingController.java
package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.onboarding.*;
import com.jobmanager.job_manager.global.jwt.SimpleUserPrincipal;
import com.jobmanager.job_manager.service.OnboardingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
@Tag(name = "Onboarding", description = "회원가입 이후 기본 정보/회사 정보 입력 API")
public class OnboardingController {

    private final OnboardingService onboardingService;

    /**
     * USER 온보딩
     * - JWT로 로그인한 USER 계정 기준
     * - 실명, 생년월일, 거주지 주소만 입력
     */
    @PostMapping("/user")
    @Operation(
            summary = "유저 온보딩",
            description = """
                    회원가입을 완료한 USER 계정이 최초 1회 입력하는 기본 정보입니다.
                    
                    [입력값]
                    - realName : 실명
                    - birth    : 생년월일 (yyyy-MM-dd)
                    - address  : 거주지 주소
                    
                    accountId는 JWT 토큰에서 추출하며, 클라이언트에서 보내지 않습니다.
                    나머지 정보(키, 몸무게, 학력, 보유기술, 경력 등)는 별도의 프로필/이력 수정 API에서 관리합니다.
                    """
    )
    public UserOnboardingResponse userOnboarding(
            Authentication authentication,
            @Parameter(description = "유저 온보딩 요청 바디")
            @Valid @RequestBody UserOnboardingRequest req
    ) {
        SimpleUserPrincipal principal = (SimpleUserPrincipal) authentication.getPrincipal();

        Long accountId = principal.getAccountId();
        String type = principal.getAccountType(); // USER / COMPANY / ADMIN

        if (!"USER".equals(type)) {
            throw new IllegalArgumentException("USER 계정만 유저 온보딩을 사용할 수 있습니다.");
        }

        return onboardingService.onboardUser(accountId, req);
    }

    /**
     * COMPANY 온보딩
     * - JWT로 로그인한 COMPANY 계정 기준
     */
    @PostMapping("/company")
    @Operation(
            summary = "회사 온보딩",
            description = """
                    COMPANY 계정이 회사 정보를 최초 등록/수정할 때 사용하는 API입니다.
                    
                    [입력값]
                    - companyName : 회사 이름
                    - description : 회사 소개/설명
                    - location    : 회사 위치
                    - logoUrl     : 로고 이미지 URL
                    """
    )
    public CompanyOnboardingResponse companyOnboarding(
            Authentication authentication,
            @Parameter(description = "회사 온보딩 요청 바디")
            @Valid @RequestBody CompanyOnboardingRequest req
    ) {
        SimpleUserPrincipal principal = (SimpleUserPrincipal) authentication.getPrincipal();

        Long accountId = principal.getAccountId();
        String type = principal.getAccountType(); // USER / COMPANY / ADMIN

        if (!"COMPANY".equals(type)) {
            throw new IllegalArgumentException("COMPANY 계정만 회사 온보딩을 사용할 수 있습니다.");
        }

        return onboardingService.onboardCompany(accountId, req);
    }
}
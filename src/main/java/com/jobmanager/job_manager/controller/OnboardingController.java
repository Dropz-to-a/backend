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
                    """
    )
    public UserOnboardingResponse userOnboarding(
            Authentication authentication,
            @Parameter(description = "유저 온보딩 요청 바디")
            @Valid @RequestBody UserOnboardingRequest req
    ) {
        SimpleUserPrincipal principal =
                (SimpleUserPrincipal) authentication.getPrincipal();

        Long accountId = principal.getAccountId();

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
                    COMPANY 계정이 회사 정보를 최초 등록할 때 사용하는 API입니다.
                    
                    [입력값]
                    - companyName     : 회사 이름
                    - zonecode        : 우편번호
                    - address         : 기본 주소
                    - detailAddress   : 상세 주소
                    - businessNumber  : 사업자 등록 번호
                    """
    )
    public CompanyOnboardingResponse companyOnboarding(
            Authentication authentication,
            @Parameter(description = "회사 온보딩 요청 바디")
            @Valid @RequestBody CompanyOnboardingRequest req
    ) {
        SimpleUserPrincipal principal =
                (SimpleUserPrincipal) authentication.getPrincipal();

        Long accountId = principal.getAccountId();

        return onboardingService.onboardCompany(accountId, req);
    }
}
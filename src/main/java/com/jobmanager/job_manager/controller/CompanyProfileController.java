// src/main/java/com/jobmanager/job_manager/controller/CompanyProfileController.java
package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.company.CompanyProfileQueryRequest;
import com.jobmanager.job_manager.dto.company.CompanyProfileResponse;
import com.jobmanager.job_manager.dto.company.CompanyProfileUpdateRequest;
import com.jobmanager.job_manager.global.jwt.JwtHeaderUtils;
import com.jobmanager.job_manager.global.jwt.JwtTokenProvider;
import com.jobmanager.job_manager.service.CompanyProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Company Profile")
@RestController
@RequestMapping("/api/company/profile")
@RequiredArgsConstructor
public class CompanyProfileController {

    private final CompanyProfileService service;
    private final JwtTokenProvider jwt;

    private Long currentCompanyId() {
        String token = JwtHeaderUtils.getTokenFromHeader();
        return jwt.getAccountId(token);
    }

    @GetMapping("/me")
    @Operation(summary = "내 회사 프로필 조회")
    public CompanyProfileResponse getMyProfile() {
        return service.getMyProfile(currentCompanyId());
    }

    @PatchMapping("/me")
    @Operation(summary = "내 회사 프로필 수정")
    public CompanyProfileResponse updateMyProfile(
            @RequestBody CompanyProfileUpdateRequest req
    ) {
        return service.updateMyProfile(currentCompanyId(), req);
    }

    @PostMapping("/public")
    @Operation(summary = "회사 공개 프로필 조회")
    public CompanyProfileResponse getPublicCompanyProfile(
            @RequestBody CompanyProfileQueryRequest req
    ) {
        return service.getPublicProfile(req.getAccountId());
    }

}
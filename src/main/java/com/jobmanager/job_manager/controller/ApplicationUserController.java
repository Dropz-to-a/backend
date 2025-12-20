package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.application.ApplicationCreateRequest;
import com.jobmanager.job_manager.dto.application.MyApplicationResponse;
import com.jobmanager.job_manager.global.jwt.SimpleUserPrincipal;
import com.jobmanager.job_manager.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "지원서 관리 (사용자)")
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class ApplicationUserController {

    private final ApplicationService applicationService;

    /**
     * 1️⃣ 채용 공고 지원
     */
    @Operation(
            summary = "채용 공고 지원",
            description = """
                    사용자가 회사의 채용 공고에 지원서를 제출합니다.
                    
                    - 지원서 양식은 고정입니다.
                    - 중복 지원은 불가능합니다.
                    """
    )
    @PostMapping
    public ResponseEntity<Void> apply(
            @AuthenticationPrincipal SimpleUserPrincipal principal,
            @RequestBody ApplicationCreateRequest request
    ) {
        applicationService.apply(
                principal.getAccountId(),
                request
        );
        return ResponseEntity.ok().build();
    }

    /**
     * 2️⃣ 내 지원서 목록 조회
     */
    @Operation(
            summary = "내 지원서 목록 조회",
            description = """
                    사용자가 자신이 제출한 지원서 목록을 조회합니다.
                    
                    - 합격 / 불합격 / 검토중 상태를 확인할 수 있습니다.
                    """
    )
    @GetMapping("/me")
    public ResponseEntity<List<MyApplicationResponse>> myApplications(
            @AuthenticationPrincipal SimpleUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                applicationService.getMyApplications(principal.getAccountId())
        );
    }
}
package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.application.ApplicationDetailResponse;
import com.jobmanager.job_manager.dto.application.CompanyApplicationResponse;
import com.jobmanager.job_manager.entity.application.ApplicationStatus;
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

@Tag(name = "지원서 관리 (회사)")
@RestController
@RequestMapping("/api/company/applications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('COMPANY')")
public class ApplicationCompanyController {

    private final ApplicationService applicationService;

    /**
     * 1️⃣ 공고로 들어온 지원서 전체 조회
     */
    @Operation(
            summary = "공고 지원서 목록 조회",
            description = """
                    회사가 특정 채용 공고로 들어온 지원서 목록을 조회합니다.
                    """
    )
    @GetMapping("/posting/{postingId}")
    public ResponseEntity<List<CompanyApplicationResponse>> getApplicationsByPosting(
            @PathVariable Long postingId,
            @AuthenticationPrincipal SimpleUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                applicationService.getApplicationsByPosting(
                        postingId,
                        principal.getAccountId()
                )
        );
    }

    /**
     * 2️⃣ 지원서 상세 조회
     */
    @Operation(
            summary = "지원서 상세 조회",
            description = """
                    회사가 특정 지원서의 상세 내용을 조회합니다.
                    """
    )
    @GetMapping("/{applicationId}")
    public ResponseEntity<ApplicationDetailResponse> getApplicationDetail(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal SimpleUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                applicationService.getApplicationDetail(
                        applicationId,
                        principal.getAccountId()
                )
        );
    }

    /**
     * 3️⃣ 합격 / 불합격 결정
     */
    @Operation(
            summary = "지원 결과 결정",
            description = """
                    회사가 지원서를 합격(HIRED) 또는 불합격(REJECTED) 처리합니다.
                    """
    )
    @PatchMapping("/{applicationId}/result")
    public ResponseEntity<Void> decideResult(
            @PathVariable Long applicationId,
            @RequestParam ApplicationStatus status,
            @AuthenticationPrincipal SimpleUserPrincipal principal
    ) {
        applicationService.decideResult(
                applicationId,
                status,
                principal.getAccountId()
        );
        return ResponseEntity.ok().build();
    }
}
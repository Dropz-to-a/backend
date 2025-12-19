package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.jobposting.JobPostingCreateRequest;
import com.jobmanager.job_manager.dto.jobposting.JobPostingHistoryResponse;
import com.jobmanager.job_manager.dto.jobposting.JobPostingManageResponse;
import com.jobmanager.job_manager.dto.jobposting.JobPostingUpdateRequest;
import com.jobmanager.job_manager.global.jwt.SimpleUserPrincipal;
import com.jobmanager.job_manager.service.JobPostingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "회사 채용 공고 관리",
        description = "회사 계정이 본인 공고를 등록, 조회, 수정, 마감하는 API"
)
@RestController
@RequestMapping("/api/company/job-postings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('COMPANY')")
public class JobPostingController {

    private final JobPostingService jobPostingService;

    @Operation(
            summary = "채용 공고 등록",
            description = "회사가 새로운 채용 공고를 등록합니다."
    )
    @PostMapping
    public ResponseEntity<Void> createPosting(
            @AuthenticationPrincipal SimpleUserPrincipal principal,
            @RequestBody JobPostingCreateRequest request
    ) {
        jobPostingService.createPosting(
                principal.getAccountId(),
                request
        );
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "내 공고 목록 조회",
            description = "회사 계정이 본인이 등록한 공고 목록을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<List<JobPostingManageResponse>> getMyPostings(
            @AuthenticationPrincipal SimpleUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                jobPostingService.getMyPostings(
                        principal.getAccountId()
                )
        );
    }

    @Operation(
            summary = "공고 기록 조회",
            description = "회사가 과거에 등록한 마감된 공고를 조회합니다."
    )
    @GetMapping("/history")
    public ResponseEntity<List<JobPostingHistoryResponse>> getHistory(
            @AuthenticationPrincipal SimpleUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                jobPostingService.getPostingHistory(
                        principal.getAccountId()
                )
        );
    }

    @Operation(
            summary = "채용 공고 수정",
            description = "회사가 본인 공고의 내용을 수정합니다. (마감된 공고는 수정 불가)"
    )
    @PatchMapping("/{postingId}")
    public ResponseEntity<Void> updatePosting(
            @PathVariable Long postingId,
            @AuthenticationPrincipal SimpleUserPrincipal principal,
            @RequestBody JobPostingUpdateRequest request
    ) {
        jobPostingService.updatePosting(
                postingId,
                principal.getAccountId(),
                request
        );
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "채용 공고 마감",
            description = "회사가 본인 공고를 마감 처리합니다. (실제 삭제는 아님)"
    )
    @PatchMapping("/{postingId}/close")
    public ResponseEntity<Void> closePosting(
            @PathVariable Long postingId,
            @AuthenticationPrincipal SimpleUserPrincipal principal
    ) {
        jobPostingService.closePosting(
                postingId,
                principal.getAccountId()
        );
        return ResponseEntity.ok().build();
    }
}

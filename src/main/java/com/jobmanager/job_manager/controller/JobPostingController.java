package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.jobposting.JobPostingHistoryResponse;
import com.jobmanager.job_manager.dto.jobposting.JobPostingManageResponse;
import com.jobmanager.job_manager.service.JobPostingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 회사 계정 전용 채용 공고 관리 API
 */
@Tag(
        name = "회사 채용 공고 관리",
        description = "회사 계정이 등록한 채용 공고를 관리하는 API"
)
@RestController
@RequestMapping("/api/company/job-postings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('COMPANY')")
public class JobPostingController {

    private final JobPostingService jobPostingService;

    @Operation(
            summary = "내 공고 목록 조회",
            description = """
                    회사 계정이 등록한 채용 공고 목록을 조회합니다.

                    - 모집 중(OPEN), 마감(CLOSED) 공고를 모두 조회합니다.
                    - 각 공고별 지원자 수가 함께 반환됩니다.
                    """
    )
    @GetMapping
    public ResponseEntity<List<JobPostingManageResponse>> getMyPostings(
            @RequestAttribute("companyId") Long companyId
    ) {
        return ResponseEntity.ok(
                jobPostingService.getMyPostings(companyId)
        );
    }

    @Operation(
            summary = "공고 기록 조회",
            description = """
                    회사 계정이 과거에 등록했던 채용 공고 기록을 조회합니다.

                    - 마감된(CLOSED) 공고만 조회됩니다.
                    - 최신 마감 순으로 정렬됩니다.
                    """
    )
    @GetMapping("/history")
    public ResponseEntity<List<JobPostingHistoryResponse>> getHistory(
            @RequestAttribute("companyId") Long companyId
    ) {
        return ResponseEntity.ok(
                jobPostingService.getPostingHistory(companyId)
        );
    }

    @Operation(
            summary = "채용 공고 삭제",
            description = """
                    채용 공고를 삭제 처리합니다.

                    - 실제 DB 삭제는 수행하지 않습니다.
                    - 공고 상태를 CLOSED 로 변경하여 기록을 유지합니다.
                    - 지원자가 1명이라도 존재하는 경우 삭제할 수 없습니다.
                    """
    )
    @PatchMapping("/{postingId}/close")
    public ResponseEntity<Void> closePosting(
            @PathVariable Long postingId,
            @RequestAttribute("companyId") Long companyId
    ) {
        jobPostingService.closePosting(postingId, companyId);
        return ResponseEntity.ok().build();
    }
}

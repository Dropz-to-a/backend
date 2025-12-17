package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.jobposting.JobPostingHistoryResponse;
import com.jobmanager.job_manager.dto.jobposting.JobPostingManageResponse;
import com.jobmanager.job_manager.service.JobPostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/company/job-postings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('COMPANY')")
public class JobPostingController {

    private final JobPostingService jobPostingService;

    /**
     * 내 공고 관리
     */
    @GetMapping
    public ResponseEntity<List<JobPostingManageResponse>> getMyPostings(
            @RequestAttribute("companyId") Long companyId
    ) {
        return ResponseEntity.ok(
                jobPostingService.getMyPostings(companyId)
        );
    }

    /**
     * 공고 기록 조회
     */
    @GetMapping("/history")
    public ResponseEntity<List<JobPostingHistoryResponse>> getHistory(
            @RequestAttribute("companyId") Long companyId
    ) {
        return ResponseEntity.ok(
                jobPostingService.getPostingHistory(companyId)
        );
    }

    /**
     * 공고 삭제 (CLOSED 처리)
     */
    @PatchMapping("/{postingId}/close")
    public ResponseEntity<Void> closePosting(
            @PathVariable Long postingId,
            @RequestAttribute("companyId") Long companyId
    ) {
        jobPostingService.closePosting(postingId, companyId);
        return ResponseEntity.ok().build();
    }
}

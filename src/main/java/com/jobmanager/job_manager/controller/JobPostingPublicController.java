package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.jobposting.JobPostingPublicResponse;
import com.jobmanager.job_manager.service.JobPostingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "공개 채용 공고",
        description = "지원자 및 비로그인 사용자를 위한 채용 공고 조회 API"
)
@RestController
@RequestMapping("/api/job-postings")
@RequiredArgsConstructor
public class JobPostingPublicController {

    private final JobPostingService jobPostingService;

    @Operation(
            summary = "채용 공고 전체 조회",
            description = "모집 중인 채용 공고를 전체 조회합니다."
    )
    @GetMapping
    public ResponseEntity<List<JobPostingPublicResponse>> getPublicPostings() {
        return ResponseEntity.ok(
                jobPostingService.getPublicPostings()
        );
    }
}

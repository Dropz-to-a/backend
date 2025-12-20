package com.jobmanager.job_manager.dto.jobposting;

import com.jobmanager.job_manager.entity.jobposting.JobPosting;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class JobPostingHistoryResponse {

    private Long postingId;
    private String title;
    private LocalDateTime closedAt;
    private long applicationCount;

    public static JobPostingHistoryResponse from(
            JobPosting posting,
            long applicationCount
    ) {
        return JobPostingHistoryResponse.builder()
                .postingId(posting.getId())
                .title(posting.getTitle())
                .closedAt(posting.getClosedAt())
                .applicationCount(applicationCount)
                .build();
    }
}
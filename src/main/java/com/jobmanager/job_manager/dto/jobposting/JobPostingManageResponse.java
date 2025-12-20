package com.jobmanager.job_manager.dto.jobposting;

import com.jobmanager.job_manager.entity.jobposting.JobPosting;
import com.jobmanager.job_manager.entity.jobposting.JobPostingStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JobPostingManageResponse {

    private Long postingId;
    private String title;
    private JobPostingStatus status;
    private long applicationCount;

    public static JobPostingManageResponse from(
            JobPosting posting,
            long applicationCount
    ) {
        return JobPostingManageResponse.builder()
                .postingId(posting.getId())
                .title(posting.getTitle())
                .status(posting.getStatus())
                .applicationCount(applicationCount)
                .build();
    }
}
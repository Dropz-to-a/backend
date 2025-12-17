package com.jobmanager.job_manager.dto.jobposting;

import com.jobmanager.job_manager.entity.jobposting.JobPosting;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobPostingHistoryResponse {

    private Long postingId;
    private String title;
    private LocalDateTime closedAt;
    private int totalApplicants;

    public static JobPostingHistoryResponse from(
            JobPosting posting,
            int totalApplicants
    ) {
        return JobPostingHistoryResponse.builder()
                .postingId(posting.getId())
                .title(posting.getTitle())
                .closedAt(posting.getClosedAt())
                .totalApplicants(totalApplicants)
                .build();
    }
}

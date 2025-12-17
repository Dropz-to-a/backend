package com.jobmanager.job_manager.dto.jobposting;

import com.jobmanager.job_manager.entity.jobposting.JobPosting;
import com.jobmanager.job_manager.entity.jobposting.JobPostingStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobPostingManageResponse {

    private Long postingId;
    private String title;
    private String description;
    private String locationText;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private JobPostingStatus status;
    private int applicantCount;

    public static JobPostingManageResponse from(
            JobPosting posting,
            int applicantCount
    ) {
        return JobPostingManageResponse.builder()
                .postingId(posting.getId())
                .title(posting.getTitle())
                .description(posting.getDescription())
                .locationText(posting.getLocationText())
                .salaryMin(posting.getSalaryMin())
                .salaryMax(posting.getSalaryMax())
                .status(posting.getStatus())
                .applicantCount(applicantCount)
                .build();
    }
}

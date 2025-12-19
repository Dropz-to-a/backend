package com.jobmanager.job_manager.dto.jobposting;

import com.jobmanager.job_manager.entity.jobposting.EmploymentType;
import com.jobmanager.job_manager.entity.jobposting.JobPosting;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class JobPostingPublicResponse {

    private Long postingId;
    private String title;
    private String companyName;
    private EmploymentType employmentType;
    private String locationText;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;

    public static JobPostingPublicResponse from(
            JobPosting posting,
            String companyName
    ) {
        return JobPostingPublicResponse.builder()
                .postingId(posting.getId())
                .title(posting.getTitle())
                .companyName(companyName)
                .employmentType(posting.getEmploymentType())
                .locationText(posting.getLocationText())
                .salaryMin(posting.getSalaryMin())
                .salaryMax(posting.getSalaryMax())
                .build();
    }
}

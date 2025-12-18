package com.jobmanager.job_manager.dto.jobposting;

import com.jobmanager.job_manager.entity.jobposting.EmploymentType;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class JobPostingCreateRequest {

    private String title;
    private String description;
    private EmploymentType employmentType;
    private String locationText;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
}

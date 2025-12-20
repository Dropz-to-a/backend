package com.jobmanager.job_manager.dto.application;

import com.jobmanager.job_manager.entity.application.Application;
import com.jobmanager.job_manager.entity.application.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CompanyApplicationResponse {

    private Long applicationId;
    private Long applicantId;
    private String applicantName;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;

    public static CompanyApplicationResponse from(Application app) {
        return CompanyApplicationResponse.builder()
                .applicationId(app.getId())
                .applicantId(app.getApplicantId())
                .applicantName(app.getApplicantName())
                .status(app.getStatus())
                .appliedAt(app.getCreatedAt())
                .build();
    }
}
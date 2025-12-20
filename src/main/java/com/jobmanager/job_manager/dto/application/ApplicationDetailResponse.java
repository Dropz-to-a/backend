package com.jobmanager.job_manager.dto.application;

import com.jobmanager.job_manager.entity.application.Application;
import com.jobmanager.job_manager.entity.application.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApplicationDetailResponse {

    private Long applicationId;
    private Long postingId;
    private Long applicantId;

    private String applicantName;
    private String motivation;

    private ApplicationStatus status;
    private String memoInternal;
    private LocalDateTime appliedAt;

    public static ApplicationDetailResponse from(Application app) {
        return ApplicationDetailResponse.builder()
                .applicationId(app.getId())
                .postingId(app.getPostingId())
                .applicantId(app.getApplicantId())
                .applicantName(app.getApplicantName())
                .motivation(app.getMotivation())
                .status(app.getStatus())
                .memoInternal(app.getMemoInternal())
                .appliedAt(app.getCreatedAt())
                .build();
    }
}
package com.jobmanager.job_manager.dto.application;

import com.jobmanager.job_manager.entity.application.Application;
import com.jobmanager.job_manager.entity.application.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyApplicationResponse {

    private Long applicationId;
    private Long postingId;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;

    public static MyApplicationResponse from(Application app) {
        return MyApplicationResponse.builder()
                .applicationId(app.getId())
                .postingId(app.getPostingId())
                .status(app.getStatus())
                .appliedAt(app.getCreatedAt())
                .build();
    }
}
package com.jobmanager.job_manager.dto.application;

import com.jobmanager.job_manager.entity.application.Application;
import com.jobmanager.job_manager.entity.application.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ApplicationDetailResponse {

    private Long applicationId;
    private Long postingId;
    private Long writerId;

    // 기본 정보
    private String name;
    private String email;
    private String phone;
    private String address;

    // 학력
    private String educationSchool;
    private String educationMajor;
    private String educationDegree;
    private LocalDate educationStartDate;
    private LocalDate educationEndDate;
    private boolean educationGraduated;

    // 대외활동
    private String activities;

    // 자기소개
    private String introduction;
    private String motivation;
    private String personality;
    private String futureGoal;

    private ApplicationStatus status;
    private LocalDateTime appliedAt;

    public static ApplicationDetailResponse from(Application app) {
        return ApplicationDetailResponse.builder()
                .applicationId(app.getId())
                .postingId(app.getPostingId())
                .writerId(app.getWriterId())

                .name(app.getName())
                .email(app.getEmail())
                .phone(app.getPhone())
                .address(app.getAddress())

                .educationSchool(app.getEducationSchool())
                .educationMajor(app.getEducationMajor())
                .educationDegree(app.getEducationDegree())
                .educationStartDate(app.getEducationStartDate())
                .educationEndDate(app.getEducationEndDate())
                .educationGraduated(app.isEducationGraduated())

                .activities(app.getActivities())

                .introduction(app.getIntroduction())
                .motivation(app.getMotivation())
                .personality(app.getPersonality())
                .futureGoal(app.getFutureGoal())

                .status(app.getStatus())
                .appliedAt(app.getCreatedAt())
                .build();
    }
}
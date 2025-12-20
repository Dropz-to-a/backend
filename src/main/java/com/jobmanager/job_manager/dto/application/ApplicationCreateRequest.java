package com.jobmanager.job_manager.dto.application;

import lombok.Getter;

@Getter
public class ApplicationCreateRequest {

    private Long postingId;
    private String applicantName;
    private String motivation;
}
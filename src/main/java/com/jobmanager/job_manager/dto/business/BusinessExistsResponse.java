package com.jobmanager.job_manager.dto.business;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BusinessExistsResponse {
    private boolean exists;
    private String companyName;
}
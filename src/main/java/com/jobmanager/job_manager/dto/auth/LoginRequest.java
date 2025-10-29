package com.jobmanager.job_manager.dto.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String id;        // username 또는 email
    private String password;
}

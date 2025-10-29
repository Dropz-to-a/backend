package com.jobmanager.job_manager.dto.auth;

import lombok.Data;

@Data
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";

    public AuthResponse(String token) {
        this.accessToken = token;
    }
}

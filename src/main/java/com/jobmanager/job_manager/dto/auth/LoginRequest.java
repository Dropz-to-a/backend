package com.jobmanager.job_manager.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String id;        // username 또는 email
    private String password;  // 원문 비밀번호
}
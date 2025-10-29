package com.jobmanager.job_manager.dto.auth;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;  // 고유
    private String email;     // 선택 (NULL 허용)
    private String phone;     // 선택
    private String password;  // 평문 입력 -> bcrypt 저장
    private String roleCode;  // 선택: 기본 ROLE_USER
}

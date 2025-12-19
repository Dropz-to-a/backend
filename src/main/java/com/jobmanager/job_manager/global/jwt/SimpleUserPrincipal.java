package com.jobmanager.job_manager.global.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SimpleUserPrincipal {

    private Long accountId;
    private String accountType;     // USER / COMPANY / ADMIN
    private String role;            // ROLE_*
    private Boolean onboarded;      // 온보딩 여부
    private String companyName;     // USER 전용
    private String businessNumber;  // COMPANY 전용
}
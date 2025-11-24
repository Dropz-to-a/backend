package com.jobmanager.job_manager.global.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SimpleUserPrincipal {
    private Long accountId;     // accounts.id
    private String accountType; // USER / COMPANY
    private String role;        // ROLE_USER / ROLE_COMPANY
}

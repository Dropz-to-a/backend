package com.jobmanager.job_manager.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeResponse {

    private Long accountId;
    private String username;
    private String accountType;
    private String role;
}

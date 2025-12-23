// src/main/java/com/jobmanager/job_manager/dto/bankonboarding/BankAccountResponse.java
package com.jobmanager.job_manager.dto.bankonboarding;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BankAccountResponse {

    private Long accountId;
    private String role;
    private String bankName;
    private String bankAccountNumber;
}
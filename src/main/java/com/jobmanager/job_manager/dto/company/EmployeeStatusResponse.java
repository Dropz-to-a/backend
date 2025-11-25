package com.jobmanager.job_manager.dto.company;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeStatusResponse {

    private Long employeeAccountId;   // 직원 계정 ID
    private String status;            // WORKING, DONE, ON_LEAVE, NOT_STARTED
}

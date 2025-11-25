package com.jobmanager.job_manager.dto.company;

import lombok.Getter;
import jakarta.validation.constraints.NotNull;

@Getter
public class CompanyEmployeeAssignRequest {

    @NotNull
    private Long companyId;

    @NotNull
    private Long employeeAccountId;
}

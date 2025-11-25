package com.jobmanager.job_manager.dto.company;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompanyEmployeeAssignRequest {


    @NotNull
    private Long companyId;

    @NotNull
    private Long employeeAccountId;

    @Schema(description = "회사에 소속시킬 직원(사용자)의 accountId", example = "5")
    private Long employeeId;

}

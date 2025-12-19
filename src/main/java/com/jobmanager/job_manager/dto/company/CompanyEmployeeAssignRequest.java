package com.jobmanager.job_manager.dto.company;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompanyEmployeeAssignRequest {

    @NotNull
    @Schema(description = "회사에 소속시킬 직원(USER)의 accountId", example = "5")
    private Long employeeId;
}
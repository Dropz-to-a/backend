package com.jobmanager.job_manager.dto.company;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CompanyProfileQueryRequest {

    @Schema(description = "조회할 COMPANY 계정의 accountId", example = "8")
    @NotNull
    private Long accountId;
}
package com.jobmanager.job_manager.dto.company;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "회사 직원 목록 응답 DTO")
public class CompanyEmployeeResponse {

    @Schema(description = "직원 accountId", example = "5")
    private Long employeeAccountId;

    @Schema(description = "직원 이름", example = "홍길동")
    private String name;

    @Schema(description = "부서 ID (없으면 null)", example = "1", nullable = true)
    private Long teamId;

    @Schema(description = "부서명 (없으면 null)", example = "개발팀", nullable = true)
    private String teamName;

    @Schema(description = "입사일시", example = "2025-11-20T09:00:00")
    private String joinedAt;
}
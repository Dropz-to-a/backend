package com.jobmanager.job_manager.dto.company;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "회사 부서 응답 DTO")
public class CompanyTeamResponse {

    @Schema(description = "부서 ID", example = "10")
    private Long teamId;

    @Schema(description = "부서명", example = "개발팀")
    private String name;

    @Schema(description = "부서 설명", example = "백엔드/프론트엔드 개발 담당")
    private String description;
}
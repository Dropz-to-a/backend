package com.jobmanager.job_manager.dto.company;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTeamRequest {

    @NotBlank
    @Schema(description = "부서명", example = "개발팀")
    private String name;

    @Schema(description = "부서 설명", example = "백엔드/프론트엔드 개발 조직")
    private String description;
}
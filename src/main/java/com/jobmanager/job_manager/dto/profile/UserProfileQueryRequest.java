package com.jobmanager.job_manager.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserProfileQueryRequest {

    @Schema(description = "조회할 USER 계정의 accountId", example = "12")
    @NotNull
    private Long accountId;
}
package com.jobmanager.job_manager.dto.company;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AssignTeamRequest {

    @NotNull
    private Long employeeId;

    @NotNull
    private Long teamId;
}
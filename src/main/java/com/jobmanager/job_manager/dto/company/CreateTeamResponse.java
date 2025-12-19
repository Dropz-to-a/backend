package com.jobmanager.job_manager.dto.company;

import com.jobmanager.job_manager.entity.CompanyTeam;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateTeamResponse {

    private Long teamId;
    private String name;
    private String description;

    public static CreateTeamResponse from(CompanyTeam team) {
        return CreateTeamResponse.builder()
                .teamId(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .build();
    }
}
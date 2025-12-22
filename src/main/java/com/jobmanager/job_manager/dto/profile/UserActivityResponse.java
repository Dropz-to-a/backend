package com.jobmanager.job_manager.dto.profile;

import com.jobmanager.job_manager.entity.UserActivity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserActivityResponse {

    private Long id;
    private String userPosition;
    private String companyName;
    private String description;
    private String startDate;
    private String endDate;

    public static UserActivityResponse from(UserActivity e) {
        return UserActivityResponse.builder()
                .id(e.getId())
                .userPosition(e.getUserPosition())
                .companyName(e.getCompanyName())
                .description(e.getDescription())
                .startDate(e.getStartDate().toString())
                .endDate(e.getEndDate() != null ? e.getEndDate().toString() : null)
                .build();
    }
}
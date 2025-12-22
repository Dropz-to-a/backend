package com.jobmanager.job_manager.dto.profile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserActivityRequest {

    private String userPosition;
    private String companyName;
    private String description;

    private String startDate; // yyyy-MM-dd
    private String endDate;   // null 가능
}

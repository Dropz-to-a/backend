// src/main/java/com/jobmanager/job_manager/dto/onboarding/UserOnboardingResponse.java
package com.jobmanager.job_manager.dto.onboarding;

import com.jobmanager.job_manager.entity.UserForm;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserOnboardingResponse {

    private Long accountId;
    private String realName;
    private String birth;
    private String address;
    private String detailAddress;
    private String zonecode;

    public static UserOnboardingResponse from(UserForm form) {
        return UserOnboardingResponse.builder()
                .accountId(form.getAccountId())
                .realName(form.getName())
                .birth(form.getBirth() != null ? form.getBirth().toString() : null)
                .address(form.getAddress())
                .detailAddress(form.getDetailaddress())
                .zonecode(form.getZonecode())
                .build();
    }
}
package com.jobmanager.job_manager.dto.profile;

import com.jobmanager.job_manager.entity.UserForm;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponse {

    private Long accountId;
    private String name;
    private String email;
    private String phone;
    private String birth;
    private String address;
    private String detailAddress;
    private String zonecode;

    private String height;
    private String weight;
    private String blood;
    private String education;
    private String military;
    private String license;
    private String foreignLang;
    private String activity;
    private String family;
    private String hobby;
    private String motivation;

    public static UserProfileResponse from(UserForm f) {
        return UserProfileResponse.builder()
                .accountId(f.getAccountId())
                .name(f.getName())
                .email(f.getEmail())
                .phone(f.getPhone())
                .birth(f.getBirth() != null ? f.getBirth().toString() : null)
                .address(f.getAddress())
                .detailAddress(f.getDetailAddress())
                .zonecode(f.getZonecode())
                .height(f.getHeight())
                .weight(f.getWeight())
                .blood(f.getBlood())
                .education(f.getEducation())
                .military(f.getMilitary())
                .license(f.getLicense())
                .foreignLang(f.getForeignLang())
                .activity(f.getActivity())
                .hobby(f.getHobby())
                .motivation(f.getMotivation())
                .build();
    }
}
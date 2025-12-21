package com.jobmanager.job_manager.dto.profile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobmanager.job_manager.entity.UserForm;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

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
    private List<String> skills;
    private List<String> license;
    private List<String> foreignLang;
    private String activity;
    private String motivation;

    private static final ObjectMapper om = new ObjectMapper();

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
                .skills(readList(f.getSkills()))
                .license(readList(f.getLicense()))
                .foreignLang(readList(f.getForeignLang()))
                .activity(f.getActivity())
                .motivation(f.getMotivation())
                .build();
    }

    private static List<String> readList(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return om.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}
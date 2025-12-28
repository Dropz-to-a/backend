package com.jobmanager.job_manager.dto.profile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobmanager.job_manager.entity.UserActivity;
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
    private List<LicenseDto> licenses;    // 변경
    private List<String> foreignLangs;

    /** 경력 목록 */
    private List<UserActivityResponse> activities;

    private String motivation;

    private static final ObjectMapper om = new ObjectMapper();

    public static UserProfileResponse from(
            UserForm f,
            List<UserActivity> activities
    ) {
        return UserProfileResponse.builder()
                .accountId(f.getAccountId())
                .name(f.getName())
                .email(f.getEmail())
                .phone(f.getPhone())
                .birth(f.getBirth() != null ? f.getBirth().toString() : null)
                .address(f.getAddress())
                .detailAddress(f.getDetailAddress())
                .zonecode(f.getZonecode())
                .skills(readStringList(f.getSkills()))
                .licenses(readLicenseList(f.getLicenses()))   // 변경
                .foreignLangs(readStringList(f.getForeignLangs()))
                .activities(
                        activities.stream()
                                .map(UserActivityResponse::from)
                                .toList()
                )
                .motivation(f.getMotivation())
                .build();
    }

    /** 기존 String 리스트용 (skills, foreignLangs) */
    private static List<String> readStringList(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return om.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    /** 자격증 전용 */
    private static List<LicenseDto> readLicenseList(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return om.readValue(json, new TypeReference<List<LicenseDto>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}

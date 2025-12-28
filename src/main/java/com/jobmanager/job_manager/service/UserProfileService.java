package com.jobmanager.job_manager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobmanager.job_manager.dto.profile.UserProfileResponse;
import com.jobmanager.job_manager.dto.profile.UserProfileUpdateRequest;
import com.jobmanager.job_manager.entity.UserActivity;
import com.jobmanager.job_manager.entity.UserForm;
import com.jobmanager.job_manager.global.exception.errorcodes.ProfileErrorCode;
import com.jobmanager.job_manager.global.exception.exceptions.ProfileException;
import com.jobmanager.job_manager.repository.UserActivityRepository;
import com.jobmanager.job_manager.repository.UserFormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileService {

    private final UserFormRepository userFormRepository;
    private final UserActivityRepository userActivityRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /** =========================
     *  내 프로필 조회
     *  ========================= */
    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(Long accountId) {

        UserForm form = userFormRepository.findById(accountId)
                .orElseThrow(() ->
                        new ProfileException(ProfileErrorCode.PROFILE_NOT_ONBOARDED)
                );

        List<UserActivity> activities =
                userActivityRepository.findByAccountIdOrderByStartDateDesc(accountId);

        return UserProfileResponse.from(form, activities);
    }

    /** =========================
     *  내 프로필 수정
     *  ========================= */
    public UserProfileResponse updateMyProfile(
            Long accountId,
            UserProfileUpdateRequest req
    ) {

        if (isEmptyRequest(req)) {
            throw new ProfileException(ProfileErrorCode.PROFILE_UPDATE_EMPTY);
        }

        UserForm form = userFormRepository.findById(accountId)
                .orElseThrow(() ->
                        new ProfileException(ProfileErrorCode.PROFILE_NOT_ONBOARDED)
                );

        if (req.getName() != null) form.setName(req.getName());
        if (req.getEmail() != null) form.setEmail(req.getEmail());
        if (req.getPhone() != null) form.setPhone(req.getPhone());

        if (req.getSkills() != null) {
            form.setSkills(toJson(req.getSkills()));
        }

        if (req.getLicenses() != null) {
            form.setLicenses(toJson(req.getLicenses()));
        }

        if (req.getForeignLangs() != null) {
            form.setForeignLangs(toJson(req.getForeignLangs()));
        }

        if (req.getMotivation() != null) {
            form.setMotivation(req.getMotivation());
        }

        List<UserActivity> activities =
                userActivityRepository.findByAccountIdOrderByStartDateDesc(accountId);

        return UserProfileResponse.from(form, activities);
    }

    /** =========================
     *  공개 프로필 조회
     *  ========================= */
    @Transactional(readOnly = true)
    public UserProfileResponse getPublicProfile(Long accountId) {

        UserForm form = userFormRepository.findById(accountId)
                .orElseThrow(() ->
                        new ProfileException(ProfileErrorCode.PROFILE_NOT_ONBOARDED)
                );

        List<UserActivity> activities =
                userActivityRepository.findByAccountIdOrderByStartDateDesc(accountId);

        return UserProfileResponse.from(form, activities);
    }

    /** =========================
     *  내부 유틸
     *  ========================= */

    private boolean isEmptyRequest(UserProfileUpdateRequest req) {
        return req.getName() == null
                && req.getEmail() == null
                && req.getPhone() == null
                && req.getSkills() == null
                && req.getLicenses() == null
                && req.getForeignLangs() == null
                && req.getMotivation() == null;
    }

    private String toJson(List<String> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            throw new IllegalStateException("List JSON 변환 실패", e);
        }
    }
}

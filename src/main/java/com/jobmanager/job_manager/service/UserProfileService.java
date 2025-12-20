package com.jobmanager.job_manager.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobmanager.job_manager.dto.profile.UserProfileResponse;
import com.jobmanager.job_manager.dto.profile.UserProfileUpdateRequest;
import com.jobmanager.job_manager.entity.UserForm;
import com.jobmanager.job_manager.global.exception.errorcodes.ProfileErrorCode;
import com.jobmanager.job_manager.global.exception.exceptions.ProfileException;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 내 프로필 조회 */
    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(Long accountId) {

        UserForm form = userFormRepository.findById(accountId)
                .orElseThrow(() ->
                        new ProfileException(ProfileErrorCode.PROFILE_NOT_ONBOARDED)
                );

        return UserProfileResponse.from(form);
    }

    /** 내 프로필 수정 */
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

        if (req.getLicense() != null) {
            form.setLicense(toJson(req.getLicense()));
        }

        if (req.getForeignLang() != null) form.setForeignLang(req.getForeignLang());
        if (req.getActivity() != null) form.setActivity(req.getActivity());
        if (req.getMotivation() != null) form.setMotivation(req.getMotivation());

        return UserProfileResponse.from(form);
    }

    /** PATCH 요청이 비어있는지 체크 */
    private boolean isEmptyRequest(UserProfileUpdateRequest req) {
        return req.getName() == null
                && req.getEmail() == null
                && req.getPhone() == null
                && req.getSkills() == null
                && req.getLicense() == null
                && req.getForeignLang() == null
                && req.getActivity() == null
                && req.getMotivation() == null;
    }

    /** List → JSON 문자열 변환 */
    private String toJson(List<String> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            throw new IllegalStateException("List JSON 변환 실패", e);
        }
    }

    /** JSON 문자열 → List 변환 (필요 시 확장용) */
    @SuppressWarnings("unused")
    private List<String> fromJson(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}
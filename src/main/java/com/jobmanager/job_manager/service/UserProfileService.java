package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.profile.UserProfileResponse;
import com.jobmanager.job_manager.dto.profile.UserProfileUpdateRequest;
import com.jobmanager.job_manager.entity.UserForm;
import com.jobmanager.job_manager.global.exception.errorcodes.ProfileErrorCode;
import com.jobmanager.job_manager.global.exception.exceptions.ProfileException;
import com.jobmanager.job_manager.repository.UserFormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileService {

    private final UserFormRepository userFormRepository;

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

        // PATCH 요청이 완전히 비어있는 경우
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

        if (req.getHeight() != null) form.setHeight(req.getHeight());
        if (req.getWeight() != null) form.setWeight(req.getWeight());
        if (req.getBlood() != null) form.setBlood(req.getBlood());
        if (req.getEducation() != null) form.setEducation(req.getEducation());
        if (req.getMilitary() != null) form.setMilitary(req.getMilitary());
        if (req.getLicense() != null) form.setLicense(req.getLicense());
        if (req.getForeignLang() != null) form.setForeignLang(req.getForeignLang());
        if (req.getActivity() != null) form.setActivity(req.getActivity());
        if (req.getHobby() != null) form.setHobby(req.getHobby());
        if (req.getMotivation() != null) form.setMotivation(req.getMotivation());

        return UserProfileResponse.from(form);
    }

    /** PATCH 요청이 비어있는지 체크 */
    private boolean isEmptyRequest(UserProfileUpdateRequest req) {
        return req.getName() == null
                && req.getEmail() == null
                && req.getPhone() == null
                && req.getHeight() == null
                && req.getWeight() == null
                && req.getBlood() == null
                && req.getEducation() == null
                && req.getMilitary() == null
                && req.getLicense() == null
                && req.getForeignLang() == null
                && req.getActivity() == null
                && req.getHobby() == null
                && req.getMotivation() == null;
    }
}
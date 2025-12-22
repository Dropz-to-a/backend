package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.profile.UserActivityRequest;
import com.jobmanager.job_manager.dto.profile.UserActivityResponse;
import com.jobmanager.job_manager.entity.UserActivity;
import com.jobmanager.job_manager.global.exception.errorcodes.ProfileErrorCode;
import com.jobmanager.job_manager.global.exception.exceptions.ProfileException;
import com.jobmanager.job_manager.repository.UserActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class UserActivityService {

    private final UserActivityRepository userActivityRepository;

    /** =========================
     *  경력 추가
     *  ========================= */
    public UserActivityResponse create(Long accountId, UserActivityRequest req) {

        LocalDate startDate = LocalDate.parse(req.getStartDate());
        LocalDate endDate =
                req.getEndDate() != null ? LocalDate.parse(req.getEndDate()) : null;

        // 날짜 검증
        if (endDate != null && startDate.isAfter(endDate)) {
            throw new ProfileException(ProfileErrorCode.PROFILE_INVALID_VALUE);
        }

        UserActivity activity = UserActivity.builder()
                .accountId(accountId)
                .userPosition(req.getUserPosition())
                .companyName(req.getCompanyName())
                .description(req.getDescription())
                .startDate(startDate)
                .endDate(endDate)
                .build();

        return UserActivityResponse.from(
                userActivityRepository.save(activity)
        );
    }

    /** =========================
     *  경력 수정
     *  ========================= */
    public UserActivityResponse update(
            Long accountId,
            Long activityId,
            UserActivityRequest req
    ) {

        UserActivity activity = userActivityRepository.findById(activityId)
                .orElseThrow(() ->
                        new ProfileException(ProfileErrorCode.PROFILE_NOT_ONBOARDED)
                );

        // 본인 경력만 수정 가능
        if (!activity.getAccountId().equals(accountId)) {
            throw new ProfileException(ProfileErrorCode.PROFILE_ACCESS_FORBIDDEN);
        }

        if (req.getUserPosition() != null) {
            activity.setUserPosition(req.getUserPosition());
        }
        if (req.getCompanyName() != null) {
            activity.setCompanyName(req.getCompanyName());
        }
        if (req.getDescription() != null) {
            activity.setDescription(req.getDescription());
        }
        if (req.getStartDate() != null) {
            activity.setStartDate(LocalDate.parse(req.getStartDate()));
        }
        if (req.getEndDate() != null) {
            activity.setEndDate(LocalDate.parse(req.getEndDate()));
        }

        // 수정 후 날짜 검증
        if (activity.getEndDate() != null &&
                activity.getStartDate().isAfter(activity.getEndDate())) {
            throw new ProfileException(ProfileErrorCode.PROFILE_INVALID_VALUE);
        }

        return UserActivityResponse.from(activity);
    }

    /** =========================
     *  경력 삭제
     *  ========================= */
    public void delete(Long accountId, Long activityId) {

        UserActivity activity = userActivityRepository.findById(activityId)
                .orElseThrow(() ->
                        new ProfileException(ProfileErrorCode.PROFILE_NOT_ONBOARDED)
                );

        // 본인 경력만 삭제 가능
        if (!activity.getAccountId().equals(accountId)) {
            throw new ProfileException(ProfileErrorCode.PROFILE_ACCESS_FORBIDDEN);
        }

        userActivityRepository.delete(activity);
    }
}
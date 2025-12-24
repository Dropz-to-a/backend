package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.profile.*;
import com.jobmanager.job_manager.entity.UserFamily;
import com.jobmanager.job_manager.global.exception.errorcodes.ProfileErrorCode;
import com.jobmanager.job_manager.global.exception.exceptions.ProfileException;
import com.jobmanager.job_manager.global.jwt.SimpleUserPrincipal;
import com.jobmanager.job_manager.service.UserActivityService;
import com.jobmanager.job_manager.service.UserFamilyService;
import com.jobmanager.job_manager.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "UserProfile", description = "유저 프로필 API")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserFamilyService userFamilyService;
    private final UserActivityService userActivityService;

    /** =========================
     *  인증 + USER 체크
     *  ========================= */
    private SimpleUserPrincipal getUser(Authentication authentication) {
        if (authentication == null ||
                !(authentication.getPrincipal() instanceof SimpleUserPrincipal principal) ||
                !"USER".equals(principal.getAccountType())) {

            throw new ProfileException(ProfileErrorCode.PROFILE_ACCESS_FORBIDDEN);
        }
        return principal;
    }

    /** =========================
     *  프로필
     *  ========================= */

    @GetMapping("/me")
    @Operation(summary = "내 프로필 조회")
    public UserProfileResponse getMyProfile(Authentication authentication) {
        SimpleUserPrincipal principal = getUser(authentication);
        return userProfileService.getMyProfile(principal.getAccountId());
    }

    @PatchMapping("/me")
    @Operation(summary = "내 프로필 수정")
    public UserProfileResponse updateMyProfile(
            Authentication authentication,
            @RequestBody UserProfileUpdateRequest req
    ) {
        SimpleUserPrincipal principal = getUser(authentication);
        return userProfileService.updateMyProfile(principal.getAccountId(), req);
    }

    /**
     * ✅ 수정된 부분
     * POST + Body → GET + Query Param
     */
    @GetMapping("/public")
    @Operation(summary = "유저 공개 프로필 조회")
    public UserProfileResponse getPublicUserProfile(
            @Parameter(description = "조회할 유저 accountId", required = true)
            @RequestParam("id") Long accountId
    ) {
        return userProfileService.getPublicProfile(accountId);
    }

    /** =========================
     *  가족 정보
     *  ========================= */

    @GetMapping("/me/family")
    @Operation(summary = "내 가족 정보 목록 조회")
    public List<UserFamily> getMyFamilies(Authentication authentication) {
        SimpleUserPrincipal principal = getUser(authentication);
        return userFamilyService.getFamilies(principal.getAccountId());
    }

    @PostMapping("/me/family")
    @Operation(summary = "가족 정보 추가")
    public void addFamily(
            Authentication authentication,
            @RequestParam String role,
            @RequestParam String name
    ) {
        SimpleUserPrincipal principal = getUser(authentication);
        userFamilyService.addFamily(principal.getAccountId(), role, name);
    }

    @DeleteMapping("/me/family/{familyId}")
    @Operation(summary = "가족 정보 삭제")
    public void deleteFamily(
            Authentication authentication,
            @PathVariable Long familyId
    ) {
        SimpleUserPrincipal principal = getUser(authentication);
        userFamilyService.deleteFamily(principal.getAccountId(), familyId);
    }

    /** =========================
     *  활동(경력) 관리
     *  ========================= */

    @PostMapping("/me/activities")
    @Operation(summary = "경력 추가")
    public UserActivityResponse addActivity(
            Authentication authentication,
            @RequestBody UserActivityRequest req
    ) {
        SimpleUserPrincipal principal = getUser(authentication);
        return userActivityService.create(principal.getAccountId(), req);
    }

    @PatchMapping("/me/activities/{activityId}")
    @Operation(summary = "경력 수정")
    public UserActivityResponse updateActivity(
            Authentication authentication,
            @PathVariable Long activityId,
            @RequestBody UserActivityRequest req
    ) {
        SimpleUserPrincipal principal = getUser(authentication);
        return userActivityService.update(
                principal.getAccountId(),
                activityId,
                req
        );
    }

    @DeleteMapping("/me/activities/{activityId}")
    @Operation(summary = "경력 삭제")
    public void deleteActivity(
            Authentication authentication,
            @PathVariable Long activityId
    ) {
        SimpleUserPrincipal principal = getUser(authentication);
        userActivityService.delete(principal.getAccountId(), activityId);
    }
}

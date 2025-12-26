package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.entity.UserFamily;
import com.jobmanager.job_manager.global.exception.errorcodes.UserFamilyErrorCode;
import com.jobmanager.job_manager.global.exception.exceptions.UserFamilyException;
import com.jobmanager.job_manager.repository.UserFamilyRepository;
import com.jobmanager.job_manager.repository.UserFormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserFamilyService {

    private final UserFamilyRepository userFamilyRepository;
    private final UserFormRepository userFormRepository;

    /**
     * 온보딩 여부 체크 (공통)
     */
    private void checkOnboarded(Long accountId) {
        if (!userFormRepository.existsById(accountId)) {
            throw new UserFamilyException(UserFamilyErrorCode.FAMILY_NOT_ONBOARDED);
        }
    }

    /**
     * 가족 정보 목록 조회
     */
    @Transactional(readOnly = true)
    public List<UserFamily> getFamilies(Long accountId) {
        checkOnboarded(accountId);
        return userFamilyRepository.findByAccountId(accountId);
    }

    /**
     * 가족 정보 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean hasFamily(Long accountId) {
        checkOnboarded(accountId);
        return userFamilyRepository.existsByAccountId(accountId);
    }

    /**
     * 가족 정보 추가
     */
    public void addFamily(
            Long accountId,
            String role,
            String name,
            Integer age,
            String job
    ) {
        checkOnboarded(accountId);

        if (role == null || role.isBlank() || name == null || name.isBlank()) {
            throw new UserFamilyException(UserFamilyErrorCode.FAMILY_INVALID_VALUE);
        }

        if (userFamilyRepository.existsByAccountIdAndRoleAndName(accountId, role, name)) {
            throw new UserFamilyException(UserFamilyErrorCode.FAMILY_ALREADY_EXISTS);
        }

        UserFamily family = UserFamily.builder()
                .accountId(accountId)
                .role(role)
                .name(name)
                .age(age)   // 추가
                .job(job)   // 추가
                .build();

        userFamilyRepository.save(family);
    }

    /**
     * 가족 정보 삭제 (단건)
     */
    public void deleteFamily(Long accountId, Long familyId) {
        checkOnboarded(accountId);

        UserFamily family = userFamilyRepository.findById(familyId)
                .orElseThrow(() ->
                        new UserFamilyException(UserFamilyErrorCode.FAMILY_NOT_FOUND)
                );

        if (!family.getAccountId().equals(accountId)) {
            throw new UserFamilyException(UserFamilyErrorCode.FAMILY_ACCESS_DENIED);
        }

        userFamilyRepository.delete(family);
    }

    /**
     * 가족 정보 전체 삭제 (필요 시)
     */
    public void deleteAllFamilies(Long accountId) {
        checkOnboarded(accountId);
        userFamilyRepository.deleteByAccountId(accountId);
    }
}
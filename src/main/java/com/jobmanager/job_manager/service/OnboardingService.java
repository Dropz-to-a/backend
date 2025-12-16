// src/main/java/com/jobmanager/job_manager/service/OnboardingService.java
package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.onboarding.*;
import com.jobmanager.job_manager.entity.Account;
import com.jobmanager.job_manager.entity.Company;
import com.jobmanager.job_manager.entity.UserForm;
import com.jobmanager.job_manager.global.exception.errorcodes.OnboardingErrorCode;
import com.jobmanager.job_manager.global.exception.exceptions.OnboardingException;
import com.jobmanager.job_manager.repository.AccountRepository;
import com.jobmanager.job_manager.repository.CompanyRepository;
import com.jobmanager.job_manager.repository.UserFormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class OnboardingService {

    private final AccountRepository accountRepository;
    private final UserFormRepository userFormRepository;
    private final CompanyRepository companyRepository;

    /**
     * USER 온보딩 — 신규 1회만 허용
     */
    public UserOnboardingResponse onboardUser(Long accountId, UserOnboardingRequest req) {

        // 1. 계정 존재 여부
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new OnboardingException(OnboardingErrorCode.ACCOUNT_NOT_FOUND)
                );

        // 2. 계정 타입 검증
        if (account.getAccountType() != Account.AccountType.USER) {
            throw new OnboardingException(
                    OnboardingErrorCode.COMPANY_CANNOT_ONBOARD_USER
            );
        }

        // 3. 중복 온보딩 방지
        if (userFormRepository.existsById(accountId)) {
            throw new OnboardingException(
                    OnboardingErrorCode.USER_ALREADY_ONBOARDED
            );
        }

        // 4. 생년월일 파싱
        LocalDate birth = null;
        if (req.getBirth() != null && !req.getBirth().isBlank()) {
            try {
                birth = LocalDate.parse(req.getBirth());
            } catch (Exception e) {
                throw new OnboardingException(
                        OnboardingErrorCode.INVALID_BIRTH_FORMAT
                );
            }
        }

        // 5. 필수값 검증 (방어적)
        if (req.getRealName() == null || req.getRealName().isBlank()) {
            throw new OnboardingException(
                    OnboardingErrorCode.REQUIRED_FIELD_MISSING
            );
        }

        // 6. 신규 UserForm 생성
        UserForm form = UserForm.builder()
                .accountId(accountId)
                .name(req.getRealName())
                .birth(birth)
                .address(req.getAddress())
                .detailAddress(req.getDetailAddress())
                .zonecode(req.getZonecode())
                .build();

        userFormRepository.saveAndFlush(form);

        return UserOnboardingResponse.from(form);
    }

    /**
     * COMPANY 온보딩 — 신규 1회만 허용
     */
    public CompanyOnboardingResponse onboardCompany(Long accountId, CompanyOnboardingRequest req) {

        // 1. 계정 존재 여부
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new OnboardingException(OnboardingErrorCode.ACCOUNT_NOT_FOUND)
                );

        // 2. 계정 타입 검증
        if (account.getAccountType() != Account.AccountType.COMPANY) {
            throw new OnboardingException(
                    OnboardingErrorCode.USER_CANNOT_ONBOARD_COMPANY
            );
        }

        // 3. 중복 온보딩 방지
        if (companyRepository.existsById(accountId)) {
            throw new OnboardingException(
                    OnboardingErrorCode.COMPANY_ALREADY_ONBOARDED
            );
        }

        // 4. 필수값 검증
        if (req.getCompanyName() == null || req.getCompanyName().isBlank()) {
            throw new OnboardingException(
                    OnboardingErrorCode.REQUIRED_FIELD_MISSING
            );
        }

        // 5. 신규 Company 생성
        Company company = Company.builder()
                .accountId(accountId)
                .companyName(req.getCompanyName())
                .zonecode(req.getZonecode())
                .address(req.getAddress())
                .detailAddress(req.getDetailAddress())
                .businessNumber(req.getBusinessNumber())
                .build();

        companyRepository.saveAndFlush(company);

        return CompanyOnboardingResponse.from(company);
    }
}
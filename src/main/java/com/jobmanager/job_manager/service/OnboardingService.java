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
     * USER 온보딩 — 절대 수정하지 않음
     */
    public UserOnboardingResponse onboardUser(Long accountId, UserOnboardingRequest req) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new OnboardingException(OnboardingErrorCode.ACCOUNT_NOT_FOUND));

        if (account.getAccountType() != Account.AccountType.USER) {
            throw new OnboardingException(OnboardingErrorCode.INVALID_ACCOUNT_TYPE);
        }

        // 생년월일 파싱
        LocalDate birth = null;
        if (req.getBirth() != null && !req.getBirth().isBlank()) {
            try {
                birth = LocalDate.parse(req.getBirth());
            } catch (Exception e) {
                throw new OnboardingException(OnboardingErrorCode.INVALID_BIRTH_FORMAT);
            }
        }

        // 기존 유저폼 조회
        UserForm form = userFormRepository.findById(accountId).orElse(null);

        if (form != null) {
            throw new OnboardingException(OnboardingErrorCode.USER_ALREADY_ONBOARDED);
        }

        // 신규 생성만 허용
        form = UserForm.builder()
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


    public CompanyOnboardingResponse onboardCompany(Long accountId, CompanyOnboardingRequest req) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new OnboardingException(OnboardingErrorCode.ACCOUNT_NOT_FOUND));

        if (account.getAccountType() != Account.AccountType.COMPANY) {
            throw new OnboardingException(OnboardingErrorCode.INVALID_ACCOUNT_TYPE);
        }

        // 기존 온보딩 여부 확인 (1회 제한)
        Company company = companyRepository.findById(accountId).orElse(null);
        if (company != null) {
            throw new OnboardingException(OnboardingErrorCode.COMPANY_ALREADY_ONBOARDED);
        }

        // UserForm과 동일한 구조: 단순 PK 기반 신규 엔티티 생성
        company = Company.builder()
                .accountId(accountId)               // PK
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
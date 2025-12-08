// src/main/java/com/jobmanager/job_manager/service/OnboardingService.java
package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.onboarding.*;
import com.jobmanager.job_manager.entity.Account;
import com.jobmanager.job_manager.entity.Company;
import com.jobmanager.job_manager.entity.UserForm;
import com.jobmanager.job_manager.repository.AccountRepository;
import com.jobmanager.job_manager.repository.CompanyRepository;
import com.jobmanager.job_manager.repository.UserFormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * 온보딩 비즈니스 로직
 * - USER 온보딩: 실명/생년월일/주소 저장
 * - COMPANY 온보딩: 회사 기본 정보 저장
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OnboardingService {

    private final AccountRepository accountRepository;
    private final UserFormRepository userFormRepository;
    private final CompanyRepository companyRepository;

    public UserOnboardingResponse onboardUser(Long accountId, UserOnboardingRequest req) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 accountId"));

        if (account.getAccountType() != Account.AccountType.USER) {
            throw new IllegalArgumentException("USER 계정만 유저 온보딩을 수행할 수 있습니다.");
        }

        LocalDate birth = null;
        if (req.getBirth() != null && !req.getBirth().isBlank()) {
            birth = LocalDate.parse(req.getBirth());
        }

        UserForm form = userFormRepository.findById(accountId)
                .orElse(UserForm.builder()
                        .account(account)
                        .accountId(accountId)
                        .build()
                );

        form.setName(req.getRealName());
        form.setBirth(birth);
        form.setAddress(req.getAddress());
        form.setDetailaddress(req.getDetailAddress());
        form.setZonecode(req.getZonecode());

        userFormRepository.save(form);

        return UserOnboardingResponse.from(form);
    }

    /**
     * COMPANY 온보딩
     * - accountType 이 COMPANY 인 계정만 허용
     * - 이미 companies row 가 있으면 업데이트, 없으면 생성
     */
    public CompanyOnboardingResponse onboardCompany(Long accountId, CompanyOnboardingRequest req) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 accountId"));

        if (account.getAccountType() != Account.AccountType.COMPANY) {
            throw new IllegalArgumentException("COMPANY 계정만 회사 온보딩을 수행할 수 있습니다.");
        }

        Company company = companyRepository.findById(accountId)
                .orElse(Company.builder()
                        .account(account)
                        .accountId(accountId)
                        .build()
                );

        company.setCompanyName(req.getCompanyName());
        company.setDescription(req.getDescription());
        company.setLocation(req.getLocation());
        company.setLogoUrl(req.getLogoUrl());

        companyRepository.save(company);

        return CompanyOnboardingResponse.from(company);
    }
}
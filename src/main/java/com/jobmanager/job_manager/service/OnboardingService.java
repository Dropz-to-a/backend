package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.bankonboarding.BankAccountResponse;
import com.jobmanager.job_manager.dto.onboarding.*;
import com.jobmanager.job_manager.dto.bankonboarding.BankAccountRequest;
import com.jobmanager.job_manager.entity.Account;
import com.jobmanager.job_manager.entity.Company;
import com.jobmanager.job_manager.entity.UserForm;
import com.jobmanager.job_manager.global.exception.errorcodes.BankOnboardingErrorCode;
import com.jobmanager.job_manager.global.exception.errorcodes.OnboardingErrorCode;
import com.jobmanager.job_manager.global.exception.exceptions.BankOnboardingException;
import com.jobmanager.job_manager.global.exception.exceptions.OnboardingException;
import com.jobmanager.job_manager.repository.AccountRepository;
import com.jobmanager.job_manager.repository.CompanyRepository;
import com.jobmanager.job_manager.repository.EmployeeRepository;
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
    private final EmployeeRepository employeeRepository;

    /**
     * USER 온보딩 — 신규 1회만 허용
     */
    public UserOnboardingResponse onboardUser(Long accountId, UserOnboardingRequest req) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new OnboardingException(OnboardingErrorCode.ACCOUNT_NOT_FOUND)
                );

        if (account.getAccountType() != Account.AccountType.USER) {
            throw new OnboardingException(
                    OnboardingErrorCode.COMPANY_CANNOT_ONBOARD_USER
            );
        }

        // 이미 온보딩 완료된 계정 차단 (★)
        if (account.isOnboarded()) {
            throw new OnboardingException(
                    OnboardingErrorCode.USER_ALREADY_ONBOARDED
            );
        }

        if (userFormRepository.existsById(accountId)) {
            throw new OnboardingException(
                    OnboardingErrorCode.USER_ALREADY_ONBOARDED
            );
        }

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

        if (req.getRealName() == null || req.getRealName().isBlank()) {
            throw new OnboardingException(
                    OnboardingErrorCode.REQUIRED_FIELD_MISSING
            );
        }

        UserForm form = UserForm.builder()
                .accountId(accountId)
                .name(req.getRealName())
                .birth(birth)
                .address(req.getAddress())
                .detailAddress(req.getDetailAddress())
                .zonecode(req.getZonecode())
                .build();

        userFormRepository.saveAndFlush(form);

        // 온보딩 완료 처리
        account.markOnboarded();
        accountRepository.save(account);

        return UserOnboardingResponse.from(form);
    }

    /**
     * COMPANY 온보딩 — 신규 1회만 허용
     */
    public CompanyOnboardingResponse onboardCompany(Long accountId, CompanyOnboardingRequest req) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new OnboardingException(OnboardingErrorCode.ACCOUNT_NOT_FOUND)
                );

        if (account.getAccountType() != Account.AccountType.COMPANY) {
            throw new OnboardingException(
                    OnboardingErrorCode.USER_CANNOT_ONBOARD_COMPANY
            );
        }

        // 이미 온보딩 완료된 계정 차단
        if (account.isOnboarded()) {
            throw new OnboardingException(
                    OnboardingErrorCode.COMPANY_ALREADY_ONBOARDED
            );
        }

        if (companyRepository.existsById(accountId)) {
            throw new OnboardingException(
                    OnboardingErrorCode.COMPANY_ALREADY_ONBOARDED
            );
        }

        if (req.getCompanyName() == null || req.getCompanyName().isBlank()) {
            throw new OnboardingException(
                    OnboardingErrorCode.REQUIRED_FIELD_MISSING
            );
        }

        Company company = Company.builder()
                .accountId(accountId)
                .companyName(req.getCompanyName())
                .zonecode(req.getZonecode())
                .address(req.getAddress())
                .detailAddress(req.getDetailAddress())
                .businessNumber(req.getBusinessNumber())
                .build();

        companyRepository.saveAndFlush(company);

        // 온보딩 완료 처리
        account.markOnboarded();
        accountRepository.save(account);

        return CompanyOnboardingResponse.from(company);
    }

    // =========================
    // BANK 계좌 온보딩
    // =========================
    public BankAccountResponse saveBankAccount(
            Long accountId,
            String role,
            BankAccountRequest req
    ) {
        if ("ROLE_USER".equals(role)) {
            if (!employeeRepository.existsByEmployeeId(accountId)) {
                throw new BankOnboardingException(
                        BankOnboardingErrorCode.USER_NOT_EMPLOYED
                );
            }
        }

        if ("ROLE_USER".equals(role)) {
            UserForm form = userFormRepository.findById(accountId)
                    .orElseThrow(() ->
                            new BankOnboardingException(
                                    BankOnboardingErrorCode.USER_NOT_ONBOARDED
                            )
                    );

            form.setBankName(req.getBankName());
            form.setBankAccountNumber(req.getBankAccountNumber());
            userFormRepository.save(form);

            return BankAccountResponse.builder()
                    .accountId(accountId)
                    .role(role)
                    .bankName(form.getBankName())
                    .bankAccountNumber(form.getBankAccountNumber())
                    .build();

        } else if ("ROLE_COMPANY".equals(role)) {
            Company company = companyRepository.findById(accountId)
                    .orElseThrow(() ->
                            new BankOnboardingException(
                                    BankOnboardingErrorCode.COMPANY_NOT_ONBOARDED
                            )
                    );

            company.setBankName(req.getBankName());
            company.setBankAccountNumber(req.getBankAccountNumber());
            companyRepository.save(company);

            return BankAccountResponse.builder()
                    .accountId(accountId)
                    .role(role)
                    .bankName(company.getBankName())
                    .bankAccountNumber(company.getBankAccountNumber())
                    .build();
        }

        throw new BankOnboardingException(
                BankOnboardingErrorCode.INVALID_ACCOUNT_TYPE
        );
    }
}

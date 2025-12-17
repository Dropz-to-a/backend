package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.auth.RegisterRequest;
import com.jobmanager.job_manager.entity.*;
import com.jobmanager.job_manager.global.exception.exceptions.BusinessException;
import com.jobmanager.job_manager.global.exception.errorcodes.AuthErrorCode;
import com.jobmanager.job_manager.global.jwt.JwtTokenProvider;
import com.jobmanager.job_manager.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepo;
    private final CredentialRepository credRepo;
    private final RoleRepository roleRepo;
    private final AccountRoleRepository arRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwt;
    private final UserFormRepository userFormRepository;
    private final CompanyRepository companyRepository;

    @Transactional
    public void register(RegisterRequest req) {

        // username 중복 체크
        accountRepo.findByUsername(req.getUsername())
                .ifPresent(a -> {
                    throw new BusinessException(AuthErrorCode.USERNAME_ALREADY_EXISTS);
                });

        // email 중복 체크
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            accountRepo.findByEmail(req.getEmail())
                    .ifPresent(a -> {
                        throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
                    });
        }

        // 역할 코드 매핑
        String roleCode = defaultRoleIfBlank(req.getRoleCode());
        Role role = resolveRole(roleCode);

        Account.AccountType mappedType = mapRoleToAccountType(role.getCode());

        // Account 저장
        Account acc = Account.builder()
                .accountType(mappedType)
                .username(req.getUsername())
                .email(req.getEmail())
                .phone(req.getPhone())
                .status(Account.Status.ACTIVE)
                .build();

        acc = accountRepo.save(acc);

        // Credential 저장
        Credential cred = Credential.builder()
                .account(acc)
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .passwordUpdatedAt(LocalDateTime.now())
                .mfaEnabled(false)
                .build();

        credRepo.save(cred);

        // AccountRole 저장
        arRepo.save(AccountRole.builder()
                .accountId(acc.getId())
                .roleId(role.getId())
                .grantedAt(LocalDateTime.now())
                .build());
    }

    public String login(String id, String rawPassword) {

        // username 또는 email 로 로그인
        Account acc = accountRepo.findByUsername(id)
                .or(() -> accountRepo.findByEmail(id))
                .orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND));

        // 비밀번호 정보 체크
        Credential cred = credRepo.findByAccountId(acc.getId())
                .orElseThrow(() -> new BusinessException(AuthErrorCode.INTERNAL_ERROR));

        // 비밀번호 비교
        if (!passwordEncoder.matches(rawPassword, cred.getPasswordHash())) {
            throw new BusinessException(AuthErrorCode.PASSWORD_MISMATCH);
        }

        // 역할 조회
        String roleCode = arRepo.findTopRoleCodeByAccountId(acc.getId())
                .orElse("ROLE_USER");

        roleCode = defaultRoleIfBlank(roleCode);
        Account.AccountType type = mapRoleToAccountType(roleCode);
        boolean onboarded = isOnboarded(acc);

        // JWT 토큰 발급
        return jwt.generate(
                acc.getId(),
                acc.getUsername(),
                type.name(),
                roleCode,
                onboarded
        );
    }

    private String defaultRoleIfBlank(String roleCode) {
        return (roleCode == null || roleCode.isBlank()) ? "ROLE_USER" : roleCode;
    }

    private Role resolveRole(String roleOrId) {
        try {
            long id = Long.parseLong(roleOrId);
            return roleRepo.findById(id)
                    .orElseThrow(() -> new BusinessException(AuthErrorCode.ROLE_NOT_FOUND));
        } catch (NumberFormatException ignore) {
            return roleRepo.findByCode(roleOrId)
                    .orElseThrow(() -> new BusinessException(AuthErrorCode.ROLE_NOT_FOUND));
        }
    }

    private Account.AccountType mapRoleToAccountType(String roleCode) {

        if (roleCode == null) return Account.AccountType.USER;

        return switch (roleCode) {
            case "ROLE_COMPANY", "2" -> Account.AccountType.COMPANY;
            case "ROLE_ADMIN", "3" -> Account.AccountType.ADMIN;
            default -> Account.AccountType.USER;
        };
    }

    private boolean isOnboarded(Account acc) {

        return switch (acc.getAccountType()) {
            case USER ->
                    userFormRepository.existsById(acc.getId());

            case COMPANY ->
                    companyRepository.existsById(acc.getId());

            case ADMIN ->
                    true;
        };
    }
}
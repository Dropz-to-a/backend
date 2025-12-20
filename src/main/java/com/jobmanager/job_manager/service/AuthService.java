package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.auth.RegisterRequest;
import com.jobmanager.job_manager.entity.*;
import com.jobmanager.job_manager.global.exception.exceptions.BusinessException;
import com.jobmanager.job_manager.global.exception.errorcodes.AuthErrorCode;
import com.jobmanager.job_manager.global.jwt.JwtTokenProvider;
import com.jobmanager.job_manager.global.jwt.TokenHashUtils;
import com.jobmanager.job_manager.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

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
    private final EmployeeRepository employeeRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    // 회원가입
    @Transactional
    public void register(RegisterRequest req) {

        accountRepo.findByUsername(req.getUsername())
                .ifPresent(a -> { throw new BusinessException(AuthErrorCode.USERNAME_ALREADY_EXISTS); });

        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            accountRepo.findByEmail(req.getEmail())
                    .ifPresent(a -> { throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_EXISTS); });
        }

        String roleCode = defaultRoleIfBlank(req.getRoleCode());
        Role role = resolveRole(roleCode);
        Account.AccountType mappedType = mapRoleToAccountType(role.getCode());

        Account acc = Account.builder()
                .accountType(mappedType)
                .username(req.getUsername())
                .email(req.getEmail())
                .phone(req.getPhone())
                .status(Account.Status.ACTIVE)
                .build();

        acc = accountRepo.save(acc);

        Credential cred = Credential.builder()
                .account(acc)
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .passwordUpdatedAt(LocalDateTime.now())
                .mfaEnabled(false)
                .build();

        credRepo.save(cred);

        arRepo.save(AccountRole.builder()
                .accountId(acc.getId())
                .roleId(role.getId())
                .grantedAt(LocalDateTime.now())
                .build());
    }

    // 로그인
    @Transactional
    public String login(String id, String rawPassword) {

        Account acc = accountRepo.findByUsername(id)
                .or(() -> accountRepo.findByEmail(id))
                .orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND));

        Credential cred = credRepo.findByAccountId(acc.getId())
                .orElseThrow(() -> new BusinessException(AuthErrorCode.INTERNAL_ERROR));

        if (!passwordEncoder.matches(rawPassword, cred.getPasswordHash())) {
            throw new BusinessException(AuthErrorCode.PASSWORD_MISMATCH);
        }

        String roleCode = arRepo.findTopRoleCodeByAccountId(acc.getId())
                .orElse("ROLE_USER");

        roleCode = defaultRoleIfBlank(roleCode);
        Account.AccountType type = mapRoleToAccountType(roleCode);
        boolean onboarded = isOnboarded(acc);

        String companyName = null;
        String businessNumber = null;

        if (type == Account.AccountType.USER) {
            companyName = employeeRepository.findByEmployeeId(acc.getId())
                    .flatMap(emp -> companyRepository.findById(emp.getCompanyId()))
                    .map(Company::getCompanyName)
                    .orElse(null);
        }

        if (type == Account.AccountType.COMPANY) {
            businessNumber = companyRepository.findById(acc.getId())
                    .map(Company::getBusinessNumber)
                    .orElse(null);
        }

        // AccessToken (30분)
        String accessToken = jwt.generateLoginToken(
                acc.getId(),
                acc.getUsername(),
                type.name(),
                roleCode,
                onboarded,
                companyName,
                businessNumber
        );

        // RefreshToken 생성 + DB 저장
        String refreshRaw = UUID.randomUUID().toString().replace("-", "");
        String refreshHash = TokenHashUtils.sha256(refreshRaw);

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .accountId(acc.getId())
                        .tokenHash(refreshHash)
                        .expiresAt(LocalDateTime.now().plusDays(1))
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        // accessToken만 반환
        return accessToken;
    }

    // refresh
    @Transactional(readOnly = true)
    public String refresh(String refreshTokenRaw) {

        String hash = TokenHashUtils.sha256(refreshTokenRaw);

        RefreshToken rt = refreshTokenRepository
                .findByTokenHashAndRevokedAtIsNull(hash)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.UNAUTHORIZED));

        if (rt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(AuthErrorCode.UNAUTHORIZED);
        }

        Account acc = accountRepo.findById(rt.getAccountId())
                .orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND));

        String roleCode = arRepo.findTopRoleCodeByAccountId(acc.getId())
                .orElse("ROLE_USER");

        // AccessToken (1일)
        return jwt.generateRefreshToken(
                acc.getId(),
                acc.getUsername(),
                acc.getAccountType().name(),
                roleCode,
                true,
                null,
                null
        );
    }

    // 내부 메서드
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
            case USER -> userFormRepository.existsById(acc.getId());
            case COMPANY -> companyRepository.existsById(acc.getId());
            case ADMIN -> true;
        };
    }
}

package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.auth.RegisterRequest;
import com.jobmanager.job_manager.entity.*;
import com.jobmanager.job_manager.global.exception.errorcodes.AuthErrorCode;
import com.jobmanager.job_manager.global.exception.exceptions.BusinessException;
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

    private final AccountRepository accountRepository;
    private final CredentialRepository credentialRepository;
    private final RoleRepository roleRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // ============================================================
    // 회원가입
    // ============================================================
    @Transactional
    public void register(RegisterRequest req) {

        accountRepository.findByUsername(req.getUsername())
                .ifPresent(a -> {
                    throw new BusinessException(AuthErrorCode.USERNAME_ALREADY_EXISTS);
                });

        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            accountRepository.findByEmail(req.getEmail())
                    .ifPresent(a -> {
                        throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
                    });
        }

        String roleCode = defaultRole(req.getRoleCode());
        Role role = resolveRole(roleCode);

        Account account = accountRepository.save(
                Account.builder()
                        .username(req.getUsername())
                        .email(req.getEmail())
                        .phone(req.getPhone())
                        .accountType(mapRoleToAccountType(role.getCode()))
                        .status(Account.Status.ACTIVE)
                        .build()
        );

        credentialRepository.save(
                Credential.builder()
                        .account(account)
                        .passwordHash(passwordEncoder.encode(req.getPassword()))
                        .passwordUpdatedAt(LocalDateTime.now())
                        .mfaEnabled(false)
                        .build()
        );

        accountRoleRepository.save(
                AccountRole.builder()
                        .accountId(account.getId())
                        .roleId(role.getId())
                        .grantedAt(LocalDateTime.now())
                        .build()
        );
    }

    // ============================================================
    // 로그인 (Access + Refresh 발급)
    // ============================================================
    @Transactional
    public LoginResult login(String id, String rawPassword) {

        Account account = accountRepository.findByUsername(id)
                .or(() -> accountRepository.findByEmail(id))
                .orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND));

        Credential credential = credentialRepository.findByAccountId(account.getId())
                .orElseThrow(() -> new BusinessException(AuthErrorCode.INTERNAL_ERROR));

        if (!passwordEncoder.matches(rawPassword, credential.getPasswordHash())) {
            throw new BusinessException(AuthErrorCode.PASSWORD_MISMATCH);
        }

        String roleCode = accountRoleRepository
                .findTopRoleCodeByAccountId(account.getId())
                .orElse("ROLE_USER");

        // AccessToken (30분)
        String accessToken = jwtTokenProvider.generateAccessToken(
                account.getId(),
                account.getUsername(),
                account.getAccountType().name(),
                roleCode,
                true,
                null,
                null
        );

        // RefreshToken (UUID + DB, 1일)
        String refreshRaw = UUID.randomUUID().toString().replace("-", "");
        String refreshHash = TokenHashUtils.sha256(refreshRaw);

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .accountId(account.getId())
                        .tokenHash(refreshHash)
                        .expiresAt(LocalDateTime.now().plusDays(1))
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        return new LoginResult(accessToken, refreshRaw);
    }

    // ============================================================
    // Access Token 재발급
    // ============================================================
    @Transactional(readOnly = true)
    public String refresh(String refreshTokenRaw) {

        String hash = TokenHashUtils.sha256(refreshTokenRaw);

        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenHashAndRevokedAtIsNull(hash)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.UNAUTHORIZED));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(AuthErrorCode.TOKEN_EXPIRED);
        }

        Account account = accountRepository.findById(refreshToken.getAccountId())
                .orElseThrow(() -> new BusinessException(AuthErrorCode.ACCOUNT_NOT_FOUND));

        String roleCode = accountRoleRepository
                .findTopRoleCodeByAccountId(account.getId())
                .orElse("ROLE_USER");

        return jwtTokenProvider.generateAccessToken(
                account.getId(),
                account.getUsername(),
                account.getAccountType().name(),
                roleCode,
                true,
                null,
                null
        );
    }

    // ============================================================
    // 내부 헬퍼
    // ============================================================
    private String defaultRole(String roleCode) {
        return (roleCode == null || roleCode.isBlank())
                ? "ROLE_USER"
                : roleCode;
    }

    private Role resolveRole(String roleCode) {
        try {
            return roleRepository.findById(Long.parseLong(roleCode))
                    .orElseThrow(() -> new BusinessException(AuthErrorCode.ROLE_NOT_FOUND));
        } catch (NumberFormatException e) {
            return roleRepository.findByCode(roleCode)
                    .orElseThrow(() -> new BusinessException(AuthErrorCode.ROLE_NOT_FOUND));
        }
    }

    private Account.AccountType mapRoleToAccountType(String roleCode) {
        return switch (roleCode) {
            case "ROLE_COMPANY", "2" -> Account.AccountType.COMPANY;
            case "ROLE_ADMIN", "3" -> Account.AccountType.ADMIN;
            default -> Account.AccountType.USER;
        };
    }

    // ============================================================
    // 컨트롤러 반환용
    // ============================================================
    public record LoginResult(String accessToken, String refreshToken) {}
}

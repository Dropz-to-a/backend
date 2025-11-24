package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.auth.RegisterRequest;
import com.jobmanager.job_manager.entity.*;
import com.jobmanager.job_manager.repository.*;
import com.jobmanager.job_manager.global.jwt.JwtTokenProvider;
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

    @Transactional
    public void register(RegisterRequest req) {

        accountRepo.findByUsername(req.getUsername()).ifPresent(a -> {
            throw new IllegalArgumentException("이미 존재하는 username");
        });

        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            accountRepo.findByEmail(req.getEmail()).ifPresent(a -> {
                throw new IllegalArgumentException("이미 존재하는 email");
            });
        }

        Role role = resolveRole(defaultRoleIfBlank(req.getRoleCode()));
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

    public String login(String id, String rawPassword) {

        Account acc = accountRepo.findByUsername(id)
                .or(() -> accountRepo.findByEmail(id))
                .orElseThrow(() -> new IllegalArgumentException("계정이 존재하지 않음"));

        Credential cred = credRepo.findByAccountId(acc.getId())
                .orElseThrow(() -> new IllegalStateException("비밀번호 정보가 없음"));

        if (!passwordEncoder.matches(rawPassword, cred.getPasswordHash())) {
            throw new IllegalArgumentException("비밀번호 불일치");
        }

        String roleCode = arRepo.findTopRoleCodeByAccountId(acc.getId())
                .orElse("ROLE_USER");

        roleCode = defaultRoleIfBlank(roleCode);

        Account.AccountType accountType = mapRoleToAccountType(roleCode);

        return jwt.generate(
                acc.getId(),
                acc.getUsername(),
                accountType.name(),
                roleCode
        );
    }

    private String defaultRoleIfBlank(String roleCode) {
        return (roleCode == null || roleCode.isBlank()) ? "ROLE_USER" : roleCode;
    }

    private Role resolveRole(String roleOrId) {
        try {
            long id = Long.parseLong(roleOrId);
            return roleRepo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 roleId: " + id));
        } catch (NumberFormatException ignore) {
            return roleRepo.findByCode(roleOrId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 roleCode: " + roleOrId));
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
}

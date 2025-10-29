package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.auth.RegisterRequest;
import com.jobmanager.job_manager.entity.*;
import com.jobmanager.job_manager.repository.*;
import com.jobmanager.job_manager.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ★ 추가
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

    @Transactional // ★ 한 트랜잭션으로 처리
    public void register(RegisterRequest req) {
        // username 중복 방지
        accountRepo.findByUsername(req.getUsername()).ifPresent(a -> {
            throw new IllegalArgumentException("이미 존재하는 username");
        });
        // (선택) email 중복 방지
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            accountRepo.findByEmail(req.getEmail()).ifPresent(a -> {
                throw new IllegalArgumentException("이미 존재하는 email");
            });
        }

        // 1) accounts insert
        Account acc = Account.builder()
                .accountType(Account.AccountType.USER)
                .username(req.getUsername())
                .email(req.getEmail())
                .phone(req.getPhone())
                .status(Account.Status.ACTIVE)
                .build();
        acc = accountRepo.save(acc); // managed

        // 2) credentials insert (암호화 저장)
        Credential cred = Credential.builder()
                .account(acc) // ★ @MapsId가 account.id를 복사 -> accountId 수동세팅 X
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .passwordUpdatedAt(LocalDateTime.now())
                .mfaEnabled(false)
                .build();
        credRepo.save(cred);

        // (선택) 기본 권한 부여 — roles 테이블에 ROLE_USER가 있다고 가정
        Account finalAcc = acc;
        roleRepo.findByCode("ROLE_USER").ifPresent(role ->
                arRepo.save(AccountRole.builder()
                        .accountId(finalAcc.getId())
                        .roleId(role.getId())
                        .grantedAt(LocalDateTime.now())
                        .build())
        );
    }

    public String login(String id, String rawPassword) {
        // id는 username 우선, 없으면 email
        Account acc = accountRepo.findByUsername(id)
                .or(() -> accountRepo.findByEmail(id))
                .orElseThrow(() -> new IllegalArgumentException("계정이 존재하지 않음"));

        Credential cred = credRepo.findByAccountId(acc.getId())
                .orElseThrow(() -> new IllegalStateException("비밀번호 정보가 없음"));

        if (!passwordEncoder.matches(rawPassword, cred.getPasswordHash())) {
            throw new IllegalArgumentException("비밀번호 불일치");
        }

        // 가장 기본 역할만 토큰에 포함 (필요 시 확장)
        String role = "ROLE_USER";
        return jwt.generate(acc.getUsername(), role);
    }
}

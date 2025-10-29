package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.auth.RegisterRequest;
import com.jobmanager.job_manager.entity.*;
import com.jobmanager.job_manager.repository.*;
import com.jobmanager.job_manager.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepo;
    private final CredentialRepository credRepo;
    private final RoleRepository roleRepo;
    private final AccountRoleRepository arRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwt;

    public void register(RegisterRequest req) {
        accountRepo.findByUsername(req.getUsername()).ifPresent(a -> {
            throw new IllegalArgumentException("이미 존재하는 username");
        });

        Account acc = Account.builder()
                .accountType(Account.AccountType.USER)
                .username(req.getUsername())
                .email(req.getEmail())
                .phone(req.getPhone())
                .status(Account.Status.ACTIVE)
                .build();

        // 1 계정 먼저 저장 (managed 상태로 만듦)
        acc = accountRepo.save(acc);

        // 2️ Credential 생성 시, 저장된 account 엔티티를 그대로 연결
        Credential cred = Credential.builder()
                .account(acc)                  // managed 상태의 Account
                .accountId(acc.getId())        // ★ 명시적으로 ID 지정
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .mfaEnabled(false)
                .build();

        // 3️ Credential 저장
        credRepo.save(cred);
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

        // 가장 기본 역할 하나만 JWT에 담아 발급 (필요 시 조회 확장)
        String role = "ROLE_USER";
        // 관리자/회사 권한 부여는 account_roles 조합으로 확장 가능

        return jwt.generate(acc.getUsername(), role);
    }
}

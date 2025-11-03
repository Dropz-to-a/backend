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

    /**
     * 회원가입 단계(스웨거 설명용 번호 주석)
     * 0) roleCode 해석 (문자/숫자 모두 허용, 기본 ROLE_USER)
     * 1) accounts insert (roleCode -> account_type 매핑 반영)
     * 2) credentials insert (비밀번호 BCrypt 해시 저장)
     * 3) account_roles insert (요청된 역할 매핑)
     */
    @Transactional
    public void register(RegisterRequest req) {
        // username 중복
        accountRepo.findByUsername(req.getUsername()).ifPresent(a -> {
            throw new IllegalArgumentException("이미 존재하는 username");
        });
        // email 중복(선택)
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            accountRepo.findByEmail(req.getEmail()).ifPresent(a -> {
                throw new IllegalArgumentException("이미 존재하는 email");
            });
        }

        // 0) roleCode 결정
        Role role = resolveRole(defaultRoleIfBlank(req.getRoleCode()));

        // 0-1) roleCode -> account_type 매핑
        Account.AccountType mappedType = mapRoleToAccountType(role.getCode());

        // 1) accounts insert (account_type을 매핑값으로 저장)
        Account acc = Account.builder()
                .accountType(mappedType)
                .username(req.getUsername())
                .email(req.getEmail())
                .phone(req.getPhone())
                .status(Account.Status.ACTIVE)
                .build();
        acc = accountRepo.save(acc); // managed

        // 2) credentials insert (암호화 저장)
        Credential cred = Credential.builder()
                .account(acc) // @MapsId 사용
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .passwordUpdatedAt(LocalDateTime.now())
                .mfaEnabled(false)
                .build();
        credRepo.save(cred);

        // 3) account_roles 매핑 (요청 role 반영)
        arRepo.save(AccountRole.builder()
                .accountId(acc.getId())
                .roleId(role.getId())
                .grantedAt(LocalDateTime.now())
                .build());
    }

    /**
     * 로그인 단계(스웨거 설명용 번호 주석)
     * 1) id(username/email)로 Account 조회
     * 2) Credential 조회 및 비밀번호 검증
     * 3) 가장 먼저 부여된 역할코드 조회(없으면 ROLE_USER)
     * 4) JWT 발급(서브젝트 = username, 권한 = roleCode)
     */
    public String login(String id, String rawPassword) {
        // 1) 계정 조회
        Account acc = accountRepo.findByUsername(id)
                .or(() -> accountRepo.findByEmail(id))
                .orElseThrow(() -> new IllegalArgumentException("계정이 존재하지 않음"));

        // 2) 비밀번호 검증
        Credential cred = credRepo.findByAccountId(acc.getId())
                .orElseThrow(() -> new IllegalStateException("비밀번호 정보가 없음"));

        if (!passwordEncoder.matches(rawPassword, cred.getPasswordHash())) {
            throw new IllegalArgumentException("비밀번호 불일치");
        }

        // 3) 조인 쿼리로 roleCode 바로 조회 (없으면 ROLE_USER)
        String roleCode = arRepo.findTopRoleCodeByAccountId(acc.getId())
                .orElse("ROLE_USER");

        // 4) JWT 발급
        return jwt.generate(acc.getUsername(), roleCode);
    }

    // ===== Helpers =====

    private String defaultRoleIfBlank(String roleCode) {
        return (roleCode == null || roleCode.isBlank()) ? "ROLE_USER" : roleCode;
    }

    /** 숫자("1","2","3") 또는 문자열("ROLE_USER") 모두 허용해서 Role 엔티티 조회 */
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

    /** roleCode → accounts.account_type 변환 */
    private Account.AccountType mapRoleToAccountType(String roleCode) {
        if (roleCode == null) return Account.AccountType.USER;
        return switch (roleCode) {
            case "ROLE_COMPANY", "2" -> Account.AccountType.COMPANY;
            case "ROLE_ADMIN",   "3" -> Account.AccountType.ADMIN;
            default -> Account.AccountType.USER;
        };
    }
}

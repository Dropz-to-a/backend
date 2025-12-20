package com.jobmanager.job_manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 계정 ID (accounts.id) */
    @Column(name = "account_id", nullable = false)
    private Long accountId;

    /** refresh token SHA-256 hash */
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    /** 요청 IP */
    @Column(name = "ip", length = 45)
    private String ip;

    /** User-Agent */
    @Column(name = "user_agent", length = 255)
    private String userAgent;

    /** 만료 시각 */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /** 폐기 시각 (로그아웃, 강제 만료 등) */
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    /** 생성 시각 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

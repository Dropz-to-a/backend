package com.jobmanager.job_manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "credentials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Credential {

    @Id
    @Column(name = "account_id")
    private Long accountId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;        // ← 해시 비밀번호 저장 컬럼

    @Column(name = "password_updated_at")
    private LocalDateTime passwordUpdatedAt;

    @Column(name = "mfa_enabled", nullable = false)
    private boolean mfaEnabled;

    @Lob
    @Column(name = "mfa_secret")
    private byte[] mfaSecret;
}

package com.jobmanager.job_manager.entity;

import jakarta.persistence.*;
import lombok.*;

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
    private Long accountId; // accounts.id

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "password_updated_at")
    private java.time.LocalDateTime passwordUpdatedAt;

    @Column(name = "mfa_enabled", nullable = false)
    private boolean mfaEnabled;

    @Lob
    @Column(name = "mfa_secret")
    private byte[] mfaSecret;
}

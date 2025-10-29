package com.jobmanager.job_manager.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity @Table(name = "account_roles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@IdClass(AccountRole.PK.class)
public class AccountRole {

    @Id @Column(name = "account_id")
    private Long accountId;

    @Id @Column(name = "role_id")
    private Long roleId;

    @Column(name = "granted_at", nullable = false)
    private LocalDateTime grantedAt;

    @Data
    public static class PK implements Serializable {
        private Long accountId;
        private Long roleId;
    }
}

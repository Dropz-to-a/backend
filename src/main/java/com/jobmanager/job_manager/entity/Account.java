package com.jobmanager.job_manager.entity;

import com.jobmanager.job_manager.entity.enums.EmploymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private Boolean onboarded;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status", nullable = false)
    private EmploymentStatus employmentStatus;

    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum AccountType {
        USER, COMPANY, ADMIN
    }

    public enum Status {
        ACTIVE, SUSPENDED, DELETED
    }

    public void markOnboarded() {
        this.onboarded = Boolean.TRUE;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.onboarded == null) {
            this.onboarded = Boolean.FALSE;
        }

        if (this.employmentStatus == null) {
            this.employmentStatus = EmploymentStatus.UNEMPLOYED;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

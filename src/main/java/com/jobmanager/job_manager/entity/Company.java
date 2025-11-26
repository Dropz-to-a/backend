// src/main/java/com/jobmanager/job_manager/entity/Company.java
package com.jobmanager.job_manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * companies 테이블 매핑
 * - PK = account_id (accounts.id와 1:1)
 */
@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @Column(name = "account_id")
    private Long accountId;   // companies.account_id (PK & FK)

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "company_name", nullable = false)
    private String companyName;   // 회사 이름

    @Column(name = "description")
    private String description;   // 회사 설명

    @Column(name = "location")
    private String location;      // 회사 위치

    @Column(name = "logo_url")
    private String logoUrl;       // 로고 URL

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
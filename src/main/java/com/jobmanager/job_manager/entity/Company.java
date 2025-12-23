package com.jobmanager.job_manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    private Long accountId;

    /* ===== 필수 기본 정보 ===== */

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "business_number", nullable = false, length = 20)
    private String businessNumber;

    @Column(name = "zonecode", nullable = false, length = 10)
    private String zonecode;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "detail_address", nullable = false)
    private String detailAddress;

    /* ===== 선택 정보 ===== */

    @Column(name = "founded_year")
    private Integer foundedYear;

    @Column(name = "employee_count")
    private Integer employeeCount;

    @Column(name = "industry", length = 100)
    private String industry;

    @Column(name = "website")
    private String website;

    @Lob
    @Column(name = "description")
    private String description;

    @Lob
    @Column(name = "company_values")
    private String companyValues;

    @Lob
    @Column(name = "mission")
    private String mission;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    /* ===== 타임스탬프 ===== */

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
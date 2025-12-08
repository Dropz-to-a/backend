// src/main/java/com/jobmanager/job_manager/entity/UserForm.java
package com.jobmanager.job_manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * user_form 테이블 매핑
 * - PK = account_id (accounts.id와 1:1)
 * - 온보딩에서는 name/birth/address만 채운다.
 *   나머지 컬럼은 나중에 프로필/추가 정보로 확장 가능.
 */
@Entity
@Table(name = "user_form")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserForm {

    @Id
    @Column(name = "account_id")
    private Long accountId;

    // accounts.id 와 1:1 매핑
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    // 온보딩에서 받는 값들
    @Column(name = "name", nullable = false)
    private String name;          // 실명

    @Column(name = "birth")
    private LocalDate birth;      // 생년월일

    @Column(name = "zonecode")
    private String zonecode;      //우편번호

    @Column(name = "address")
    private String address;       // 거주지 주소

    @Column(name = "detail_address")
    private String detailaddress; //상세 주소

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "height")
    private String height;

    @Column(name = "weight")
    private String weight;

    @Column(name = "blood")
    private String blood;

    @Column(name = "education")
    private String education;

    @Column(name = "military")
    private String military;

    @Column(name = "license", columnDefinition = "TEXT")
    private String license;

    @Column(name = "foreign_lang", columnDefinition = "TEXT")
    private String foreignLang;

    @Column(name = "activity", columnDefinition = "TEXT")
    private String activity;

    @Column(name = "family", columnDefinition = "TEXT")
    private String family;

    @Column(name = "hobby", columnDefinition = "TEXT")
    private String hobby;

    @Column(name = "motivation", columnDefinition = "TEXT")
    private String motivation;

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
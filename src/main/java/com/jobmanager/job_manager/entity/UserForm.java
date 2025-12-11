// src/main/java/com/jobmanager/job_manager/entity/UserForm.java
package com.jobmanager.job_manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    // @MapsId 및 연관관계 제거 (필수)
    // Hibernate merge 충돌을 유발하므로 삭제

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "birth")
    private LocalDate birth;

    @Column(name = "zonecode", nullable = false)
    private String zonecode;

    @Column(name = "address")
    private String address;

    @Column(name = "detail_address", nullable = false)
    private String detailAddress;

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

    @Column(name = "hobby")
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
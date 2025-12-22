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

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "birth")
    private LocalDate birth;

    @Column(name = "zonecode", nullable = false)
    private String zonecode;

    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills;

    @Column(name = "address")
    private String address;

    @Column(name = "detail_address", nullable = false)
    private String detailAddress;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    /** 자격증 목록 (배열, JSON 문자열 등으로 저장) */
    @Column(name = "licenses", columnDefinition = "TEXT")
    private String licenses;

    /** 외국어 목록 (배열, JSON 문자열 등으로 저장) */
    @Column(name = "foreign_langs", columnDefinition = "TEXT")
    private String foreignLangs;

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
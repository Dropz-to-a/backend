package com.jobmanager.job_manager.entity.application;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "posting_id", nullable = false)
    private Long postingId;

    @Column(name = "writer_id", nullable = false)
    private Long writerId;

    // 기본 정보
    private String name;
    private LocalDate birth;
    private String email;
    private String phone;
    private String address;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    // 학력
    @Column(name = "education_school")
    private String educationSchool;

    @Column(name = "education_major")
    private String educationMajor;

    @Column(name = "education_degree")
    private String educationDegree;

    @Column(name = "education_start_date")
    private LocalDate educationStartDate;

    @Column(name = "education_end_date")
    private LocalDate educationEndDate;

    @Column(name = "education_graduated")
    private boolean educationGraduated;

    @Column(name = "education_gpa")
    private String educationGpa;

    // 대외활동
    @Lob
    private String activities;

    // 자기소개
    @Lob private String introduction;
    @Lob private String motivation;
    @Lob private String personality;
    @Lob private String futureGoal;

    // 기타
    private String portfolioUrl;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
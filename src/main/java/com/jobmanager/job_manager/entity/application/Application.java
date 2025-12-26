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

    // 대외활동
    @Lob
    private String activities;

    // 자기소개
    @Lob private String introduction;
    @Lob private String motivation;
    @Lob private String personality;
    @Lob private String futureGoal;

    // ===== 신체 =====
    @Column(name = "height")
    private String height;

    @Column(name = "weight")
    private String weight;

    @Column(name = "blood")
    private String blood;

    // ===== 병역 =====
    @Column(name = "military_status")
    private String militaryStatus;

    @Column(name = "military_branch")
    private String militaryBranch;

    @Column(name = "military_type")
    private String militaryType;

    @Column(name = "military_rank")
    private String militaryRank;

    @Column(name = "military_period")
    private String militaryPeriod;

    @Column(name = "military_exempt_reason")
    private String militaryExemptReason;

    // ===== 수상 내역 (최대 3개) =====
    @Column(name = "award_name_1")
    private String awardName1;

    @Column(name = "award_date_1")
    private LocalDate awardDate1;

    @Column(name = "award_issuer_1")
    private String awardIssuer1;

    @Column(name = "award_name_2")
    private String awardName2;

    @Column(name = "award_date_2")
    private LocalDate awardDate2;

    @Column(name = "award_issuer_2")
    private String awardIssuer2;

    @Column(name = "award_name_3")
    private String awardName3;

    @Column(name = "award_date_3")
    private LocalDate awardDate3;

    @Column(name = "award_issuer_3")
    private String awardIssuer3;

    // ===== 외국어 활용 능력 (최대 2개) =====
    @Column(name = "foreign_lang_ability_1")
    private String foreignLangAbility1;

    @Column(name = "foreign_lang_test_1")
    private String foreignLangTest1;

    @Column(name = "foreign_lang_score_1")
    private String foreignLangScore1;

    @Column(name = "foreign_lang_ability_2")
    private String foreignLangAbility2;

    @Column(name = "foreign_lang_test_2")
    private String foreignLangTest2;

    @Column(name = "foreign_lang_score_2")
    private String foreignLangScore2;

    // ===== 가족 1 =====
    @Column(name = "family_relation_1")
    private String familyRelation1;

    @Column(name = "family_name_1")
    private String familyName1;

    @Column(name = "family_age_1")
    private String familyAge1;

    @Column(name = "family_job_1")
    private String familyJob1;

    // ===== 가족 2 =====
    @Column(name = "family_relation_2")
    private String familyRelation2;

    @Column(name = "family_name_2")
    private String familyName2;

    @Column(name = "family_age_2")
    private String familyAge2;

    @Column(name = "family_job_2")
    private String familyJob2;

    // ===== 가족 3 =====
    @Column(name = "family_relation_3")
    private String familyRelation3;

    @Column(name = "family_name_3")
    private String familyName3;

    @Column(name = "family_age_3")
    private String familyAge3;

    @Column(name = "family_job_3")
    private String familyJob3;

    // ===== 가족 4 =====
    @Column(name = "family_relation_4")
    private String familyRelation4;

    @Column(name = "family_name_4")
    private String familyName4;

    @Column(name = "family_age_4")
    private String familyAge4;

    @Column(name = "family_job_4")
    private String familyJob4;

    // ===== 취미 / 특기 =====
    @Column(name = "hobby")
    private String hobby;

    @Column(name = "specialty")
    private String specialty;

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
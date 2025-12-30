package com.jobmanager.job_manager.dto.application;

import com.jobmanager.job_manager.entity.application.Application;
import com.jobmanager.job_manager.entity.application.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ApplicationDetailResponse {

    private Long applicationId;
    private Long postingId;
    private Long writerId;

    // =====================
    // 기본 정보
    // =====================
    private String name;
    private String email;
    private String phone;
    private String address;

    // =====================
    // 대외활동
    // =====================
    private String activities;

    // =====================
    // 자기소개
    // =====================
    private String introduction;
    private String motivation;
    private String personality;
    private String futureGoal;

    // =====================
    // 신체
    // =====================
    private Integer height;
    private Integer weight;
    private String blood;

    // =====================
    // 병역
    // =====================
    private String militaryStatus;
    private String militaryBranch;
    private String militaryType;
    private String militaryRank;
    private LocalDate militaryStartDate;
    private LocalDate militaryEndDate;
    private String militaryExemptReason;

    // =====================
    // 수상 내역
    // =====================
    private String awardName1;
    private LocalDate awardDate1;
    private String awardIssuer1;

    private String awardName2;
    private LocalDate awardDate2;
    private String awardIssuer2;

    private String awardName3;
    private LocalDate awardDate3;
    private String awardIssuer3;

    // =====================
    // 외국어
    // =====================
    private String foreignLangAbility1;
    private String foreignLangTest1;
    private String foreignLangScore1;

    private String foreignLangAbility2;
    private String foreignLangTest2;
    private String foreignLangScore2;

    // =====================
    // 가족
    // =====================
    private String familyRelation1;
    private String familyName1;
    private String familyAge1;
    private String familyJob1;

    private String familyRelation2;
    private String familyName2;
    private String familyAge2;
    private String familyJob2;

    private String familyRelation3;
    private String familyName3;
    private String familyAge3;
    private String familyJob3;

    private String familyRelation4;
    private String familyName4;
    private String familyAge4;
    private String familyJob4;

    // =====================
    // 자격증
    // =====================
    private String licenseType1;
    private String licenseLevel1;
    private LocalDate licenseDate1;
    private String licenseIssuer1;

    private String licenseType2;
    private String licenseLevel2;
    private LocalDate licenseDate2;
    private String licenseIssuer2;

    private String licenseType3;
    private String licenseLevel3;
    private LocalDate licenseDate3;
    private String licenseIssuer3;

    private String middleSchoolName;
    private LocalDate middleSchoolStartDate;
    private LocalDate middleSchoolEndDate;
    private boolean middleSchoolGraduated;

    private String highSchoolName;
    private String highSchoolMajor;
    private LocalDate highSchoolStartDate;
    private LocalDate highSchoolEndDate;
    private boolean highSchoolGraduated;

    private String universityName;
    private String universityMajor;
    private LocalDate universityStartDate;
    private LocalDate universityEndDate;
    private boolean universityGraduated;

    // =====================
    // 취미 / 특기
    // =====================
    private String hobby;
    private String specialty;

    private String portfolioUrl;

    private ApplicationStatus status;
    private LocalDateTime appliedAt;

    public static ApplicationDetailResponse from(Application app) {
        return ApplicationDetailResponse.builder()
                .applicationId(app.getId())
                .postingId(app.getPostingId())
                .writerId(app.getWriterId())

                // =====================
                // 기본 정보
                // =====================
                .name(app.getName())
                .email(app.getEmail())
                .phone(app.getPhone())
                .address(app.getAddress())

                // =====================
                // 학력 - 중학교
                // =====================
                .middleSchoolName(app.getMiddleSchoolName())
                .middleSchoolStartDate(app.getMiddleSchoolStartDate())
                .middleSchoolEndDate(app.getMiddleSchoolEndDate())
                .middleSchoolGraduated(app.getMiddleSchoolGraduated())

                // =====================
                // 학력 - 고등학교
                // =====================
                .highSchoolName(app.getHighSchoolName())
                .highSchoolMajor(app.getHighSchoolMajor())
                .highSchoolStartDate(app.getHighSchoolStartDate())
                .highSchoolEndDate(app.getHighSchoolEndDate())
                .highSchoolGraduated(app.getHighSchoolGraduated())

                // =====================
                // 학력 - 대학교
                // =====================
                .universityName(app.getUniversityName())
                .universityMajor(app.getUniversityMajor())
                .universityStartDate(app.getUniversityStartDate())
                .universityEndDate(app.getUniversityEndDate())
                .universityGraduated(app.getUniversityGraduated())

                // =====================
                // 대외활동
                // =====================
                .activities(app.getActivities())

                // =====================
                // 자기소개
                // =====================
                .introduction(app.getIntroduction())
                .motivation(app.getMotivation())
                .personality(app.getPersonality())
                .futureGoal(app.getFutureGoal())

                // =====================
                // 신체
                // =====================
                .height(app.getHeight())
                .weight(app.getWeight())
                .blood(app.getBlood())

                // =====================
                // 병역
                // =====================
                .militaryStatus(app.getMilitaryStatus())
                .militaryBranch(app.getMilitaryBranch())
                .militaryType(app.getMilitaryType())
                .militaryRank(app.getMilitaryRank())
                .militaryStartDate(app.getMilitaryStartDate())
                .militaryEndDate(app.getMilitaryEndDate())
                .militaryExemptReason(app.getMilitaryExemptReason())

                // =====================
                // 수상 내역
                // =====================
                .awardName1(app.getAwardName1())
                .awardDate1(app.getAwardDate1())
                .awardIssuer1(app.getAwardIssuer1())

                .awardName2(app.getAwardName2())
                .awardDate2(app.getAwardDate2())
                .awardIssuer2(app.getAwardIssuer2())

                .awardName3(app.getAwardName3())
                .awardDate3(app.getAwardDate3())
                .awardIssuer3(app.getAwardIssuer3())

                // =====================
                // 외국어
                // =====================
                .foreignLangAbility1(app.getForeignLangAbility1())
                .foreignLangTest1(app.getForeignLangTest1())
                .foreignLangScore1(app.getForeignLangScore1())

                .foreignLangAbility2(app.getForeignLangAbility2())
                .foreignLangTest2(app.getForeignLangTest2())
                .foreignLangScore2(app.getForeignLangScore2())

                // =====================
                // 가족
                // =====================
                .familyRelation1(app.getFamilyRelation1())
                .familyName1(app.getFamilyName1())
                .familyAge1(app.getFamilyAge1())
                .familyJob1(app.getFamilyJob1())

                .familyRelation2(app.getFamilyRelation2())
                .familyName2(app.getFamilyName2())
                .familyAge2(app.getFamilyAge2())
                .familyJob2(app.getFamilyJob2())

                .familyRelation3(app.getFamilyRelation3())
                .familyName3(app.getFamilyName3())
                .familyAge3(app.getFamilyAge3())
                .familyJob3(app.getFamilyJob3())

                .familyRelation4(app.getFamilyRelation4())
                .familyName4(app.getFamilyName4())
                .familyAge4(app.getFamilyAge4())
                .familyJob4(app.getFamilyJob4())

                // =====================
                // 자격증
                // =====================
                .licenseType1(app.getLicenseType1())
                .licenseLevel1(app.getLicenseLevel1())
                .licenseDate1(app.getLicenseDate1())
                .licenseIssuer1(app.getLicenseIssuer1())

                .licenseType2(app.getLicenseType2())
                .licenseLevel2(app.getLicenseLevel2())
                .licenseDate2(app.getLicenseDate2())
                .licenseIssuer2(app.getLicenseIssuer2())

                .licenseType3(app.getLicenseType3())
                .licenseLevel3(app.getLicenseLevel3())
                .licenseDate3(app.getLicenseDate3())
                .licenseIssuer3(app.getLicenseIssuer3())

                // =====================
                // 기타
                // =====================
                .hobby(app.getHobby())
                .specialty(app.getSpecialty())
                .portfolioUrl(app.getPortfolioUrl())

                .status(app.getStatus())
                .appliedAt(app.getCreatedAt())
                .build();
    }
}
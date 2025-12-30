package com.jobmanager.job_manager.dto.application;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "채용 공고 지원서 생성 요청")
public class ApplicationCreateRequest {

    @Schema(example = "7")
    private Long postingId;

    // =====================
    // 기본 정보
    // =====================
    @Schema(example = "홍길동")
    private String name;

    @Schema(example = "2008-08-09")
    private String birth; // yyyy-MM-dd

    @Schema(example = "hong@example.com")
    private String email;

    @Schema(example = "010-1234-5678")
    private String phone;

    @Schema(example = "서울특별시 강남구 테헤란로 123")
    private String address;

    @Schema(example = "https://example.com/profile.jpg")
    private String profileImageUrl;

    // =====================
    // 대외활동
    // =====================
    @Schema(example = "교내 프로그래밍 동아리 활동")
    private String activities;

    // =====================
    // 자기소개
    // =====================
    @Schema(example = "백엔드 개발자를 목표로 하고 있습니다.")
    private String introduction;

    @Schema(example = "실무 경험을 쌓기 위해 지원했습니다.")
    private String motivation;

    @Schema(example = "책임감 있고 성실합니다.")
    private String personality;

    @Schema(example = "신뢰받는 서버 개발자가 되는 것입니다.")
    private String futureGoal;

    // =====================
    // 신체 (숫자형)
    // =====================
    @Schema(example = "175")
    private Integer height;

    @Schema(example = "68")
    private Integer weight;

    @Schema(example = "A")
    private String blood;

    // =====================
    // 병역 (기간 분리)
    // =====================
    @Schema(example = "군필")
    private String militaryStatus;

    @Schema(example = "육군")
    private String militaryBranch;

    @Schema(example = "현역")
    private String militaryType;

    @Schema(example = "병장")
    private String militaryRank;

    @Schema(example = "2021-03-01")
    private String militaryStartDate; // yyyy-MM-dd

    @Schema(example = "2023-02-28")
    private String militaryEndDate;   // yyyy-MM-dd

    @Schema(example = "해당 없음")
    private String militaryExemptReason;

    // =====================
    // 수상 내역
    // =====================
    private String awardName1;
    private String awardDate1;
    private String awardIssuer1;

    private String awardName2;
    private String awardDate2;
    private String awardIssuer2;

    private String awardName3;
    private String awardDate3;
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
    // 가족 (최대 4명)
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
    private String licenseDate1;
    private String licenseIssuer1;

    private String licenseType2;
    private String licenseLevel2;
    private String licenseDate2;
    private String licenseIssuer2;

    private String licenseType3;
    private String licenseLevel3;
    private String licenseDate3;
    private String licenseIssuer3;

    // =====================
    // 학력 - 중학교
    // =====================
    private String middleSchoolName;
    private String middleSchoolStartDate;
    private String middleSchoolEndDate;
    private boolean middleSchoolGraduated;

    // =====================
    // 학력 - 고등학교
    // =====================
    private String highSchoolName;
    private String highSchoolMajor;
    private String highSchoolStartDate;
    private String highSchoolEndDate;
    private boolean highSchoolGraduated;

    // =====================
    // 학력 - 대학교
    // =====================
    private String universityName;
    private String universityMajor;
    private String universityStartDate;
    private String universityEndDate;
    private boolean universityGraduated;

    // =====================
    // 취미 / 특기
    // =====================
    private String hobby;
    private String specialty;

    // =====================
    // 기타
    // =====================
    @Schema(example = "https://github.com/example")
    private String portfolioUrl;
}
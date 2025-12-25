package com.jobmanager.job_manager.dto.application;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(
        description = "채용 공고 지원서 생성 요청",
        example = """
        {
          "postingId": 7,
          "name": "홍길동",
          "birth": "2008-08-09",
          "email": "hong@example.com",
          "phone": "010-1234-5678",
          "address": "서울특별시 강남구 테헤란로 123",
          "profileImageUrl": "https://example.com/profile.jpg",
          "educationSchool": "서울소프트웨어고등학교",
          "educationMajor": "소프트웨어개발과",
          "educationDegree": "고등학교",
          "educationStartDate": "2023-03-02",
          "educationEndDate": "2026-02-28",
          "educationGraduated": false,
          "activities": "교내 프로그래밍 동아리 활동",
          "introduction": "백엔드 개발자를 목표로 하고 있습니다.",
          "motivation": "실무 경험을 쌓기 위해 지원했습니다.",
          "personality": "책임감 있고 성실합니다.",
          "futureGoal": "신뢰받는 서버 개발자가 되는 것입니다.",
          "portfolioUrl": "https://github.com/example"
        }
        """
)
public class ApplicationCreateRequest {

    private Long postingId;

    // 기본 정보
    private String name;
    private String birth;          // yyyy-MM-dd
    private String email;
    private String phone;
    private String address;
    private String profileImageUrl;

    // 학력
    private String educationSchool;
    private String educationMajor;

    /**
     * 학교 구분 (예: 고등학교 / 전문대 / 대학교 / 대학원)
     */
    private String educationDegree;

    private String educationStartDate;
    private String educationEndDate;
    private boolean educationGraduated;

    // 대외활동
    private String activities;

    // 자기소개
    private String introduction;
    private String motivation;
    private String personality;
    private String futureGoal;

    // 기타
    private String portfolioUrl;
}
package com.jobmanager.job_manager.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserProfileUpdateRequest {

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "이메일", example = "hong@example.com")
    private String email;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;

    @Schema(
            description = "보유 자격증 (자유 입력)",
            example = "[\"정보처리기사\", \"SQLD\"]"
    )
    private List<String> license;

    @Schema(
            description = "보유 기술",
            example = "[\"리액트\", \"스프링\"]"
    )
    private List<String> skills;

    @Schema(description = "외국어 능력", example = "영어(중급), 일본어(기초)")
    private String foreignLang;

    @Schema(description = "대외활동", example = "교내 개발 동아리 활동, 해커톤 참가")
    private String activity;

    @Schema(
            description = "지원 동기 / 자기소개",
            example = "책임감을 가지고 맡은 일을 끝까지 수행하는 성격입니다. 새로운 환경에서도 빠르게 적응하며 팀워크를 중요하게 생각합니다."
    )
    private String motivation;
}
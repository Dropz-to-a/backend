package com.jobmanager.job_manager.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "내 프로필 수정 요청")
public class UserProfileUpdateRequest {

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "이메일", example = "hong@example.com")
    private String email;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;

    @Schema(
            description = "보유 기술",
            example = "[\"Spring Boot\", \"JPA\", \"MySQL\"]"
    )
    private List<String> skills;

    @Schema(
            description = "보유 자격증",
            example = "[\"정보처리기사\", \"SQLD\"]"
    )
    private List<String> licenses;

    @Schema(
            description = "외국어 능력",
            example = "[\"영어(중급)\", \"일본어(기초)\"]"
    )
    private List<String> foreignLangs;

    @Schema(description = "지원 동기 / 자기소개")
    private String motivation;
}

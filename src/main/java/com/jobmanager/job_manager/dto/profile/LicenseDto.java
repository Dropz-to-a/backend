package com.jobmanager.job_manager.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LicenseDto {

    @Schema(description = "자격증 종류 및 등급", example = "정보처리기사")
    private String name;

    @Schema(description = "취득일", example = "2024-06-15")
    private LocalDate acquiredDate;

    @Schema(description = "발행처", example = "한국산업인력공단")
    private String issuer;
}

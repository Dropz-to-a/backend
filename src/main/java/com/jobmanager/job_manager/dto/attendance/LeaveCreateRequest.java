package com.jobmanager.job_manager.dto.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveCreateRequest {

    @Schema(description = "휴가를 받는 직원의 account_id", example = "1")
    @NotNull
    private Long employeeAccountId;

    @Schema(description = "휴가를 등록하는 회사의 account_id", example = "8")
    @NotNull
    private Long companyAccountId;

    @Schema(description = "휴가 시작일 (yyyy-MM-dd)", example = "2025-11-25")
    @NotBlank
    private String startDate;

    @Schema(description = "휴가 종료일 (yyyy-MM-dd)", example = "2025-11-25")
    @NotBlank
    private String endDate;

    @Schema(
            description = "휴가 유형 (FULL_DAY / HALF_DAY_AM / HALF_DAY_PM 등)",
            example = "FULL_DAY"
    )
    @NotBlank
    private String leaveType;   // 이 필드 때문에 getLeaveType() 이 생김

    @Schema(description = "휴가 사유", example = "병원 진료")
    private String reason;
}

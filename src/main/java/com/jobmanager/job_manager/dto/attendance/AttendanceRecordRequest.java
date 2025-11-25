package com.jobmanager.job_manager.dto.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendanceRecordRequest {

    @Schema(description = "출근·퇴근 처리되는 직원의 account_id (USER 계정 ID)", example = "1")
    private Long employeeAccountId;

    @Schema(description = "해당 직원의 소속 회사 account_id (COMPANY 계정 ID)", example = "8")
    private Long companyAccountId;
}

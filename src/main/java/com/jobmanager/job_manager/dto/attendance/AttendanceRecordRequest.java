package com.jobmanager.job_manager.dto.attendance;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 회사 계정이 직원의 출근/퇴근을 찍어줄 때 사용하는 요청 DTO
 */
@Getter
@Setter
public class AttendanceRecordRequest {

    @NotNull
    private Long employeeAccountId;  // 직원 계정 ID

    @NotNull
    private Long companyAccountId;   // 회사 계정 ID
}

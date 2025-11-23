package com.jobmanager.job_manager.dto.attendance;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 회사 계정이 직원 휴가를 등록할 때 사용
 */
@Getter
@Setter
public class LeaveCreateRequest {

    @NotNull
    private Long employeeAccountId;

    @NotNull
    private Long companyAccountId;

    private String leaveType;
    private String reason;
    private String startDate; // "2025-11-10" 형식
    private String endDate;   // "2025-11-10" 형식
}

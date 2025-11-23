package com.jobmanager.job_manager.dto.attendance;

import com.jobmanager.job_manager.entity.UserLeave;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class LeaveResponse {

    private Long id;
    private Long employeeAccountId;
    private Long companyAccountId;
    private String leaveType;
    private String reason;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean approved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static LeaveResponse from(UserLeave e) {
        return LeaveResponse.builder()
                .id(e.getId())
                .employeeAccountId(e.getAccountId())
                .companyAccountId(e.getCompanyId())
                .leaveType(e.getLeaveType())
                .reason(e.getReason())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .approved(e.getApproved())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}

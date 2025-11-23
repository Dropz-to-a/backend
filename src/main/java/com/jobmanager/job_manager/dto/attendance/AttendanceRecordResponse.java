package com.jobmanager.job_manager.dto.attendance;

import com.jobmanager.job_manager.entity.UserAttendance;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class AttendanceRecordResponse {

    private Long id;
    private Long employeeAccountId;
    private Long companyAccountId;
    private LocalDate workDate;
    private LocalDateTime clockIn;
    private LocalDateTime clockOut;
    private String status;

    public static AttendanceRecordResponse from(UserAttendance entity) {
        return AttendanceRecordResponse.builder()
                .id(entity.getId())
                .employeeAccountId(entity.getAccountId())
                .companyAccountId(entity.getCompanyId())
                .workDate(entity.getWorkDate())
                .clockIn(entity.getClockIn())
                .clockOut(entity.getClockOut())
                .status(entity.getStatus())
                .build();
    }
}

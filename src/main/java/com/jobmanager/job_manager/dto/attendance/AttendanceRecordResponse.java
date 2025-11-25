package com.jobmanager.job_manager.dto.attendance;

import com.jobmanager.job_manager.entity.UserAttendance;
import io.swagger.v3.oas.annotations.media.Schema;   // <= 이거 추가
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class AttendanceRecordResponse {

    @Schema(description = "출퇴근 기록 ID", example = "12")
    private Long id;

    @Schema(description = "직원 계정 ID", example = "1")
    private Long employeeAccountId;

    @Schema(description = "회사 계정 ID", example = "8")
    private Long companyAccountId;

    @Schema(description = "근무 날짜 (yyyy-MM-dd)", example = "2025-11-24")
    private LocalDate workDate;

    @Schema(description = "출근 시간 (null이면 미출근)", example = "2025-11-24T09:05:23")
    private LocalDateTime clockIn;

    @Schema(description = "퇴근 시간 (null이면 아직 퇴근 안함)", example = "2025-11-24T18:01:10")
    private LocalDateTime clockOut;

    @Schema(description = "근무 상태 (PRESENT / LATE / ABSENT)", example = "PRESENT")
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

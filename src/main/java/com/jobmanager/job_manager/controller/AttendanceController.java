package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.attendance.AttendanceRecordRequest;
import com.jobmanager.job_manager.dto.attendance.AttendanceRecordResponse;
import com.jobmanager.job_manager.entity.UserAttendance;
import com.jobmanager.job_manager.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Attendance", description = "출근/퇴근 관리 API")
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /** 회사가 직원 출근 찍기 */
    @Operation(summary = "출근 기록 생성", description = "회사 계정이 특정 직원의 출근을 기록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "출근 기록 성공",
                    content = @Content(schema = @Schema(implementation = AttendanceRecordResponse.class))),
            @ApiResponse(responseCode = "400", description = "이미 출근이 찍힌 경우")
    })
    @PostMapping("/clock-in")
    public AttendanceRecordResponse clockIn(@RequestBody @Valid AttendanceRecordRequest req) {
        UserAttendance result = attendanceService.clockIn(req);
        return AttendanceRecordResponse.from(result);
    }

    /** 회사가 직원 퇴근 찍기 */
    @Operation(summary = "퇴근 기록 생성", description = "회사 계정이 특정 직원의 퇴근을 기록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "퇴근 기록 성공",
                    content = @Content(schema = @Schema(implementation = AttendanceRecordResponse.class))),
            @ApiResponse(responseCode = "400", description = "출근 기록 없음 또는 이미 퇴근됨")
    })
    @PostMapping("/clock-out")
    public AttendanceRecordResponse clockOut(@RequestBody @Valid AttendanceRecordRequest req) {
        UserAttendance result = attendanceService.clockOut(req);
        return AttendanceRecordResponse.from(result);
    }
}

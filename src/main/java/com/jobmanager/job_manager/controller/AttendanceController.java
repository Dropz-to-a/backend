package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.attendance.AttendanceRecordRequest;
import com.jobmanager.job_manager.dto.attendance.AttendanceRecordResponse;
import com.jobmanager.job_manager.entity.UserAttendance;
import com.jobmanager.job_manager.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /** 회사가 직원 출근 찍기 */
    @PostMapping("/clock-in")
    public AttendanceRecordResponse clockIn(@RequestBody @Valid AttendanceRecordRequest req) {
        UserAttendance result = attendanceService.clockIn(req);
        return AttendanceRecordResponse.from(result);
    }

    /** 회사가 직원 퇴근 찍기 */
    @PostMapping("/clock-out")
    public AttendanceRecordResponse clockOut(@RequestBody @Valid AttendanceRecordRequest req) {
        UserAttendance result = attendanceService.clockOut(req);
        return AttendanceRecordResponse.from(result);
    }
}

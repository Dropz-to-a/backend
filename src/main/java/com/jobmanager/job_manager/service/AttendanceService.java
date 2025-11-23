package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.attendance.AttendanceRecordRequest;
import com.jobmanager.job_manager.entity.UserAttendance;
import com.jobmanager.job_manager.repository.UserAttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceService {

    private final UserAttendanceRepository attendanceRepository;

    /** 출근 찍기 */
    public UserAttendance clockIn(AttendanceRecordRequest req) {
        Long employeeId = req.getEmployeeAccountId();
        Long companyId = req.getCompanyAccountId();
        LocalDate today = LocalDate.now();

        UserAttendance attendance = attendanceRepository
                .findByAccountIdAndCompanyIdAndWorkDate(employeeId, companyId, today)
                .orElseGet(() -> UserAttendance.builder()
                        .accountId(employeeId)
                        .companyId(companyId)
                        .workDate(today)
                        .status("WORK") // 일단 기본값
                        .build()
                );

        if (attendance.getClockIn() != null) {
            throw new IllegalStateException("이미 출근이 찍혀 있습니다.");
        }

        attendance.setClockIn(LocalDateTime.now());

        return attendanceRepository.save(attendance);
    }

    /** 퇴근 찍기 */
    public UserAttendance clockOut(AttendanceRecordRequest req) {
        Long employeeId = req.getEmployeeAccountId();
        Long companyId = req.getCompanyAccountId();
        LocalDate today = LocalDate.now();

        UserAttendance attendance = attendanceRepository
                .findByAccountIdAndCompanyIdAndWorkDate(employeeId, companyId, today)
                .orElseThrow(() -> new IllegalStateException("오늘 출근 기록이 없습니다."));

        if (attendance.getClockOut() != null) {
            throw new IllegalStateException("이미 퇴근이 찍혀 있습니다.");
        }

        attendance.setClockOut(LocalDateTime.now());

        return attendanceRepository.save(attendance);
    }
}

package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.attendance.AttendanceRecordRequest;
import com.jobmanager.job_manager.entity.UserAttendance;
import com.jobmanager.job_manager.entity.Employee;
import com.jobmanager.job_manager.repository.AttendanceRepository;
import com.jobmanager.job_manager.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * 출근
     */
    @Transactional
    public UserAttendance clockIn(AttendanceRecordRequest req) {

        // 1) 먼저 employee-company 소속 확인
        boolean isMember = employeeRepository
                .existsByCompanyIdAndEmployeeId(req.getCompanyAccountId(), req.getEmployeeAccountId());

        if (!isMember) {
            throw new IllegalArgumentException("해당 직원은 이 회사에 소속되어 있지 않습니다.");
        }

        // 2) 이미 오늘 출근 기록이 있는지 확인
        LocalDate today = LocalDate.now();
        UserAttendance existing = attendanceRepository
                .findByAccountIdAndWorkDate(req.getEmployeeAccountId(), today)
                .orElse(null);

        if (existing != null) {
            throw new IllegalArgumentException("이미 오늘 출근 기록이 있습니다.");
        }

        // 3) 출근 기록 생성
        UserAttendance att = UserAttendance.builder()
                .accountId(req.getEmployeeAccountId())
                .companyId(req.getCompanyAccountId())
                .workDate(today)
                .clockIn(LocalDateTime.now())
                .status("WORKING")
                .build();

        return attendanceRepository.save(att);
    }


    /**
     * 퇴근
     */
    @Transactional
    public UserAttendance clockOut(AttendanceRecordRequest req) {

        // 1) employee-company 소속 확인
        boolean isMember = employeeRepository
                .existsByCompanyIdAndEmployeeId(req.getCompanyAccountId(), req.getEmployeeAccountId());

        if (!isMember) {
            throw new IllegalArgumentException("해당 직원은 이 회사에 소속되어 있지 않습니다.");
        }

        // 2) 출근 기록 불러오기
        LocalDate today = LocalDate.now();
        UserAttendance att = attendanceRepository
                .findByAccountIdAndWorkDate(req.getEmployeeAccountId(), today)
                .orElseThrow(() -> new IllegalArgumentException("오늘 출근 기록이 없습니다."));

        if (att.getClockOut() != null) {
            throw new IllegalArgumentException("이미 퇴근 처리가 완료된 기록입니다.");
        }

        // 3) 퇴근 처리
        att.setClockOut(LocalDateTime.now());
        att.setStatus("DONE");

        return attendanceRepository.save(att);
    }
}

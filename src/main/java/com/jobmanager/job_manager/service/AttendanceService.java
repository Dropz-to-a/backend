package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.attendance.AttendanceRecordRequest;
import com.jobmanager.job_manager.entity.UserAttendance;
import com.jobmanager.job_manager.repository.AttendanceRepository;
import com.jobmanager.job_manager.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public UserAttendance clockIn(AttendanceRecordRequest req) {

        validateMembership(req.getCompanyAccountId(), req.getEmployeeAccountId());

        LocalDate today = LocalDate.now();

        attendanceRepository.findByAccountIdAndWorkDate(req.getEmployeeAccountId(), today)
                .ifPresent(att -> { throw new IllegalArgumentException("이미 오늘 출근 기록이 있습니다."); });

        UserAttendance att = UserAttendance.builder()
                .accountId(req.getEmployeeAccountId())
                .companyId(req.getCompanyAccountId())
                .workDate(today)
                .clockIn(LocalDateTime.now())
                .status("WORKING")
                .build();

        return attendanceRepository.save(att);
    }

    @Transactional
    public UserAttendance clockOut(AttendanceRecordRequest req) {

        validateMembership(req.getCompanyAccountId(), req.getEmployeeAccountId());

        LocalDate today = LocalDate.now();

        UserAttendance att = attendanceRepository
                .findByAccountIdAndWorkDate(req.getEmployeeAccountId(), today)
                .orElseThrow(() -> new IllegalArgumentException("오늘 출근 기록이 없습니다."));

        if (att.getClockOut() != null) {
            throw new IllegalArgumentException("이미 퇴근 처리가 완료된 기록입니다.");
        }

        att.setClockOut(LocalDateTime.now());
        att.setStatus("DONE");

        return attendanceRepository.save(att);
    }

    /** ⭐ 근태 기록 조회 기능 추가 */
    @Transactional(readOnly = true)
    public List<UserAttendance> getHistory(
            Long companyId,
            Long employeeId,
            LocalDate fromDate,
            LocalDate toDate
    ) {

        if (employeeId == null) {
            // 회사 전체 직원
            return attendanceRepository.findByCompanyIdAndWorkDateBetween(
                    companyId, fromDate, toDate
            );
        }

        // 특정 직원만
        return attendanceRepository.findByCompanyIdAndAccountIdAndWorkDateBetween(
                companyId, employeeId, fromDate, toDate
        );
    }

    /** 소속 확인 공통 로직 */
    private void validateMembership(Long companyId, Long employeeId) {
        boolean isMember = employeeRepository.existsByCompanyIdAndEmployeeId(companyId, employeeId);
        if (!isMember) {
            throw new IllegalArgumentException("해당 직원은 이 회사에 소속되어 있지 않습니다.");
        }
    }
}
package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.attendance.LeaveCreateRequest;
import com.jobmanager.job_manager.entity.UserLeave;
import com.jobmanager.job_manager.repository.UserLeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveService {

    private final UserLeaveRepository userLeaveRepository;

    /** 휴가 등록 (회사 → 직원) */
    public UserLeave createLeave(LeaveCreateRequest req) {
        UserLeave leave = UserLeave.builder()
                .accountId(req.getEmployeeAccountId())
                .companyId(req.getCompanyAccountId())
                .leaveType(req.getLeaveType())
                .reason(req.getReason())
                .startDate(parseDate(req.getStartDate()))
                .endDate(parseDate(req.getEndDate()))
                .approved(false)
                .build();

        return userLeaveRepository.save(leave);
    }

    /** 휴가 승인 */
    public UserLeave approve(Long leaveId) {
        UserLeave leave = userLeaveRepository.findById(leaveId)
                .orElseThrow(() -> new IllegalArgumentException("휴가 정보를 찾을 수 없습니다."));

        leave.setApproved(true);
        return leave; // JPA dirty checking
    }

    /** 회사 기준 직원 휴가 목록 */
    @Transactional(readOnly = true)
    public List<UserLeave> getLeaves(Long companyId, Long employeeId) {
        return userLeaveRepository.findByCompanyIdAndAccountId(companyId, employeeId);
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        return LocalDate.parse(s); // "yyyy-MM-dd"
    }
}

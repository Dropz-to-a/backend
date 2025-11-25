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

    public UserLeave createLeave(LeaveCreateRequest req) {

        UserLeave leave = UserLeave.builder()
                .accountId(req.getEmployeeAccountId())
                .companyId(req.getCompanyAccountId())
                .leaveType(req.getLeaveType())
                .reason(req.getReason())
                .startDate(parse(req.getStartDate()))
                .endDate(parse(req.getEndDate()))
                .approved(false)
                .build();

        return userLeaveRepository.save(leave);
    }

    public UserLeave approve(Long leaveId) {
        UserLeave leave = userLeaveRepository.findById(leaveId)
                .orElseThrow(() -> new IllegalArgumentException("휴가 정보를 찾을 수 없습니다."));

        leave.setApproved(true);
        return leave;
    }

    @Transactional(readOnly = true)
    public List<UserLeave> getLeaves(Long companyId, Long employeeId) {
        return userLeaveRepository.findByCompanyIdAndAccountId(companyId, employeeId);
    }

    private LocalDate parse(String s) {
        return (s == null || s.isBlank()) ? null : LocalDate.parse(s);
    }
}

package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.attendance.LeaveCreateRequest;
import com.jobmanager.job_manager.dto.attendance.LeaveResponse;
import com.jobmanager.job_manager.entity.UserLeave;
import com.jobmanager.job_manager.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    /** 회사가 직원 휴가 등록 */
    @PostMapping
    public LeaveResponse create(@RequestBody @Valid LeaveCreateRequest req) {
        UserLeave leave = leaveService.createLeave(req);
        return LeaveResponse.from(leave);
    }

    /** 회사가 휴가 승인 */
    @PatchMapping("/{leaveId}/approve")
    public LeaveResponse approve(@PathVariable Long leaveId) {
        UserLeave leave = leaveService.approve(leaveId);
        return LeaveResponse.from(leave);
    }

    /** 회사 기준 직원 휴가 목록 조회 */
    @GetMapping
    public List<LeaveResponse> list(
            @RequestParam Long companyId,
            @RequestParam Long employeeAccountId
    ) {
        return leaveService.getLeaves(companyId, employeeAccountId)
                .stream()
                .map(LeaveResponse::from)
                .toList();
    }
}

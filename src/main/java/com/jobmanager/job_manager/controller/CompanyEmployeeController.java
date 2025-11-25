package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.company.CompanyEmployeeAssignRequest;
import com.jobmanager.job_manager.global.jwt.JwtHeaderUtils;
import com.jobmanager.job_manager.global.jwt.JwtTokenProvider;
import com.jobmanager.job_manager.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Company-Employee", description = "회사 직원 등록/해제 API")
@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyEmployeeController {

    private final EmployeeService employeeService;
    private final JwtTokenProvider jwt;

    /** JWT에서 현재 로그인된 회사의 accountId 추출 */
    private long currentCompanyId() {
        String token = JwtHeaderUtils.getTokenFromHeader(); // 헤더에서 토큰 추출
        return jwt.getAccountId(token);                     // ★ 여기서도 토큰 넘겨주기
    }

    /** 직원 등록 */
    @Operation(summary = "직원 등록", description = "현재 로그인된 회사에 직원(사용자)을 소속시킵니다.")
    @PostMapping("/assign-employee")
    public String assignEmployee(@RequestBody CompanyEmployeeAssignRequest req) {
        long companyId = currentCompanyId();
        employeeService.assignEmployee(companyId, req.getEmployeeId());
        return "등록 완료";
    }

    /** 직원 해제 */
    @Operation(summary = "직원 해제", description = "현재 로그인된 회사에서 특정 직원을 소속 해제합니다.")
    @DeleteMapping("/remove-employee")
    public String removeEmployee(@RequestParam Long employeeId) {
        long companyId = currentCompanyId();
        employeeService.removeEmployee(companyId, employeeId);
        return "해제 완료";
    }
}

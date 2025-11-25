package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.entity.Company;
import com.jobmanager.job_manager.dto.company.EmployeeStatusResponse;
import com.jobmanager.job_manager.global.jwt.JwtHeaderUtils;
import com.jobmanager.job_manager.service.CompanyService;
import com.jobmanager.job_manager.global.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Company", description = "회사 정보 및 직원 관리 API")
@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final JwtTokenProvider jwt;

    /** JWT에서 현재 로그인한 회사 ID 추출 */
    private long currentCompanyId() {
        String token = JwtHeaderUtils.getTokenFromHeader(); // util로 분리
        return jwt.getAccountId(token);
    }


    @GetMapping("/me")
    @Operation(summary = "내 회사 정보 조회")
    public Company getMyCompany() {
        return companyService.getCompany(currentCompanyId());
    }

    @GetMapping("/employees")
    @Operation(summary = "내 회사 직원 목록 조회")
    public List<?> getEmployees() {
        return companyService.getMyEmployees(currentCompanyId());
    }

    @GetMapping("/employees/status")
    @Operation(summary = "직원 상태 조회(출근/퇴근/휴가)")
    public List<EmployeeStatusResponse> getStatuses() {
        return companyService.getEmployeeStatuses(currentCompanyId());
    }
}

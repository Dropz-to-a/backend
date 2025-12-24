package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.company.AssignTeamRequest;
import com.jobmanager.job_manager.dto.company.CompanyEmployeeAssignRequest;
import com.jobmanager.job_manager.dto.company.CompanyEmployeeResponse;
import com.jobmanager.job_manager.dto.company.CompanyTeamResponse;
import com.jobmanager.job_manager.dto.company.CreateTeamRequest;
import com.jobmanager.job_manager.dto.company.CreateTeamResponse;
import com.jobmanager.job_manager.global.jwt.JwtHeaderUtils;
import com.jobmanager.job_manager.global.jwt.JwtTokenProvider;
import com.jobmanager.job_manager.global.jwt.SimpleUserPrincipal;
import com.jobmanager.job_manager.service.CompanyEmployeeQueryService;
import com.jobmanager.job_manager.service.CompanyTeamService;
import com.jobmanager.job_manager.service.EmployeeService;
import com.jobmanager.job_manager.service.EmployeeTeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Company-Employee",
        description = """
            회사(COMPANY) 계정 기준으로 직원(USER) 및 부서(팀) 소속 관계를 관리하는 API입니다.
            """
)
@RestController
@RequestMapping("/api/company/manage")
@RequiredArgsConstructor
public class CompanyEmployeeController {

    private final EmployeeService employeeService;
    private final JwtTokenProvider jwt;
    private final CompanyTeamService companyTeamService;
    private final EmployeeTeamService employeeTeamService;
    private final CompanyEmployeeQueryService companyEmployeeQueryService;

    /** JWT에서 현재 로그인한 회사 accountId 추출 */
    private long currentCompanyId() {
        String token = JwtHeaderUtils.getTokenFromHeader();
        return jwt.getAccountId(token);
    }

    /* ==================================================
       직원 관리
       ================================================== */

    @Operation(
            summary = "직원 등록",
            description = """
                현재 로그인한 회사(COMPANY)가 USER 계정을 직원으로 등록합니다.
                """
    )
    @PostMapping("/employees")
    public String assignEmployee(
            Authentication authentication,
            @RequestBody CompanyEmployeeAssignRequest req
    ) {
        validateCompany(authentication);
        employeeService.assignEmployee(currentCompanyId(), req.getEmployeeId());
        return "등록 완료";
    }

    @Operation(
            summary = "직원 해제",
            description = """
                현재 로그인한 회사(COMPANY)에서 특정 직원을 소속 해제합니다.
                """
    )
    @DeleteMapping("/employees/{employeeId}")
    public String removeEmployee(
            Authentication authentication,
            @PathVariable Long employeeId
    ) {
        validateCompany(authentication);
        employeeService.removeEmployee(currentCompanyId(), employeeId);
        return "해제 완료";
    }


    /* ==================================================
       부서(팀) 관리
       ================================================== */

    @Operation(summary = "부서(팀) 생성")
    @PostMapping("/teams")
    public CreateTeamResponse createTeam(
            Authentication authentication,
            @RequestBody @Valid CreateTeamRequest req
    ) {
        SimpleUserPrincipal principal =
                (SimpleUserPrincipal) authentication.getPrincipal();

        validateCompany(authentication);

        return CreateTeamResponse.from(
                companyTeamService.createTeam(principal.getAccountId(), req)
        );
    }

    @Operation(summary = "부서(팀) 삭제")
    @DeleteMapping("/teams/{teamId}")
    public String deleteTeam(
            Authentication authentication,
            @PathVariable Long teamId
    ) {
        validateCompany(authentication);
        companyTeamService.deleteTeam(currentCompanyId(), teamId);
        return "부서 삭제 완료";
    }

    @Operation(summary = "직원 부서 최초 지정")
    @PostMapping("/teams/assign")
    public String assignTeam(
            Authentication authentication,
            @RequestBody AssignTeamRequest req
    ) {
        validateCompany(authentication);
        employeeTeamService.assignTeam(
                currentCompanyId(),
                req.getEmployeeId(),
                req.getTeamId()
        );
        return "부서 지정 완료";
    }

    @Operation(summary = "직원 부서 변경")
    @PatchMapping("/teams/change")
    public String changeTeam(
            Authentication authentication,
            @RequestBody @Valid AssignTeamRequest req
    ) {
        validateCompany(authentication);

        employeeTeamService.changeTeam(
                currentCompanyId(),
                req.getEmployeeId(),
                req.getTeamId()
        );

        return "부서 변경 완료";
    }

    /* ==================================================
       조회 API
       ================================================== */

    @Operation(
            summary = "부서(팀) 목록 조회",
            description = "현재 로그인한 회사(COMPANY)가 보유한 부서 목록 조회"
    )
    @GetMapping("/teams")
    public List<CompanyTeamResponse> getTeams(Authentication authentication) {
        validateCompany(authentication);
        return companyEmployeeQueryService.getMyTeams(currentCompanyId());
    }

    @Operation(
            summary = "회사 직원 목록 조회 (부서 포함)",
            description = "현재 로그인한 회사(COMPANY)에 소속된 직원 목록 조회"
    )
    @GetMapping("/employees")
    public List<CompanyEmployeeResponse> getEmployees(Authentication authentication) {
        validateCompany(authentication);
        return companyEmployeeQueryService.getMyCompanyEmployees(currentCompanyId());
    }

    /* ==================================================
       공통 검증
       ================================================== */

    /** COMPANY 계정 여부 검증 */
    private void validateCompany(Authentication authentication) {
        String type = ((SimpleUserPrincipal) authentication.getPrincipal())
                .getAccountType();

        if (!"COMPANY".equals(type)) {
            throw new IllegalArgumentException("회사 계정만 접근할 수 있습니다.");
        }
    }
}
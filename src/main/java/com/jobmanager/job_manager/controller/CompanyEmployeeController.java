package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.company.AssignTeamRequest;
import com.jobmanager.job_manager.dto.company.CompanyEmployeeAssignRequest;
import com.jobmanager.job_manager.dto.company.CreateTeamRequest;
import com.jobmanager.job_manager.dto.company.CreateTeamResponse;
import com.jobmanager.job_manager.global.jwt.JwtHeaderUtils;
import com.jobmanager.job_manager.global.jwt.JwtTokenProvider;
import com.jobmanager.job_manager.global.jwt.SimpleUserPrincipal;
import com.jobmanager.job_manager.service.CompanyTeamService;
import com.jobmanager.job_manager.service.EmployeeService;
import com.jobmanager.job_manager.service.EmployeeTeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Company-Employee",
        description = """
            회사(COMPANY) 계정 기준으로 직원(USER) 및 부서(팀) 소속 관계를 관리하는 API입니다.
            """
)
@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyEmployeeController {

    private final EmployeeService employeeService;
    private final JwtTokenProvider jwt;
    private final CompanyTeamService companyTeamService;
    private final EmployeeTeamService employeeTeamService;

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

                사용 시나리오
                - 회사 관리자가 이미 가입된 USER 계정을 직원으로 초대

                조건
                - COMPANY 계정만 호출 가능
                - employeeId는 USER accountId 기준
                - 이미 다른 회사 소속이거나, 이미 우리 회사 직원인 경우 등록 불가
                """
    )
    @PostMapping("/assign-employee")
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

                사용 시나리오
                - 퇴사 처리
                - 더 이상 회사 소속이 아닌 직원 제거

                조건
                - COMPANY 계정만 호출 가능
                - 해당 회사에 실제로 소속된 직원만 해제 가능
                """
    )
    @DeleteMapping("/remove-employee")
    public String removeEmployee(
            Authentication authentication,
            @RequestParam Long employeeId
    ) {
        validateCompany(authentication);
        employeeService.removeEmployee(currentCompanyId(), employeeId);
        return "해제 완료";
    }

    /* ==================================================
       부서(팀) 관리
       ================================================== */

    @Operation(
            summary = "부서(팀) 생성",
            description = """
                현재 로그인한 회사(COMPANY)가 새로운 부서(팀)를 생성합니다.

                사용 시나리오
                - 조직 구성(개발팀, 기획팀 등)

                조건
                - COMPANY 계정만 호출 가능
                - 같은 회사 내 부서명 중복 불가
                """
    )
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

    @Operation(
            summary = "부서(팀) 삭제",
            description = """
                회사가 보유한 부서(팀)를 삭제합니다.

                동작 방식
                - 해당 부서에 소속된 직원들은 자동으로 부서 해제(team_id = NULL)

                조건
                - COMPANY 계정만 호출 가능
                - 해당 회사 소유의 부서만 삭제 가능
                """
    )
    @DeleteMapping("/teams/{teamId}")
    public String deleteTeam(
            Authentication authentication,
            @PathVariable Long teamId
    ) {
        validateCompany(authentication);
        companyTeamService.deleteTeam(currentCompanyId(), teamId);
        return "부서 삭제 완료";
    }

    @Operation(
            summary = "직원 부서 최초 지정",
            description = """
            회사 소속 직원에게 부서를 최초로 지정합니다.

            조건
            - COMPANY 계정만 호출 가능
            - 직원은 아직 부서가 없어야 함
            """
    )
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

    @Operation(
            summary = "직원 부서 변경",
            description = """
            회사 소속 직원의 부서를 변경합니다.

            사용 시나리오
            - 인사 이동

            조건
            - COMPANY 계정만 호출 가능
            - 직원은 기존에 부서가 지정되어 있어야 함
            """
    )
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
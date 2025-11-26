package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.company.EmployeeStatusResponse;
import com.jobmanager.job_manager.entity.Company;
import com.jobmanager.job_manager.global.jwt.JwtHeaderUtils;
import com.jobmanager.job_manager.global.jwt.JwtTokenProvider;
import com.jobmanager.job_manager.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Company",
        description = """
            회사 정보 및 회사 기준 직원 목록/상태를 조회하는 API입니다.
            - JWT 토큰에서 company accountId를 추출해 사용합니다.
            - 모든 엔드포인트는 회사 계정으로 로그인되어 있어야 합니다.
            """
)
@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final JwtTokenProvider jwt;

    /** JWT에서 현재 로그인한 회사 ID 추출 */
    private long currentCompanyId() {
        String token = JwtHeaderUtils.getTokenFromHeader(); // Authorization 헤더에서 Bearer 토큰 추출
        return jwt.getAccountId(token);                     // ★ 토큰을 넘겨서 accountId 얻기
    }

    @GetMapping("/me")
    @Operation(
            summary = "내 회사 정보 조회",
            description = """
                현재 로그인된 회사 계정 기준으로 회사 정보를 조회합니다.
                
                 인증
                - Authorization: Bearer {accessToken}
                - 토큰 안의 accountId가 companyId로 사용됩니다.
                """
    )
    @ApiResponse(
            responseCode = "200",
            description = "회사 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = Company.class))
    )
    public Company getMyCompany() {
        return companyService.getCompany(currentCompanyId());
    }

    @GetMapping("/employees")
    @Operation(
            summary = "내 회사 직원 목록 조회",
            description = """
                현재 로그인된 회사에 소속된 직원 목록을 조회합니다.
                
                 반환 데이터 예시
                - 직원 accountId
                - 이름/닉네임, 이메일
                - 고용 상태(재직/퇴사 등) (엔티티/DTO 설계에 따라 확장)
                """
    )
    @ApiResponse(
            responseCode = "200",
            description = "직원 목록 조회 성공"
    )
    public List<?> getEmployees() {
        return companyService.getMyEmployees(currentCompanyId());
    }

    @GetMapping("/employees/status")
    @Operation(
            summary = "직원 상태 조회 (출근/퇴근/휴가)",
            description = """
                현재 로그인된 회사 기준으로 직원들의 근태/휴가 상태를 조회합니다.
                
                 예시 상태
                - WORKING: 출근 상태
                - OFF: 퇴근 상태
                - ON_LEAVE: 휴가 중
                - 기타: 회사 정책에 따라 확장 가능
                
                프론트에서는 이 API를 호출해 대시보드에 현재 직원 상태를 표시할 수 있습니다.
                """
    )
    @ApiResponse(
            responseCode = "200",
            description = "직원 상태 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = EmployeeStatusResponse.class))
    )
    public List<EmployeeStatusResponse> getStatuses() {
        return companyService.getEmployeeStatuses(currentCompanyId());
    }
}
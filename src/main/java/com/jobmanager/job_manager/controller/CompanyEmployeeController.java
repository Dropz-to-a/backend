package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.company.CompanyEmployeeAssignRequest;
import com.jobmanager.job_manager.global.jwt.JwtHeaderUtils;
import com.jobmanager.job_manager.global.jwt.JwtTokenProvider;
import com.jobmanager.job_manager.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Company-Employee",
        description = """
            회사와 직원(사용자) 간 소속 관계를 관리하는 API입니다.
            - 회사가 직원으로 등록(assign)하거나, 소속 해제(remove)하는 기능을 제공합니다.
            """
)
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
    @Operation(
            summary = "직원 등록",
            description = """
                현재 로그인된 회사에 특정 사용자를 '직원'으로 등록합니다.
                
                 사용 시나리오
                - 회사가 이미 가입된 USER 계정을 직원으로 초대/등록하는 구조
                - employeeId는 accounts.id 기준입니다.
                
                 제약사항 (예시)
                - 이미 회사에 소속된 직원이면 중복 등록 불가
                - 존재하지 않는 employeeId인 경우 404 처리
                """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "요청 예시",
                                    value = """
                                        {
                                          "employeeId": 1
                                        }
                                        """
                            )
                    )
            )
    )
    @PostMapping("/assign-employee")
    public String assignEmployee(@RequestBody CompanyEmployeeAssignRequest req) {
        long companyId = currentCompanyId();
        employeeService.assignEmployee(companyId, req.getEmployeeId());
        return "등록 완료";
    }

    /** 직원 해제 */
    @Operation(
            summary = "직원 해제",
            description = """
                현재 로그인된 회사에서 특정 직원을 소속 해제합니다.
                
                 Query Parameter
                - employeeId : 소속을 해제할 직원의 account_id
                
                회사/직원 매핑이 존재하지 않으면 404 또는 400으로 처리할 수 있습니다(서비스 로직에 따라).
                """
    )
    @DeleteMapping("/remove-employee")
    public String removeEmployee(@RequestParam Long employeeId) {
        long companyId = currentCompanyId();
        employeeService.removeEmployee(companyId, employeeId);
        return "해제 완료";
    }
}

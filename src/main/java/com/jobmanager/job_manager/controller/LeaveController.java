package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.attendance.LeaveCreateRequest;
import com.jobmanager.job_manager.dto.attendance.LeaveResponse;
import com.jobmanager.job_manager.entity.UserLeave;
import com.jobmanager.job_manager.service.LeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Leave",
        description = """
            휴가(연차/반차/병가 등)를 관리하는 API입니다.
            - 회사가 직원의 휴가를 등록/승인/조회하는 시나리오를 가정합니다.
            """
)
@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    /**
     * 회사가 직원 휴가 등록
     */
    @Operation(
            summary = "휴가 등록",
            description = """
                회사 계정이 특정 직원의 휴가(연차/반차/병가 등)를 등록합니다.
                
                 Request Body 필드
                - employeeAccountId : 휴가를 사용하는 직원의 account_id
                - companyAccountId  : 회사의 account_id
                - startDate         : 휴가 시작일 (예: 2025-11-24)
                - endDate           : 휴가 종료일 (예: 2025-11-24)
                - leaveType         : 휴가 유형 (예: FULL, HALF_AM, HALF_PM 등 프로젝트에서 정의한 값)
                - reason            : 휴가 사유 (예: "병원 진료")
                
                 비즈니스 규칙 예시
                - startDate > endDate 인 경우 400 에러
                - 같은 날짜에 중복 휴가가 이미 존재하면 400 에러
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "휴가 등록 성공",
                    content = @Content(
                            schema = @Schema(implementation = LeaveResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 예시",
                                    value = """
                                        {
                                          "leaveId": 10,
                                          "employeeAccountId": 1,
                                          "companyAccountId": 8,
                                          "leaveType": "FULL",
                                          "startDate": "2025-11-24",
                                          "endDate": "2025-11-24",
                                          "status": "REQUESTED",
                                          "reason": "병원 진료"
                                        }
                                        """
                            )
                    )
            )
    })
    @PostMapping
    public LeaveResponse create(@RequestBody @Valid LeaveCreateRequest req) {
        UserLeave leave = leaveService.createLeave(req);
        return LeaveResponse.from(leave);
    }

    /**
     * 회사가 휴가 승인
     */
    @Operation(
            summary = "휴가 승인",
            description = """
                회사 계정이 특정 휴가 신청을 승인합니다.
                
                 주의
                - 이미 APPROVED / REJECTED / CANCELLED 상태인 경우 400 처리할 수 있습니다.
                - 없는 leaveId인 경우 404 반환.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "승인 성공",
                    content = @Content(
                            schema = @Schema(implementation = LeaveResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "휴가 ID를 찾을 수 없는 경우"
            )
    })
    @PatchMapping("/{leaveId}/approve")
    public LeaveResponse approve(
            @Parameter(description = "승인할 휴가 ID", example = "10")
            @PathVariable Long leaveId
    ) {
        UserLeave leave = leaveService.approve(leaveId);
        return LeaveResponse.from(leave);
    }

    /**
     * 회사 기준 직원 휴가 목록 조회
     */
    @Operation(
            summary = "직원 휴가 목록 조회",
            description = """
                회사 계정이 특정 직원의 전체 휴가 목록을 조회합니다.
                
                 Query Parameter
                - companyId         : 회사 account_id
                - employeeAccountId : 직원 account_id
                
                프론트에서는 이 API로 특정 직원의 전체 휴가 히스토리를 보여줄 수 있습니다.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = LeaveResponse.class)
                    )
            )
    })
    @GetMapping
    public List<LeaveResponse> list(
            @Parameter(description = "회사 계정 ID", required = true, example = "8")
            @RequestParam Long companyId,

            @Parameter(description = "직원 계정 ID", required = true, example = "1")
            @RequestParam Long employeeAccountId
    ) {
        return leaveService.getLeaves(companyId, employeeAccountId)
                .stream()
                .map(LeaveResponse::from)
                .toList();
    }
}

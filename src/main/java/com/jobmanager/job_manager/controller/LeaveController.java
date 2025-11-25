package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.attendance.LeaveCreateRequest;
import com.jobmanager.job_manager.dto.attendance.LeaveResponse;
import com.jobmanager.job_manager.entity.UserLeave;
import com.jobmanager.job_manager.service.LeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Leave", description = "휴가 관리 API")
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
            description =
                    "회사 계정이 특정 직원의 휴가(연차/반차/병가 등)를 등록합니다.\n\n" +
                            "📌 Request Body 설명\n" +
                            "- employeeAccountId : 휴가를 사용하는 직원의 account_id\n" +
                            "- companyAccountId  : 회사의 account_id\n" +
                            "- startDate         : 휴가 시작일 (예: 2025-11-24)\n" +
                            "- endDate           : 휴가 종료일 (예: 2025-11-24)\n" +
                            "- leaveType         : 휴가 유형 (예: FULL, HALF_AM, HALF_PM 등 프로젝트에서 정의한 값)\n" +
                            "- reason            : 휴가 사유 (예: \"병원 진료\")\n\n" +
                            "예시 JSON\n" +
                            "{\n" +
                            "  \"employeeAccountId\": 1,\n" +
                            "  \"companyAccountId\": 8,\n" +
                            "  \"startDate\": \"2025-11-24\",\n" +
                            "  \"endDate\": \"2025-11-24\",\n" +
                            "  \"leaveType\": \"FULL\",\n" +
                            "  \"reason\": \"병원 진료\"\n" +
                            "}"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "휴가 등록 성공",
                    content = @Content(schema = @Schema(implementation = LeaveResponse.class)))
    })
    @PostMapping
    public LeaveResponse create(@RequestBody @Valid LeaveCreateRequest req) {
        UserLeave leave = leaveService.createLeave(req);
        return LeaveResponse.from(leave);
    }

    /**
     * 회사가 휴가 승인
     */
    @Operation(summary = "휴가 승인", description = "회사 계정이 특정 휴가 신청을 승인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "승인 성공",
                    content = @Content(schema = @Schema(implementation = LeaveResponse.class))),
            @ApiResponse(responseCode = "404", description = "휴가 ID를 찾을 수 없는 경우")
    })
    @PatchMapping("/{leaveId}/approve")
    public LeaveResponse approve(
            @Parameter(description = "승인할 휴가 ID") @PathVariable Long leaveId
    ) {
        UserLeave leave = leaveService.approve(leaveId);
        return LeaveResponse.from(leave);
    }

    /**
     * 회사 기준 직원 휴가 목록 조회
     */
    @Operation(
            summary = "직원 휴가 목록 조회",
            description =
                    "회사 계정이 특정 직원의 전체 휴가 목록을 조회합니다.\n\n" +
                            "📌 Query Parameter 설명\n" +
                            "- companyId         : 회사 account_id\n" +
                            "- employeeAccountId : 직원 account_id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = LeaveResponse.class)))
    })
    @GetMapping
    public List<LeaveResponse> list(
            @Parameter(description = "회사 계정 ID", required = true)
            @RequestParam Long companyId,

            @Parameter(description = "직원 계정 ID", required = true)
            @RequestParam Long employeeAccountId
    ) {
        return leaveService.getLeaves(companyId, employeeAccountId)
                .stream()
                .map(LeaveResponse::from)
                .toList();
    }
}

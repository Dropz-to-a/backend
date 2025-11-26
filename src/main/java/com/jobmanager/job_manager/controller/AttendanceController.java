package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.attendance.AttendanceRecordRequest;
import com.jobmanager.job_manager.dto.attendance.AttendanceRecordResponse;
import com.jobmanager.job_manager.entity.UserAttendance;
import com.jobmanager.job_manager.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(
        name = "Attendance",
        description = """
            출근/퇴근 기록을 관리하는 API입니다.
            - 회사 계정이 기준이며, 직원의 출근/퇴근을 대신 찍어주는 시나리오를 가정합니다.
            - 하루에 출근(Clock-in) 1회, 퇴근(Clock-out) 1회만 허용하는 구조를 기본으로 합니다.
            """
)
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * 회사가 직원 출근 찍기
     */
    @Operation(
            summary = "출근 기록 생성 (Clock-in)",
            description = """
                회사 계정이 특정 직원의 '출근'을 기록합니다.
                
                 사용 시나리오
                - 회사 관리자가 직원이 현장에 도착했을 때 직접 출근을 찍어주는 경우
                - 또는 사내 키오스크/관리 화면에서 호출하는 경우
                
                 제약사항
                - 이미 해당 직원이 오늘 출근(Clock-in) 기록이 있는 경우 400 에러를 반환합니다.
                - 회사/직원 매핑이 안 맞는 경우(다른 회사 직원)도 400 또는 403 처리 대상입니다(서비스/시큐리티단에서 검증).
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "출근 기록 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AttendanceRecordResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 예시",
                                    value = """
                                        {
                                          "attendanceId": 1001,
                                          "employeeAccountId": 1,
                                          "companyAccountId": 8,
                                          "checkType": "IN",
                                          "checkedAt": "2025-11-26T09:01:23",
                                          "status": "SUCCESS"
                                        }
                                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 출근이 찍힌 경우 또는 잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "이미 출근 처리된 경우",
                                    value = """
                                        {
                                          "code": "ATTENDANCE_ALREADY_CLOCKED_IN",
                                          "message": "이미 오늘 출근이 기록되어 있습니다."
                                        }
                                        """
                            )
                    )
            )
    })
    @PostMapping("/clock-in")
    public AttendanceRecordResponse clockIn(@RequestBody @Valid AttendanceRecordRequest req) {
        UserAttendance result = attendanceService.clockIn(req);
        return AttendanceRecordResponse.from(result);
    }

    /**
     * 회사가 직원 퇴근 찍기
     */
    @Operation(
            summary = "퇴근 기록 생성 (Clock-out)",
            description = """
                회사 계정이 특정 직원의 '퇴근'을 기록합니다.
                
                 사용 시나리오
                - 출근(Clock-in)을 먼저 찍은 후 같은 근무일에 퇴근을 찍는 흐름입니다.
                
                 제약사항
                - 출근 기록이 없는 상태에서 퇴근을 찍으려고 하면 400 에러를 반환합니다.
                - 이미 퇴근 기록이 있는 경우에도 400 에러를 반환합니다.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "퇴근 기록 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AttendanceRecordResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 예시",
                                    value = """
                                        {
                                          "attendanceId": 1001,
                                          "employeeAccountId": 1,
                                          "companyAccountId": 8,
                                          "checkType": "OUT",
                                          "checkedAt": "2025-11-26T18:01:23",
                                          "status": "SUCCESS"
                                        }
                                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "출근 기록 없음 / 이미 퇴근 처리됨",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "출근 기록 없음 예시",
                                    value = """
                                        {
                                          "code": "ATTENDANCE_NO_CLOCK_IN",
                                          "message": "먼저 출근 기록이 필요합니다."
                                        }
                                        """
                            )
                    )
            )
    })
    @PostMapping("/clock-out")
    public AttendanceRecordResponse clockOut(@RequestBody @Valid AttendanceRecordRequest req) {
        UserAttendance result = attendanceService.clockOut(req);
        return AttendanceRecordResponse.from(result);
    }

    /**
     * 근태 기록 조회 (회사 기준 / 직원 옵션 / 기간 조회)
     */
    @Operation(
            summary = "근태 기록 조회",
            description = """
                회사 기준 출근/퇴근 기록을 조회합니다.
                
                파라미터:
                - companyId: 회사 계정 ID (필수)
                - employeeId: 특정 직원 ID (선택)
                - fromDate, toDate: 조회 기간 (yyyy-MM-dd)
                
                employeeId 를 입력하지 않으면 회사 전체 직원 기록을 조회합니다.
                """
    )
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject("""
                    [
                      {
                        "attendanceId": 1001,
                        "employeeAccountId": 1,
                        "companyAccountId": 8,
                        "checkType": "IN",
                        "checkedAt": "2025-11-26T09:01:23",
                        "status": "SUCCESS"
                      },
                      {
                        "attendanceId": 1002,
                        "employeeAccountId": 1,
                        "companyAccountId": 8,
                        "checkType": "OUT",
                        "checkedAt": "2025-11-26T18:02:10",
                        "status": "SUCCESS"
                      }
                    ]
                    """)
            )
    )
    @GetMapping("/history")
    public List<AttendanceRecordResponse> getHistory(
            @RequestParam Long companyId,
            @RequestParam(required = false) Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        List<UserAttendance> list = attendanceService.getHistory(
                companyId, employeeId, fromDate, toDate
        );

        return list.stream()
                .map(AttendanceRecordResponse::from)
                .toList();
    }
}

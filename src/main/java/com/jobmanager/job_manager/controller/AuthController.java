package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.auth.AuthResponse;
import com.jobmanager.job_manager.dto.auth.LoginRequest;
import com.jobmanager.job_manager.dto.auth.RegisterRequest;
import com.jobmanager.job_manager.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Auth",
        description = """
            회원가입 / 로그인 관련 API입니다.
            - JWT 기반 인증을 사용합니다.
            - 로그인 시 Access Token을 발급합니다.
            - ROLE_USER / ROLE_COMPANY / ROLE_ADMIN 권한을 사용합니다.
            """
)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ============================================================
    // 회원가입(Register)
    // ============================================================
    @Operation(
            summary = "회원가입",
            description = """
                새로운 계정을 생성합니다.

                roleCode 처리 방식:
                - 문자열: "ROLE_USER", "ROLE_COMPANY", "ROLE_ADMIN"
                - 숫자: 1=ROLE_USER, 2=ROLE_COMPANY, 3=ROLE_ADMIN

                필드 설명:
                - username : 로그인 ID
                - email : 이메일 (선택)
                - password : 비밀번호
                - phone : 전화번호
                - roleCode : 권한 코드
                """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "회원가입 요청 데이터",
                    content = @Content(
                            schema = @Schema(implementation = RegisterRequest.class),
                            examples = @ExampleObject(
                                    name = "회사 계정 회원가입 예시",
                                    value = """
                                    {
                                      "username": "dropz_company",
                                      "email": "hr@dropz.co.kr",
                                      "password": "1234",
                                      "phone": "01012341234",
                                      "roleCode": "ROLE_COMPANY"
                                    }
                                    """
                            )
                    )
            )
    )
    @ApiResponse(
            responseCode = "200",
            description = "회원가입 성공",
            content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject(value = "OK")
            )
    )
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest req) {
        authService.register(req);
        return "OK";
    }


    // ============================================================
    // 로그인(Login)
    // ============================================================
    @Operation(
            summary = "로그인",
            description = """
                username 또는 email + password 로 로그인합니다.
                JSON 형식으로 요청해야 합니다.

                요청 형식(JSON):
                {
                  "id": "dropz_user",
                  "password": "1234"
                }

                응답:
                - accessToken : Bearer 인증에 사용
                """
    )
    @ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AuthResponse.class),
                    examples = @ExampleObject(
                            name = "성공 예시",
                            value = """
                            {
                              "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                            }
                            """
                    )
            )
    )
    @PostMapping("/login")
    public AuthResponse login(
            @Parameter(description = "로그인 요청 JSON", required = true)
            @RequestBody LoginRequest req
    ) {
        String token = authService.login(req.getId(), req.getPassword());
        return new AuthResponse(token);
    }
}

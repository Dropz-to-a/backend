package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.auth.AuthResponse;
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
            - JWT 기반 인증을 사용하며, 로그인 시 Access Token을 발급합니다.
            - ROLE_USER, ROLE_COMPANY, ROLE_ADMIN 세 가지 권한을 사용합니다.
            """
)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "회원가입",
            description = """
                새로운 계정을 생성합니다.
                
                 roleCode 처리 방식
                - 문자열: "ROLE_USER", "ROLE_COMPANY", "ROLE_ADMIN"
                - 숫자: 1=ROLE_USER, 2=ROLE_COMPANY, 3=ROLE_ADMIN
                
                 비즈니스 규칙 (예시)
                - username은 유니크해야 합니다.
                - email이 존재하는 경우에도 유니크해야 합니다.
                - 비밀번호는 서비스 단에서 Bcrypt 등으로 해시 저장됩니다.
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
                                          "password": "Passw0rd!",
                                          "roleCode": "ROLE_COMPANY"
                                        }
                                        """
                            )
                    )
            )
    )
    @ApiResponse(
            responseCode = "200",
            description = "회원가입 성공(단순 OK 문자열 반환)",
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

    @Operation(
            summary = "로그인",
            description = """
                username 또는 email + password로 로그인하고 JWT Access Token을 발급합니다.
                
                 요청 파라미터
                - id: username 또는 email (둘 중 하나)
                - password: 원문 비밀번호 (서버에서 해시 비교)
                
                 응답
                - accessToken: 이후 Authorization: Bearer {token} 헤더로 사용
                """)
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
            @Parameter(description = "username 또는 email", example = "dropz_user")
            @RequestParam String id,
            @Parameter(description = "로그인 비밀번호", example = "Passw0rd!")
            @RequestParam String password
    ) {
        String token = authService.login(id, password);
        return new AuthResponse(token);
    }
}
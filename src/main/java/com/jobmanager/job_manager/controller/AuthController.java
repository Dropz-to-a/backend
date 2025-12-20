package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.auth.AuthResponse;
import com.jobmanager.job_manager.dto.auth.LoginRequest;
import com.jobmanager.job_manager.dto.auth.MeResponse;
import com.jobmanager.job_manager.dto.auth.RegisterRequest;
import com.jobmanager.job_manager.global.jwt.JwtHeaderUtils;
import com.jobmanager.job_manager.global.jwt.JwtTokenProvider;
import com.jobmanager.job_manager.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Auth",
        description = """
            회원가입 / 로그인 / 내 정보 조회 API입니다.
            - JWT 기반 인증을 사용합니다.
            - ROLE_USER / ROLE_COMPANY / ROLE_ADMIN 권한을 사용합니다.
            """
)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwt;

    // ============================================================
    // 회원가입
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
                    content = @Content(
                            schema = @Schema(implementation = RegisterRequest.class),
                            examples = @ExampleObject(
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
    @ApiResponse(responseCode = "200", description = "회원가입 성공")
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest req) {
        authService.register(req);
        return "OK";
    }

    // ============================================================
    // 로그인 (JSON)
    // ============================================================
    @Operation(
            summary = "로그인",
            description = """
                username 또는 email + password로 로그인합니다.
                반드시 JSON 형식으로 요청해야 합니다.

                요청 예시:
                {
                  "id": "alvin",
                  "password": "1234"
                }
                
                JWT Token에 실려 보내지는 값
                - 유저 이름
                - 계정 타입(user, company)
                - 계정 역할(ROLE_USER, ROLE_COMPANY)
                - onboard 여부(true, false)
                - 소속된 회사 이름(소속되어있지 않을 경우 출력 X, User만 출력됨)
                """
    )
    @ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(
                    schema = @Schema(implementation = AuthResponse.class),
                    examples = @ExampleObject(
                            value = """
                            {
                              "accessToken": "eyJhbGciOiJIUzI1NiJ9..."
                            }
                            """
                    )
            )
    )
    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody LoginRequest req,
            HttpServletResponse response
    ) {
        AuthService.LoginResult result =
                authService.loginWithRefresh(req.getId(), req.getPassword());

        response.addHeader(
                "Set-Cookie",
                "refreshToken=" + result.refreshToken()
                        + "; HttpOnly; Path=/api/auth/refresh; Max-Age=86400"
        );

        return new AuthResponse(result.accessToken());
    }

    // ============================================================
    // 토큰 재발급
    // ============================================================
    @Operation(
            summary = "Access Token 재발급",
            description = """
                만료된 Access Token을 재발급합니다.

                🔐 인증 방식
                - refreshToken은 **HttpOnly Cookie**로 전달됩니다.
                - 클라이언트는 refreshToken 값을 직접 전송하지 않습니다.
                - 브라우저는 자동으로 Cookie를 포함하여 요청합니다.

                ⚠️ 주의사항
                - refreshToken이 만료되었거나 폐기(revoked)된 경우 재발급에 실패합니다.
                - 이 경우 다시 로그인해야 합니다.

                📌 요청 Body는 필요하지 않습니다.
                """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Access Token 재발급 성공",
            content = @Content(
                    schema = @Schema(implementation = AuthResponse.class),
                    examples = @ExampleObject(
                            value = """
                            {
                              "accessToken": "eyJhbGciOiJIUzI1NiJ9..."
                            }
                            """
                    )
            )
    )
    @PostMapping("/refresh")
    public AuthResponse refresh(
            @CookieValue("refreshToken") String refreshToken
    ) {
        String newAccessToken = authService.refresh(refreshToken);
        return new AuthResponse(newAccessToken);
    }

    // ============================================================
    // 내 계정 정보 조회 (역할 판단용)
    // ============================================================
    @Operation(
            summary = "내 계정 정보 조회",
            description = "현재 로그인한 사용자의 accountId, username, accountType, role 정보를 반환합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                    schema = @Schema(implementation = MeResponse.class)
            )
    )
    @GetMapping("/me")
    public MeResponse me() {

        String token = JwtHeaderUtils.getTokenFromHeader();

        return new MeResponse(
                jwt.getAccountId(token),
                jwt.getUsername(token),
                jwt.getAccountType(token),
                jwt.getRole(token)
        );
    }
}

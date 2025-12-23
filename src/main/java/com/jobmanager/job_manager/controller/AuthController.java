package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.auth.*;
import com.jobmanager.job_manager.global.jwt.JwtHeaderUtils;
import com.jobmanager.job_manager.global.jwt.JwtTokenProvider;
import com.jobmanager.job_manager.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Auth",
        description = """
            회원가입 / 로그인 / 토큰 재발급 / 내 정보 조회 API
            - JWT 기반 인증
            - AccessToken: 30분
            - RefreshToken: 1일 (HttpOnly Cookie)
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
                """
    )
    @ApiResponse(responseCode = "200", description = "회원가입 성공")
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest req) {
        authService.register(req);
        return "OK";
    }

    // ============================================================
    // 로그인
    // ============================================================
    @Operation(
            summary = "로그인",
            description = """
                username 또는 email + password로 로그인합니다.

                응답:
                - accessToken : JWT (30분)
                - refreshToken : HttpOnly Cookie (1일)
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
                authService.login(req.getId(), req.getPassword());

        Cookie refreshCookie = new Cookie("refreshToken", result.refreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/api/auth");
        refreshCookie.setMaxAge(60 * 60 * 24);

        response.addCookie(refreshCookie);

        return new AuthResponse(result.accessToken());
    }


    @PostMapping("/refresh")
    @Operation(
            summary = "Access Token 재발급",
            description = """
            HttpOnly Cookie에 저장된 RefreshToken을 사용하여
            AccessToken을 재발급합니다.

            - Request Body 없음
            - refreshToken은 Cookie에서 자동 추출
            """
    )
    public AuthResponse refresh() {
        return new AuthResponse(authService.refreshFromCookie());
    }


    // ============================================================
    // 내 계정 정보 조회
    // ============================================================
    @Operation(
            summary = "내 계정 정보 조회",
            description = """
                현재 로그인한 사용자의 정보를 반환합니다.
                - accountId
                - username
                - accountType
                - role
                """
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

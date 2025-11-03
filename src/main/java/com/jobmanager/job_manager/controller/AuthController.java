// src/main/java/com/jobmanager/job_manager/controller/AuthController.java
package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.auth.AuthResponse;
import com.jobmanager.job_manager.dto.auth.RegisterRequest;
import com.jobmanager.job_manager.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "회원가입/로그인")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "회원가입",
            description = "roleCode는 문자열 권장(예: ROLE_COMPANY). 숫자도 허용: 1=ROLE_USER, 2=ROLE_COMPANY, 3=ROLE_ADMIN"
    )
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest req) {
        authService.register(req);
        return "OK";
    }

    @Operation(summary = "로그인", description = "id는 username 또는 email")
    @PostMapping("/login")
    public AuthResponse login(@RequestParam String id, @RequestParam String password) {
        String token = authService.login(id, password);
        return new AuthResponse(token);
    }
}

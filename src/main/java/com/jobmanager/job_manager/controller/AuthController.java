package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.auth.AuthResponse;
import com.jobmanager.job_manager.dto.auth.RegisterRequest;
import com.jobmanager.job_manager.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest req) {
        authService.register(req);
        return "OK";
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestParam String id, @RequestParam String password) {
        String token = authService.login(id, password);
        return new AuthResponse(token);
    }
}

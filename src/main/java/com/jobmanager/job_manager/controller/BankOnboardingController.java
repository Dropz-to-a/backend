package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.bankonboarding.*;
import com.jobmanager.job_manager.service.BankOnboardingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Bank Onboarding", description = "1원 인증 API")
@RestController
@RequestMapping("/api/bank")
@RequiredArgsConstructor
public class BankOnboardingController {

    private final BankOnboardingService bankOnboardingService;

    @Operation(
            summary = "1원 인증 요청",
            description = "입력한 계좌로 1원을 송금하여 인증번호를 발급합니다."
    )
    @PostMapping("/onewon/request")
    public ResponseEntity<?> request(@RequestBody OneWonRequest request) {
        return ResponseEntity.ok(
                bankOnboardingService.requestOneWon(request)
        );
    }

    @Operation(
            summary = "1원 인증 확인",
            description = "입금자명에 표시된 인증번호를 검증합니다."
    )
    @PostMapping("/onewon/verify")
    public ResponseEntity<?> verify(@RequestBody OneWonVerifyRequest request) {
        return ResponseEntity.ok(
                bankOnboardingService.verifyOneWon(request)
        );
    }
}

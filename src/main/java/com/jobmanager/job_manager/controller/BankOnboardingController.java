package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.bankonboarding.*;
import com.jobmanager.job_manager.service.BankOnboardingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Bank Onboarding",
        description = "1원 인증을 통한 본인 계좌 확인 API"
)
@RestController
@RequestMapping("/api/bank")
@RequiredArgsConstructor
public class BankOnboardingController {

    private final BankOnboardingService bankOnboardingService;

    @Operation(
            summary = "1원 인증 요청",
            description = """
                입력한 계좌로 1원을 송금하여
                본인 계좌 여부를 확인합니다.

                - 실제로는 입금자명(인증번호)이 함께 전송됩니다.
                - 이후 인증번호 확인 API를 호출해야 합니다.
                """
    )
    @PostMapping("/onewon/request")
    public void request(@RequestBody OneWonRequest request) {
        bankOnboardingService.requestOneWon(request);
    }

    @Operation(
            summary = "1원 인증 확인",
            description = """
                1원 입금 내역의 인증번호를 검증합니다.

                - 사용자가 입력한 인증번호가 일치하면 인증 성공
                - 인증 성공/실패 여부는 APICK 응답 기준
                """
    )
    @PostMapping("/onewon/verify")
    public void verify(@RequestBody OneWonVerifyRequest request) {
        bankOnboardingService.verifyOneWon(request);
    }
}

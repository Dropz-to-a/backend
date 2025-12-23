package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.bankonboarding.*;
import com.jobmanager.job_manager.service.client.ApickClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 1원 인증 처리 서비스
 */
@Service
@RequiredArgsConstructor
public class BankOnboardingService {

    private final ApickClient apickClient;

    // 1원 송금 요청
    public void requestOneWon(OneWonRequest req) {
        apickClient.sendOneWon(
                req.bankCode(),
                req.accountNumber(),
                req.holderName()
        );
    }

    // 인증번호 검증
    public void verifyOneWon(OneWonVerifyRequest req) {
        apickClient.verifyOneWon(
                req.bankCode(),
                req.accountNumber(),
                req.authCode()
        );
    }
}

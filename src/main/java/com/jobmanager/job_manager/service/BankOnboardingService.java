package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.bankonboarding.*;
import com.jobmanager.job_manager.service.client.ApickClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class BankOnboardingService {

    private final ApickClient apickClient;

    public Map<String, Object> requestOneWon(OneWonRequest req) {
        return apickClient.sendOneWon(
                req.bankCode(),
                req.accountNumber(),
                req.holderName()
        );
    }

    public Map<String, Object> verifyOneWon(OneWonVerifyRequest req) {
        return apickClient.verifyOneWon(
                req.bankCode(),
                req.accountNumber(),
                req.authCode()
        );
    }
}

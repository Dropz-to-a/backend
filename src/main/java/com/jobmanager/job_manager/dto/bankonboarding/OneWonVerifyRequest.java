package com.jobmanager.job_manager.dto.bankonboarding;

/**
 * 1원 인증 확인 DTO
 */
public record OneWonVerifyRequest(
        String bankCode,
        String accountNumber,
        String authCode         // 입금자명 인증번호
) {}

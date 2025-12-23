package com.jobmanager.job_manager.dto.bankonboarding;

/**
 * 1원 인증 요청 DTO
 */
public record OneWonRequest(
        String bankCode,        // 은행 코드
        String accountNumber,   // 계좌 번호
        String holderName       // 예금주명
) {}

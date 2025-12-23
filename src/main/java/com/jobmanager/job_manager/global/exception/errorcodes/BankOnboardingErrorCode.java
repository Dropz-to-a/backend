package com.jobmanager.job_manager.global.exception.errorcodes;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BankOnboardingErrorCode {

    USER_NOT_EMPLOYED(
            HttpStatus.FORBIDDEN,
            "해당 계정은 재직자가 아닙니다."
    ),

    USER_NOT_ONBOARDED(
            HttpStatus.BAD_REQUEST,
            "유저 기본 온보딩이 필요합니다."
    ),

    COMPANY_NOT_ONBOARDED(
            HttpStatus.BAD_REQUEST,
            "회사 기본 온보딩이 필요합니다."
    ),

    INVALID_ACCOUNT_TYPE(
            HttpStatus.BAD_REQUEST,
            "유효하지 않은 계정 타입입니다."
    );

    private final HttpStatus status;
    private final String message;

    BankOnboardingErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
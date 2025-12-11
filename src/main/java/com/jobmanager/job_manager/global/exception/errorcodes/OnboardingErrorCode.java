package com.jobmanager.job_manager.global.exception.errorcodes;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OnboardingErrorCode {

    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "온보딩 대상 계정을 찾을 수 없습니다."),
    INVALID_ACCOUNT_TYPE(HttpStatus.FORBIDDEN, "해당 계정 타입에서는 온보딩을 수행할 수 없습니다."),
    INVALID_BIRTH_FORMAT(HttpStatus.BAD_REQUEST, "생년월일 형식이 잘못되었습니다. (yyyy-MM-dd)"),
    USER_ALREADY_ONBOARDED(HttpStatus.CONFLICT, "이미 유저 온보딩을 완료했습니다."),
    COMPANY_ALREADY_ONBOARDED(HttpStatus.CONFLICT, "이미 회사 온보딩을 완료했습니다."),
    REQUIRED_FIELD_MISSING(HttpStatus.BAD_REQUEST, "필수 온보딩 입력값이 누락되었습니다.");

    private final HttpStatus status;
    private final String message;

    OnboardingErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
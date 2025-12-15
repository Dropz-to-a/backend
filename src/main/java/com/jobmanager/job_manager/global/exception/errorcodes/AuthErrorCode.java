package com.jobmanager.job_manager.global.exception.errorcodes;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorCode {

    // ==========================
    // 공통 오류
    // ==========================
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),

    // ==========================
    // Auth / Account
    // ==========================
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 username 입니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 email 입니다."),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 계정입니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 역할입니다."),

    // ==========================
    // Onboarding 관련 오류
    // ==========================
    INVALID_ACCOUNT_TYPE(HttpStatus.FORBIDDEN, "해당 계정 타입으로는 온보딩을 수행할 수 없습니다."),
    USER_ONBOARDING_ALREADY_DONE(HttpStatus.CONFLICT, "이미 유저 온보딩을 완료했습니다."),
    COMPANY_ONBOARDING_ALREADY_DONE(HttpStatus.CONFLICT, "이미 회사 온보딩을 완료했습니다."),
    INVALID_BIRTH_FORMAT(HttpStatus.BAD_REQUEST, "생년월일 형식이 올바르지 않습니다. (yyyy-MM-dd)"),

    REQUIRED_FIELD_MISSING(HttpStatus.BAD_REQUEST, "필수 입력값이 누락되었습니다.");

    private final HttpStatus status;
    private final String message;

    AuthErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
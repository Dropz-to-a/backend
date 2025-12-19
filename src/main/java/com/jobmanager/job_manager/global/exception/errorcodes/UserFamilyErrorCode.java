package com.jobmanager.job_manager.global.exception.errorcodes;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserFamilyErrorCode {

    // =========================
    // 권한 / 인증
    // =========================
    FAMILY_ACCESS_FORBIDDEN(
            HttpStatus.FORBIDDEN,
            "USER 계정만 가족 정보에 접근할 수 있습니다."
    ),

    // =========================
    // 상태
    // =========================
    FAMILY_NOT_ONBOARDED(
            HttpStatus.BAD_REQUEST,
            "온보딩이 완료되지 않은 계정입니다."
    ),

    // =========================
    // 중복 / 존재
    // =========================
    FAMILY_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "이미 등록된 가족 정보입니다."
    ),

    FAMILY_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "존재하지 않는 가족 정보입니다."
    ),

    // =========================
    // 요청 유효성
    // =========================
    FAMILY_INVALID_VALUE(
            HttpStatus.BAD_REQUEST,
            "가족 정보 입력값이 올바르지 않습니다."
    ),

    FAMILY_ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "본인의 가족 정보만 수정하거나 삭제할 수 있습니다."
    ),

    // =========================
    // 정책 (확장 대비)
    // =========================
    FAMILY_LIMIT_EXCEEDED(
            HttpStatus.BAD_REQUEST,
            "등록할 수 있는 가족 정보 개수를 초과했습니다."
    );

    private final HttpStatus status;
    private final String message;

    UserFamilyErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
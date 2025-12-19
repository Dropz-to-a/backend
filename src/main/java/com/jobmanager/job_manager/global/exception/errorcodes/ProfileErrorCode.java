package com.jobmanager.job_manager.global.exception.errorcodes;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ProfileErrorCode {

    PROFILE_ACCESS_FORBIDDEN(
            HttpStatus.FORBIDDEN,
            "USER 계정만 프로필에 접근할 수 있습니다."
    ),

    PROFILE_NOT_ONBOARDED(
            HttpStatus.BAD_REQUEST,
            "온보딩이 완료되지 않은 계정입니다."
    ),

    PROFILE_UPDATE_EMPTY(
            HttpStatus.BAD_REQUEST,
            "수정할 프로필 정보가 없습니다."
    ),

    PROFILE_FIELD_IMMUTABLE(
            HttpStatus.BAD_REQUEST,
            "수정할 수 없는 필드가 포함되어 있습니다."
    ),

    PROFILE_INVALID_VALUE(
            HttpStatus.BAD_REQUEST,
            "프로필 입력값이 올바르지 않습니다."
    );

    private final HttpStatus status;
    private final String message;

    ProfileErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
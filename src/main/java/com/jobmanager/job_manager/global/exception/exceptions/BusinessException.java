package com.jobmanager.job_manager.global.exception.exceptions;

import com.jobmanager.job_manager.global.exception.errorcodes.AuthErrorCode;

public class BusinessException extends RuntimeException {

    private final AuthErrorCode errorCode;

    public BusinessException(AuthErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AuthErrorCode getErrorCode() {
        return errorCode;
    }
}
package com.jobmanager.job_manager.global.exception.exceptions;

import com.jobmanager.job_manager.global.exception.errorcodes.CompanyErrorCode;

public class CompanyException extends RuntimeException {

    private final CompanyErrorCode errorCode;

    public CompanyException(CompanyErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CompanyErrorCode getErrorCode() {
        return errorCode;
    }
}
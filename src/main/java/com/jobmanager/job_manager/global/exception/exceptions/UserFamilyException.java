package com.jobmanager.job_manager.global.exception.exceptions;

import com.jobmanager.job_manager.global.exception.errorcodes.UserFamilyErrorCode;
import lombok.Getter;

@Getter
public class UserFamilyException extends RuntimeException {

    private final UserFamilyErrorCode errorCode;

    public UserFamilyException(UserFamilyErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
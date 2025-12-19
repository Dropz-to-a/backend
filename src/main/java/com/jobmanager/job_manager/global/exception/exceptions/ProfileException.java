package com.jobmanager.job_manager.global.exception.exceptions;

import com.jobmanager.job_manager.global.exception.errorcodes.ProfileErrorCode;
import lombok.Getter;

@Getter
public class ProfileException extends RuntimeException {

    private final ProfileErrorCode errorCode;

    public ProfileException(ProfileErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
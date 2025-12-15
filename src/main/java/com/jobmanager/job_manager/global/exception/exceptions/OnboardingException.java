package com.jobmanager.job_manager.global.exception.exceptions;

import com.jobmanager.job_manager.global.exception.errorcodes.OnboardingErrorCode;
import lombok.Getter;

@Getter
public class OnboardingException extends RuntimeException {

    private final OnboardingErrorCode errorCode;

    public OnboardingException(OnboardingErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
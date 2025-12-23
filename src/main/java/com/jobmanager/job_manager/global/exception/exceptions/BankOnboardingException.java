package com.jobmanager.job_manager.global.exception.exceptions;

import com.jobmanager.job_manager.global.exception.errorcodes.BankOnboardingErrorCode;
import lombok.Getter;

@Getter
public class BankOnboardingException extends RuntimeException {

    private final BankOnboardingErrorCode errorCode;

    public BankOnboardingException(BankOnboardingErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
package com.jobmanager.job_manager.global.exception.errorcodes;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CompanyErrorCode {

    COMPANY_NOT_ONBOARDED(
            HttpStatus.BAD_REQUEST,
            "회사 온보딩이 완료되지 않았습니다."
    ),

    NOT_COMPANY_ACCOUNT(
            HttpStatus.FORBIDDEN,
            "회사 계정만 접근할 수 있습니다."
    ),

    EMPLOYEE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "존재하지 않는 사용자입니다."
    ),

    INVALID_EMPLOYEE_ACCOUNT(
            HttpStatus.BAD_REQUEST,
            "USER 계정만 직원으로 등록할 수 있습니다."
    ),

    ALREADY_ASSIGNED_TO_TEAM(
            HttpStatus.CONFLICT,
            "이미 부서가 지정된 직원입니다."
    ),

    NOT_ASSIGNED_TO_TEAM(
            HttpStatus.BAD_REQUEST,
            "아직 부서가 지정되지 않은 직원입니다."
    ),

    TEAM_NOT_FOUND(
            HttpStatus.BAD_REQUEST,
            "존재하지 않는 부서입니다."
    ),

    ALREADY_IN_THIS_TEAM(
            HttpStatus.CONFLICT,
            "이미 해당 부서에 소속된 직원입니다."
    ),

    EMPLOYEE_ALREADY_ASSIGNED(
            HttpStatus.CONFLICT,
            "이미 다른 회사에 소속된 사용자입니다."
    ),

    ALREADY_EMPLOYEE_OF_COMPANY(
            HttpStatus.CONFLICT,
            "이미 해당 회사의 직원입니다."
    ),

    EMPLOYEE_NOT_IN_COMPANY(
            HttpStatus.BAD_REQUEST,
            "해당 회사 소속 직원이 아닙니다."
    ),

    TEAM_NOT_IN_COMPANY(
            HttpStatus.BAD_REQUEST,
            "해당 회사의 부서가 아닙니다."
    );

    private final HttpStatus status;
    private final String message;

    CompanyErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
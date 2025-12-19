package com.jobmanager.job_manager.global.exception;

import com.jobmanager.job_manager.global.exception.errorcodes.AuthErrorCode;
import com.jobmanager.job_manager.global.exception.errorcodes.CompanyErrorCode;
import com.jobmanager.job_manager.global.exception.errorcodes.OnboardingErrorCode;
import com.jobmanager.job_manager.global.exception.exceptions.BusinessException;
import com.jobmanager.job_manager.global.exception.exceptions.CompanyException;
import com.jobmanager.job_manager.global.exception.exceptions.OnboardingException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import com.jobmanager.job_manager.global.exception.errorcodes.ProfileErrorCode;
import com.jobmanager.job_manager.global.exception.exceptions.ProfileException;
import com.jobmanager.job_manager.global.exception.errorcodes.UserFamilyErrorCode;
import com.jobmanager.job_manager.global.exception.exceptions.UserFamilyException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ErrorResponse(
            int status,
            String error,
            String message,
            String path,
            LocalDateTime timestamp
    ) {}

    private ErrorResponse build(HttpStatus status, String message, HttpServletRequest req) {
        return new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                req.getRequestURI(),
                LocalDateTime.now()
        );
    }

    // ========================================================================
    // 0) BusinessException (Auth 전용)
    // ========================================================================
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException e, HttpServletRequest req
    ) {
        AuthErrorCode code = e.getErrorCode();

        ErrorResponse body = new ErrorResponse(
                code.getStatus().value(),
                code.getStatus().getReasonPhrase(),
                code.getMessage(),
                req.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(code.getStatus()).body(body);
    }

    // ========================================================================
    // 0-1) CompanyException (Company / Employee 도메인 전용)
    // ========================================================================
    @ExceptionHandler(CompanyException.class)
    public ResponseEntity<ErrorResponse> handleCompanyException(
            CompanyException e, HttpServletRequest req
    ) {
        CompanyErrorCode code = e.getErrorCode();

        ErrorResponse body = new ErrorResponse(
                code.getStatus().value(),
                code.getStatus().getReasonPhrase(),
                code.getMessage(),
                req.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(code.getStatus()).body(body);
    }

    // ========================================================================
    // 1) 잘못된 요청 파라미터
    // ========================================================================

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(
            MissingServletRequestParameterException e, HttpServletRequest req
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(build(
                        HttpStatus.BAD_REQUEST,
                        "필수 요청값 '" + e.getParameterName() + "' 이(가) 누락되었습니다.",
                        req
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseError(
            HttpMessageNotReadableException e, HttpServletRequest req
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(build(
                        HttpStatus.BAD_REQUEST,
                        "요청 JSON 형식을 읽을 수 없습니다.",
                        req
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(
            MethodArgumentNotValidException e, HttpServletRequest req
    ) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(err -> "[" + err.getField() + "] " + err.getDefaultMessage())
                .findFirst()
                .orElse("잘못된 요청 형식입니다.");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(build(HttpStatus.BAD_REQUEST, msg, req));
    }

    // ========================================================================
    // 2) 권한 오류
    // ========================================================================
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException e, HttpServletRequest req
    ) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(build(
                        HttpStatus.FORBIDDEN,
                        "이 작업을 수행할 권한이 없습니다.",
                        req
                ));
    }

    // ========================================================================
    // 3) DB 제약조건 위반
    // ========================================================================
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException e, HttpServletRequest req
    ) {
        String msg = "데이터 무결성 제약조건 위반입니다.";

        if (e.getMessage() != null) {
            if (e.getMessage().contains("Duplicate entry")) {
                msg = "이미 존재하는 데이터입니다.";
            } else if (e.getMessage().contains("FOREIGN KEY")) {
                msg = "존재하지 않는 참조로 인해 작업이 실패했습니다.";
            }
        }

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(build(HttpStatus.CONFLICT, msg, req));
    }

    // ========================================================================
    // 4) Content-Type 오류
    // ========================================================================
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaType(
            HttpMediaTypeNotSupportedException e, HttpServletRequest req
    ) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(build(
                        HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                        "지원하지 않는 Content-Type 입니다. JSON 사용을 권장합니다.",
                        req
                ));
    }

    // ========================================================================
    // 5) OnboardingException (Onboarding 전용)
    // ========================================================================
    @ExceptionHandler(OnboardingException.class)
    public ResponseEntity<ErrorResponse> handleOnboardingException(
            OnboardingException e, HttpServletRequest req
    ) {
        OnboardingErrorCode code = e.getErrorCode();

        ErrorResponse body = new ErrorResponse(
                code.getStatus().value(),
                code.getStatus().getReasonPhrase(),
                code.getMessage(),
                req.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(code.getStatus()).body(body);
    }

    // ========================================================================
    // 6) ProfileException (Profile 도메인 전용)
    // ========================================================================
    @ExceptionHandler(ProfileException.class)
    public ResponseEntity<ErrorResponse> handleProfileException(
            ProfileException e, HttpServletRequest req
    ) {
        ProfileErrorCode code = e.getErrorCode();

        ErrorResponse body = new ErrorResponse(
                code.getStatus().value(),
                code.getStatus().getReasonPhrase(),
                code.getMessage(),
                req.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(code.getStatus()).body(body);
    }

    // ========================================================================
    // 7) UserFamilyException (UserFamily 도메인 전용)
    // ========================================================================
    @ExceptionHandler(UserFamilyException.class)
    public ResponseEntity<ErrorResponse> handleUserFamilyException(
            UserFamilyException e, HttpServletRequest req
    ) {
        UserFamilyErrorCode code = e.getErrorCode();

        ErrorResponse body = new ErrorResponse(
                code.getStatus().value(),
                code.getStatus().getReasonPhrase(),
                code.getMessage(),
                req.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(code.getStatus()).body(body);
    }

    // ========================================================================
    // 8) 기타 예상 못한 오류 (진짜 500)
    // ========================================================================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest req) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(build(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "서버 내부 오류가 발생했습니다.",
                        req
                ));
    }
}
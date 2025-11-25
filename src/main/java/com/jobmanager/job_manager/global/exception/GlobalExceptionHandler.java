package com.jobmanager.job_manager.global.exception;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
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

    // ===========================
    // 1) 잘못된 요청 파라미터
    // ===========================

    /** @RequestParam 누락 */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ErrorResponse handleMissingParam(
            MissingServletRequestParameterException e, HttpServletRequest req
    ) {
        return build(HttpStatus.BAD_REQUEST,
                "필수 요청값 '" + e.getParameterName() + "' 이(가) 누락되었습니다.",
                req);
    }

    /** JSON 파싱 실패 */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse handleJsonParseError(
            HttpMessageNotReadableException e, HttpServletRequest req
    ) {
        return build(HttpStatus.BAD_REQUEST,
                "요청 형식(JSON)을 읽을 수 없습니다. 요청 데이터를 다시 확인해주세요.",
                req);
    }

    /** @Valid 검증 오류 */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationError(
            MethodArgumentNotValidException e, HttpServletRequest req
    ) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(err -> "[" + err.getField() + "] " + err.getDefaultMessage())
                .findFirst()
                .orElse("잘못된 요청 형식입니다.");

        return build(HttpStatus.BAD_REQUEST, msg, req);
    }

    // ===========================
    // 2) 권한 & 인증 오류
    // ===========================

    /** JWT는 있지만 권한 없음 */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponse handleAccessDenied(
            AccessDeniedException e, HttpServletRequest req
    ) {
        return build(HttpStatus.FORBIDDEN,
                "이 작업을 수행할 권한이 없습니다.",
                req);
    }

    // ===========================
    // 3) 비즈니스 로직 오류
    // ===========================

    /** 잘못된 입력 또는 비즈니스 룰 위반 */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgument(
            IllegalArgumentException e, HttpServletRequest req
    ) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage(), req);
    }

    // ===========================
    // 4) DB 제약 조건 오류
    // ===========================

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleDataIntegrity(
            DataIntegrityViolationException e, HttpServletRequest req
    ) {
        String msg = "데이터베이스 무결성 제약조건 위반입니다.";

        if (e.getMessage().contains("Duplicate entry")) {
            msg = "이미 존재하는 데이터입니다.";
        }
        if (e.getMessage().contains("FOREIGN KEY")) {
            msg = "존재하지 않는 참조 데이터로 인해 작업이 실패했습니다.";
        }

        return build(HttpStatus.CONFLICT, msg, req);
    }

    // ===========================
    // 5) 요청 Content-Type 잘못됨
    // ===========================

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ErrorResponse handleMediaType(
            HttpMediaTypeNotSupportedException e, HttpServletRequest req
    ) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "지원하지 않는 Content-Type 입니다. (JSON 사용 권장)",
                req);
    }

    // ===========================
    // 6) 그 외 모든 예상 못한 오류
    // ===========================

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception e, HttpServletRequest req) {
        e.printStackTrace(); // 서버 로그에는 전체 예외 출력
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
                req
        );
    }
}

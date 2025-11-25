package com.jobmanager.job_manager.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Swagger와 완전 호환되는 에러 응답 DTO */
    public record ErrorResponse(
            int status,
            String error,
            String message,
            String path
    ) {}

    /** 잘못된 요청 */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException e, HttpServletRequest req){
        return new ErrorResponse(
                400,
                "Bad Request",
                e.getMessage(),
                req.getRequestURI()
        );
    }

    /** 서버 내부 오류 */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e, HttpServletRequest req){
        return new ErrorResponse(
                500,
                "Internal Server Error",
                e.getMessage(),
                req.getRequestURI()
        );
    }
}

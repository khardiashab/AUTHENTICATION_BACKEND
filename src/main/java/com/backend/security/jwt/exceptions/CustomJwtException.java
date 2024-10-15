package com.backend.security.jwt.exceptions;

import org.springframework.http.HttpStatus;

import com.backend.exceptions.CustomException;

import lombok.Getter;

@Getter
public class CustomJwtException extends CustomException {
    private final JwtExceptionStatus jwtExceptionStatus;

    public CustomJwtException(String message, JwtExceptionStatus jwtExceptionStatus, HttpStatus httpStatus) {
        super(message, httpStatus);
        this.jwtExceptionStatus = jwtExceptionStatus;
    }

    public CustomJwtException(String message, JwtExceptionStatus jwtExceptionStatus, HttpStatus httpStatus, Throwable cause) {
        super(message, httpStatus, cause);
        this.jwtExceptionStatus = jwtExceptionStatus;
    }

    public enum JwtExceptionStatus {
        EMPTY_TOKEN,
        UNSUPPORTED_TOKEN,
        INVALID_TOKEN,
        EXPIRED_TOKEN,
        SIGNING_ERROR
    }
}



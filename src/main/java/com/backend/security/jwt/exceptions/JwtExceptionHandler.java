package com.backend.security.jwt.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.backend.exceptions.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class JwtExceptionHandler {

    @ExceptionHandler(CustomJwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(CustomJwtException e) {
        log.error("JWT Exception: {} - Status: {}", e.getMessage(), e.getJwtExceptionStatus());
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), e.getJwtExceptionStatus().toString());
        return ResponseEntity.status(e.getHttpStatus()).body(errorResponse);
    }
    
}

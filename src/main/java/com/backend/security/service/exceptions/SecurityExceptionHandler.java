package com.backend.security.service.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.backend.exceptions.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class SecurityExceptionHandler {

    @ExceptionHandler(RoleAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleCustumSecurityException(RoleAccessDeniedException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getHttpStatus().toString());
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }
    
}

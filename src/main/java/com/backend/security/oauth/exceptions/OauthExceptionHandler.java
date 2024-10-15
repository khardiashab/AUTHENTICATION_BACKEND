package com.backend.security.oauth.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.backend.exceptions.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class OauthExceptionHandler {
    
    @ExceptionHandler(CustomOauthException.class)
    public ResponseEntity<ErrorResponse> handleCustumOauthException(CustomOauthException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getStatus().toString());
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }
}

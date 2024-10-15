package com.backend.exceptions;

import org.springframework.http.HttpStatus;

public class FailedToLoadEnvException extends CustomException {

    public FailedToLoadEnvException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
}

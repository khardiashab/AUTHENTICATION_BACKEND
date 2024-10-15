package com.backend.security.service.exceptions;

import org.springframework.http.HttpStatus;

import com.backend.exceptions.CustomException;

public class RoleAccessDeniedException extends CustomException{


    public RoleAccessDeniedException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
    
}

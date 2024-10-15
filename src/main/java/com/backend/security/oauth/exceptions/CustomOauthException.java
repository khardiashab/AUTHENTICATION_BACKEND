package com.backend.security.oauth.exceptions;

import org.springframework.http.HttpStatus;
import com.backend.exceptions.CustomException;
import lombok.Getter;

@Getter
public class CustomOauthException extends CustomException {

    private final CustomOauthExceptionStatus status;

    public CustomOauthException(String message, CustomOauthExceptionStatus status, HttpStatus httpStatus) {
        super(message, httpStatus);
        this.status = status;
    }

    public CustomOauthException(String message, CustomOauthExceptionStatus status, HttpStatus httpStatus, Throwable cause) {
        super(message, httpStatus, cause);
        this.status = status;
    }

    public enum CustomOauthExceptionStatus {
        FAILED_TO_LOAD_ENV,
        INVALID_AUTH_CODE,
        FAILED_TO_GET_ACCESS_TOKEN,
        FAILED_TO_GET_USER_INFO,
        USER_REGISTRATION_FAILED
    } 
}

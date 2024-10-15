package com.backend.security.jwt;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.backend.security.jwt.exceptions.CustomJwtException;
import com.backend.security.jwt.exceptions.CustomJwtException.JwtExceptionStatus;
import com.backend.security.service.SecuritySerivce;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class JwtValidationAspect {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JwtValidator jwtValidator;

    @Autowired
    private SecuritySerivce securityService;

    @Before("@annotation(JwtValidated)")
    public void validateJwtToken() {

        // Get token from header
        String jwtToken = request.getHeader("Authorization");

        if (jwtToken == null || jwtToken.isEmpty()) {
            throw new CustomJwtException("JWT token is missing or empty.", JwtExceptionStatus.EMPTY_TOKEN,
                    HttpStatus.BAD_REQUEST);
        }

        if (!jwtToken.startsWith("Bearer ")) {
            throw new CustomJwtException("JWT token does not start with 'Bearer'.",
                    JwtExceptionStatus.UNSUPPORTED_TOKEN, HttpStatus.UNAUTHORIZED);
        }

        String token = jwtToken.substring(7); // Remove "Bearer " prefix

        try {
            if (!jwtValidator.validateToken(token)) {
                throw new CustomJwtException("JWT token is invalid or expired.", JwtExceptionStatus.INVALID_TOKEN,
                        HttpStatus.UNAUTHORIZED);
            }

            // Extract username from token
            String username = jwtValidator.extractUsername(token);
            if (username == null) {
                throw new CustomJwtException("JWT token does not contain a valid username.",
                        JwtExceptionStatus.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
            }

            // Set authentication in the security context
            securityService.setAuthentication(username);
            log.info("Authentication set for user: {}", username);

        } catch (ExpiredJwtException e) {
            throw new CustomJwtException("JWT token has expired.", JwtExceptionStatus.EXPIRED_TOKEN,
                    HttpStatus.UNAUTHORIZED, e);
        } catch (UnsupportedJwtException e) {
            throw new CustomJwtException("JWT token is unsupported.", JwtExceptionStatus.UNSUPPORTED_TOKEN,
                    HttpStatus.UNAUTHORIZED, e);
        } catch (MalformedJwtException e) {
            throw new CustomJwtException("JWT token is malformed.", JwtExceptionStatus.INVALID_TOKEN,
                    HttpStatus.UNAUTHORIZED, e);
        } catch (SignatureException e) {
            throw new CustomJwtException("JWT token signature validation failed.", JwtExceptionStatus.SIGNING_ERROR,
                    HttpStatus.UNAUTHORIZED, e);
        } catch (Exception e) {
            throw new CustomJwtException("Failed to validate JWT token.", JwtExceptionStatus.INVALID_TOKEN,
                    HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

}
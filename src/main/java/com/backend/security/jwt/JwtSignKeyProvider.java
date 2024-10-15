package com.backend.security.jwt;

import java.security.Key;

import org.springframework.stereotype.Service;

import com.backend.exceptions.FailedToLoadEnvException;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtSignKeyProvider {

    private String secretKey;

    public JwtSignKeyProvider(Dotenv dotenv) {
        try {
            this.secretKey = dotenv.get("JWT_SECRET_KEY");
            
        } catch (Exception e) {
            log.error("Failed to get secret key from .env file: {}", e);
            throw new FailedToLoadEnvException("Failed to get secret key from .env file: " + e.getMessage());
            
        }
    }

    public Key getSignKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
}

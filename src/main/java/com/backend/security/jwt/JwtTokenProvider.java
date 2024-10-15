package com.backend.security.jwt;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.backend.security.user.entity.User;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.expiration_time}")
    private Long expirationTime;

    private final JwtSignKeyProvider jwtSignKeyProvider;

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(jwtSignKeyProvider.getSignKey())
                .compact();
    }
    
}
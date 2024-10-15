package com.backend.security.jwt;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.backend.security.jwt.exceptions.CustomJwtException;
import com.backend.security.jwt.exceptions.CustomJwtException.JwtExceptionStatus;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Service
public class JwtValidator {

    @Autowired
    JwtSignKeyProvider jwtSignKeyProvider;

    public Claims extractClaims(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(jwtSignKeyProvider.getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new CustomJwtException("JWT token has expired.", JwtExceptionStatus.EXPIRED_TOKEN, HttpStatus.UNAUTHORIZED, e);
        } catch (UnsupportedJwtException e) {
            throw new CustomJwtException("JWT token is unsupported.", JwtExceptionStatus.UNSUPPORTED_TOKEN, HttpStatus.UNAUTHORIZED, e);
        } catch (MalformedJwtException e) {
            throw new CustomJwtException("JWT token is malformed.", JwtExceptionStatus.INVALID_TOKEN, HttpStatus.UNAUTHORIZED, e);
        } catch (SignatureException e) {
            throw new CustomJwtException("JWT token signature validation failed.", JwtExceptionStatus.SIGNING_ERROR, HttpStatus.UNAUTHORIZED, e);
        } catch (IllegalArgumentException e) {
            throw new CustomJwtException("JWT token is illegal or inappropriate.", JwtExceptionStatus.INVALID_TOKEN, HttpStatus.UNAUTHORIZED, e);
        }
    }

    public boolean validateToken(String token) {
        // Validate expiration and other claims
        return extractExpiration(token).after(new Date());
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }
}

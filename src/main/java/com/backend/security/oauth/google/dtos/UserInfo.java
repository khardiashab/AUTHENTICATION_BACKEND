package com.backend.security.oauth.google.dtos;

public record UserInfo(
    String id,
    String email,
    String name,
    String picture,
    String verified_email
) {
    
}

package com.backend.security.user.dtos;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.backend.security.user.entity.User;

public record UserInfoResponse(
        String id,
        String name,
        String email,
        String picture,
        String provider,
        List<String> authorities) {

    public static UserInfoResponse from(User userEntity) {
        return new UserInfoResponse(
                userEntity.getId().toString(),
                userEntity.getName(),
                userEntity.getEmail(),
                userEntity.getPicture(),
                userEntity.getProvider(),
                userEntity.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList());
    }

}

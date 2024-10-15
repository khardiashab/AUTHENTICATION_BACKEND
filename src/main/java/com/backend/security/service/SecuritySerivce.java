package com.backend.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.backend.security.user.UserService;
import com.backend.security.user.entity.User;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SecuritySerivce {

    @Autowired
    private UserService userService;

    public void setAuthentication(String username) {

        var userDetails = userService.loadUserByUsername(username);
        var contextHolder = SecurityContextHolder.getContext();
        var authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        log.info("here is autheticationToken: {}", authenticationToken);
        contextHolder.setAuthentication(authenticationToken);
    }

    public User getUserDetails() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}

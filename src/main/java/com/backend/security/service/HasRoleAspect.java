package com.backend.security.service;

import java.util.Collection;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.backend.security.service.exceptions.RoleAccessDeniedException;
import com.backend.security.user.entity.Authority;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class HasRoleAspect {

    @Around("@annotation(hasRole)")
    public Object validateRole(ProceedingJoinPoint proceedingJoinPoint, HasRole hasRole) throws Throwable {
        Authority requiredRole = hasRole.value();
    
        log.info("Required role for access: {}", requiredRole);
    
        // Get user's authorities from the SecurityContext
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities();
    
        // If the user has no roles
        if (authorities == null || authorities.isEmpty()) {
            throw new RoleAccessDeniedException("You do not have any roles assigned. Access denied.");
        }
    
        // Check if the user has the required role
        boolean hasRequiredRole = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equalsIgnoreCase(requiredRole.name()));
    
        if (!hasRequiredRole) {
            throw new RoleAccessDeniedException("You don't have the required role: " + requiredRole.name());
        }
    
        log.info("User has the required role: {}. Proceeding with request.", requiredRole);
        return proceedingJoinPoint.proceed(); // Proceed with the method execution
    }
    
}

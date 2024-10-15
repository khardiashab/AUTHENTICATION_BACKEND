package com.backend;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.security.jwt.JwtValidated;
import com.backend.security.service.HasRole;
import com.backend.security.user.entity.Authority;

@RestController
@RequestMapping("/api")
public class DemoEndPoint {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }

    @GetMapping("/demo")
    @JwtValidated
    public String demo() {
        return "demo jwt World";
    }

    @GetMapping("/user")
    @JwtValidated
    @HasRole(Authority.USER)
    public String user() {
        return "user jwt World";
    }

    @GetMapping("/admin")
    @JwtValidated
    @HasRole(Authority.ADMIN)
    public String admin() {
        return "admin jwt World";
    }
}

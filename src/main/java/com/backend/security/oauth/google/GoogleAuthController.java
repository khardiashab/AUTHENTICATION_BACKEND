package com.backend.security.oauth.google;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.security.jwt.JwtTokenProvider;
import com.backend.security.oauth.exceptions.CustomOauthException;
import com.backend.security.user.entity.User;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/auth/google")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class GoogleAuthController {

    @Autowired
    private final GoogleAuthHelper googleAuthHelper;

    @Autowired
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("")
    public ResponseEntity<String> google() {
        log.info("Received Google Login Request");
        String authUrl = googleAuthHelper.createAuthUrl();
        log.info("Initiated Google authentication, redirecting to: {}", authUrl);
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                .header("Location", authUrl).build();
    }

    @GetMapping("")
    public ResponseEntity<String> googleGet() {
        log.info("Received Google Login Request (GET)");
        String authUrl = googleAuthHelper.createAuthUrl();
        log.info("Initiated Google authentication, redirecting to: {}", authUrl);
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                .header("Location", authUrl).build();
    }

    @GetMapping("/callback")
    public ResponseEntity<?> callback(@PathParam("code") String code) {
        log.info("Received Google Callback Request. Code is: {}", code);
            // Get access token from Google using the code
            String accessToken = googleAuthHelper.getAccesssToken(code);
            log.info("Successfully retrieved access token: {}", accessToken);
            
            // Get user information based on the access token
            User user = googleAuthHelper.getUserInfo(accessToken);
            log.info("Successfully retrieved user info for user: {}", user.getUsername());
            
            // Generate JWT token for the user
            String jwtToken = jwtTokenProvider.generateToken(user);
            log.info("Successfully generated JWT token for user: {}", user.getUsername());

            return ResponseEntity.status(HttpStatus.OK).body(jwtToken);
    }
}

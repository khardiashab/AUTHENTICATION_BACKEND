package com.backend.security.oauth.github;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.security.jwt.JwtTokenProvider;
import com.backend.security.user.entity.User;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/auth/github")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class GitHubAuthController {

    @Autowired
    private GitHubAuthHelper gitHubAuthHelper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("")
    public ResponseEntity<String> github() {
        log.info("Received GitHub Login Request");
        String authUrl = gitHubAuthHelper.createAuthUrl();
        log.info("Initiated GitHub authentication, redirecting to: {}", authUrl);
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                .header("Location", authUrl).build();
    }

    @GetMapping("/callback")
    public ResponseEntity<?> callback(@PathParam("code") String code) {
        log.info("Received GitHub Callback Request. Code is: {}", code);
        
        // Get access token from GitHub using the code
        String accessToken = gitHubAuthHelper.getAccessToken(code);
        log.info("Successfully retrieved access token: {}", accessToken);
        
        // Get user information based on the access token
        User user = gitHubAuthHelper.getUserInfo(accessToken);
        log.info("Successfully retrieved user info for user: {}", user.getUsername());
        
        // Generate JWT token for the user
        String jwtToken = jwtTokenProvider.generateToken(user);
        log.info("Successfully generated JWT token for user: {}", user.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(jwtToken);
    }
}

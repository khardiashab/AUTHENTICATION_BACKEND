package com.backend.security.oauth.github;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.backend.security.oauth.exceptions.CustomOauthException;
import com.backend.security.oauth.github.dtos.GitHubAccessToken;
import com.backend.security.oauth.github.dtos.GitHubUserInfo;
import com.backend.security.user.UserRepository;
import com.backend.security.user.entity.Authority;
import com.backend.security.user.entity.User;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GitHubAuthHelper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String clientId;

    private String clientSecret;

    private String redirectUri = "http://localhost:8080/api/auth/github/callback";

    private final String TOKEN_URL = "https://github.com/login/oauth/access_token";
    private final String USER_INFO_URL = "https://api.github.com/user";

    public GitHubAuthHelper(Dotenv dotenv) {
        this.clientId = dotenv.get("GITHUB_AUTH_CLIENT_ID");
        this.clientSecret = dotenv.get("GITHUB_AUTH_CLIENT_SECRET");
        if (clientId == null || clientSecret == null) {
            throw new CustomOauthException("Failed to load environment variables for GitHub Auth",
                    CustomOauthException.CustomOauthExceptionStatus.FAILED_TO_LOAD_ENV,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String createAuthUrl() {
        return new StringBuilder()
                .append("https://github.com/login/oauth/authorize")
                .append("?client_id=").append(clientId)
                .append("&redirect_uri=").append(redirectUri)
                .append("&scope=user")
                .append("&state=randomStateString") // Use state parameter for security
                .toString();
    }

    public String getAccessToken(String code) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("code", code);
            params.add("redirect_uri", redirectUri);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Accept", "application/json");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            ResponseEntity<GitHubAccessToken> response = restTemplate.postForEntity(TOKEN_URL, request,
                    GitHubAccessToken.class);

            if (response.getBody() == null) {
                throw new CustomOauthException("Failed to get access token",
                        CustomOauthException.CustomOauthExceptionStatus.FAILED_TO_GET_ACCESS_TOKEN,
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
            // String accessToken = response.getBody().getAccessToken();

            log.info("Access token retrieved: {}", response.getBody());
            return response.getBody().getAccess_token();

        } catch (Exception e) {
            throw new CustomOauthException("Error while getting access token from GitHub",
                    CustomOauthException.CustomOauthExceptionStatus.FAILED_TO_GET_ACCESS_TOKEN,
                    HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    public User getUserInfo(String accessToken) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<GitHubUserInfo> response = restTemplate.exchange(USER_INFO_URL, HttpMethod.GET, entity,
                    GitHubUserInfo.class);
            var userInfo = response.getBody();
            log.info("User info retrieved: {}", userInfo);

            if (userInfo == null) {
                throw new CustomOauthException("Failed to get user info",
                        CustomOauthException.CustomOauthExceptionStatus.FAILED_TO_GET_USER_INFO,
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return userRepository.findByUsername(userInfo.getLogin())
                    .orElseGet(() -> registerNewUser(userInfo));

        } catch (Exception e) {
            throw new CustomOauthException("Error while retrieving user info from GitHub",
                    CustomOauthException.CustomOauthExceptionStatus.FAILED_TO_GET_USER_INFO,
                    HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    public User registerNewUser(GitHubUserInfo userInfo) {
        try {
            User user = User.builder()
                    .email(userInfo.getEmail())
                    .username(userInfo.getLogin())
                    .password(passwordEncoder.encode(userInfo.getLogin()))
                    .name(userInfo.getName())
                    .oauthId(userInfo.getId())
                    .picture(userInfo.getAvatar_url())
                    .provider("GITHUB")
                    .authorities(Set.of(Authority.USER))
                    .build();

            return userRepository.save(user);

        } catch (Exception e) {
            throw new CustomOauthException("User registration failed",
                    CustomOauthException.CustomOauthExceptionStatus.USER_REGISTRATION_FAILED,
                    HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }
}

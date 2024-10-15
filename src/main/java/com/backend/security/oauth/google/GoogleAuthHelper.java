package com.backend.security.oauth.google;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.*;

import com.backend.security.oauth.exceptions.CustomOauthException;
import com.backend.security.oauth.google.dtos.AccessToken;
import com.backend.security.oauth.google.dtos.UserInfo;
import com.backend.security.user.UserRepository;
import com.backend.security.user.entity.Authority;
import com.backend.security.user.entity.User;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GoogleAuthHelper {

    @Autowired
    private UserRepository userEntityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String clientId;
    private String clientSecret;

    private final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";
    private final String responseType = "code";
    private final String redirectUri = "http://localhost:8080/api/auth/google/callback";

    @Value(value = "${google.scope}")
    private String scope;

    public GoogleAuthHelper(Dotenv dotenv) {
        this.clientId = dotenv.get("GOOGLE_AUTH_CLIENT_ID");
        this.clientSecret = dotenv.get("GOOGLE_AUTH_CLIENT_SECRET");
        if (clientId == null || clientSecret == null) {
            throw new CustomOauthException("Failed to load environment variables for Google Auth",
                    CustomOauthException.CustomOauthExceptionStatus.FAILED_TO_LOAD_ENV, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String createAuthUrl() {
        return new StringBuilder()
                .append("https://accounts.google.com/o/oauth2/auth")
                .append("?client_id=").append(clientId)
                .append("&redirect_uri=").append(redirectUri)
                .append("&scope=").append(scope)
                .append("&response_type=").append(responseType)
                .toString();
    }

    public String getAccesssToken(String code) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("redirect_uri", redirectUri);
            params.add("grant_type", "authorization_code");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, new HttpHeaders());
            ResponseEntity<AccessToken> response = restTemplate.postForEntity(TOKEN_URL, request, AccessToken.class);
            
            String accessToken = response.getBody().access_token();
            if (accessToken == null) {
                throw new CustomOauthException("Failed to get access token",
                        CustomOauthException.CustomOauthExceptionStatus.FAILED_TO_GET_ACCESS_TOKEN,
                        HttpStatus.UNAUTHORIZED);
            }

            log.info("Access token retrieved: {}", accessToken);
            return accessToken;

        } catch (Exception e) {
            throw new CustomOauthException("Error while getting access token",
                    CustomOauthException.CustomOauthExceptionStatus.FAILED_TO_GET_ACCESS_TOKEN, HttpStatus.UNAUTHORIZED, e);
        }
    }

    public User getUserInfo(String accessToken) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<UserInfo> response = restTemplate.exchange(USER_INFO_URL, HttpMethod.GET, entity, UserInfo.class);
            UserInfo userInfo = response.getBody();

            if (userInfo == null) {
                throw new CustomOauthException("Failed to get user info",
                        CustomOauthException.CustomOauthExceptionStatus.FAILED_TO_GET_USER_INFO,
                        HttpStatus.UNAUTHORIZED);
            }

            return userEntityRepository.findByUsername(userInfo.email())
                    .orElseGet(() -> registerNewUser(userInfo));

        } catch (Exception e) {
            throw new CustomOauthException("Error while retrieving user info",
                    CustomOauthException.CustomOauthExceptionStatus.FAILED_TO_GET_USER_INFO, HttpStatus.UNAUTHORIZED, e);
        }
    }

    public User registerNewUser(UserInfo userInfo) {
        try {
            User userEntity = User.builder()
                    .email(userInfo.email())
                    .username(userInfo.email())
                    .password(passwordEncoder.encode(userInfo.email()))
                    .name(userInfo.name())
                    .picture(userInfo.picture())
                    .oauthId(userInfo.id())
                    .provider("GOOGLE")
                    .authorities(Set.of(Authority.USER))
                    .build();

            return userEntityRepository.save(userEntity);

        } catch (Exception e) {
            throw new CustomOauthException("User registration failed",
                    CustomOauthException.CustomOauthExceptionStatus.USER_REGISTRATION_FAILED, HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }
}

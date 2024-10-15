package com.backend.security.oauth.github.dtos;

import lombok.Data;

@Data
public class GitHubUserInfo {
    private String avatar_url;
    private String login;
    private String email;
    private String name;
    private String id;
}

package com.example.codetwin.backend.dto;

public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private Long userId;

    public LoginResponse(String accessToken, String refreshToken, Long userId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Long getUserId() {
        return userId;
    }
}
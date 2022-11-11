package com.example.datnbackend.dto.security;

import java.util.Set;

public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "bearer";
    private String type;
    public JwtAuthenticationResponse(String accessToken, String type){
        this.accessToken = accessToken;
        this.type = type;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

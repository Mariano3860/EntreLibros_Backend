package com.entrelibros.backend.auth.dto;

public class LoginResponse {
    private String token;
    private UserDto user;
    private String messageKey;

    public LoginResponse(String token, UserDto user, String messageKey) {
        this.token = token;
        this.user = user;
        this.messageKey = messageKey;
    }

    public String getToken() {
        return token;
    }

    public UserDto getUser() {
        return user;
    }

    public String getMessageKey() {
        return messageKey;
    }
}

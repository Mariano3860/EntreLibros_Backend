package com.entrelibros.backend.auth.dto;

import java.util.UUID;

public class UserDto {
    private UUID id;
    private String email;
    private String role;

    public UserDto(UUID id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}

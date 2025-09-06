package com.entrelibros.backend.auth;

import com.entrelibros.backend.auth.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void loginSuccess() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@entrelibros.com");
        request.setPassword("correcthorsebatterystaple");
        mockMvc.perform(post("/api/v1/auth/login").contextPath("/api/v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(header().stringValues("Set-Cookie", org.hamcrest.Matchers.hasItem(org.hamcrest.Matchers.containsString("sessionToken"))))
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.user.email").value("user@entrelibros.com"))
            .andExpect(jsonPath("$.messageKey").value("auth.success.login"));
    }

    @Test
    void loginInvalidPassword() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@entrelibros.com");
        request.setPassword("wrong");
        mockMvc.perform(post("/api/v1/auth/login").contextPath("/api/v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("InvalidCredentials"))
            .andExpect(jsonPath("$.messageKey").value("auth.errors.invalid_credentials"));
    }
}

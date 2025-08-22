package com.entrelibros.backend.auth;

import com.entrelibros.backend.auth.dto.LoginRequest;
import com.entrelibros.backend.user.User;
import com.entrelibros.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
        registry.add("spring.datasource.driverClassName", () -> "org.h2.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.flyway.enabled", () -> "false");
        registry.add("JWT_SECRET", () -> "test-secret-0123456789abcdef0123456789abcd");
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("user@entrelibros.com");
        user.setPassword(passwordEncoder.encode("correcthorsebatterystaple"));
        user.setRole(User.Role.USER);
        userRepository.save(user);
    }

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
            .andExpect(jsonPath("$.data.token").isNotEmpty())
            .andExpect(jsonPath("$.data.user.email").value("user@entrelibros.com"))
            .andExpect(jsonPath("$.data.messageKey").value("auth.success.login"));
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

package com.entrelibros.backend.auth;

import com.entrelibros.backend.auth.dto.ApiResponse;
import com.entrelibros.backend.auth.dto.LoginRequest;
import com.entrelibros.backend.auth.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Validated @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        String cookiePath = contextPath.endsWith("/") ? contextPath + "auth" : contextPath + "/auth";
        ResponseCookie cookie = ResponseCookie.from("sessionToken", response.getToken())
            .httpOnly(true)
            .secure(true)
            .path(cookiePath)
            .sameSite("Strict")
            .build();
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(new ApiResponse<>(response));
    }
}

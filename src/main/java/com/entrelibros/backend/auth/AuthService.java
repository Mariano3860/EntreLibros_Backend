package com.entrelibros.backend.auth;

import com.entrelibros.backend.auth.dto.LoginRequest;
import com.entrelibros.backend.auth.dto.LoginResponse;
import com.entrelibros.backend.auth.dto.UserDto;
import com.entrelibros.backend.security.JwtService;
import com.entrelibros.backend.user.User;
import com.entrelibros.backend.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException();
        }
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(InvalidCredentialsException::new);
        String token = jwtService.generateToken(user);
        UserDto userDto = new UserDto(user.getId(), user.getEmail(), user.getRole().name().toLowerCase());
        return new LoginResponse(token, userDto, "auth.success.login");
    }
}

package com.entrelibros.backend.security;

import com.entrelibros.backend.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtService {

    private final SecretKey key;
    private final Duration accessTtl;
    private final String issuer;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.accessTtl}") Duration accessTtl,
                      @Value("${jwt.issuer}") String issuer) {
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 32 bytes (256 bits) for HMAC-SHA256.");
        }
        this.key = Keys.hmacShaKeyFor(secretBytes);
        this.accessTtl = accessTtl;
        this.issuer = issuer;
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTtl.toMillis());
        return Jwts.builder()
            .setSubject(user.getId().toString())
            .setIssuer(issuer)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .claim("email", user.getEmail())
            .claim("role", user.getRole().name().toLowerCase())
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }
}

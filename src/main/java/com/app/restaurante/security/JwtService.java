package com.app.restaurante.security;

import com.app.restaurante.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private static final long EXPIRATION_MINUTES = 60;

    private final SecretKey key;

    public JwtService(@Value("${jwt.secret}") String secret) {
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET precisa de 32+ bytes");
        }
        this.key = Keys.hmacShaKeyFor(secretBytes);
    }

    public String generateToken(UserEntity user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("role", normalizeRole(user.getRole()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(EXPIRATION_MINUTES * 60)))
                .signWith(key)
                .compact();
    }

    public Claims validate(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public UUID getUserId(String token) {
        return UUID.fromString(validate(token).getSubject());
    }

    public String getRole(Claims claims) {
        return normalizeRole(claims.get("role", String.class));
    }

    public static String normalizeRole(String role) {
        if (role == null) {
            return "USER";
        }
        String upper = role.toUpperCase();
        return "ADMIN".equals(upper) ? "ADMIN" : "USER";
    }
}

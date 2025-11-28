package org.quarkus.rest.service;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.eclipse.microprofile.jwt.Claims;

@ApplicationScoped
public class TokenService {

    public String generateToken(String email) {
        Instant now = Instant.now();

        return Jwt.issuer("https://yourdomain.com")
                .upn(email)
                .claim(Claims.email.name(), email)
                .groups("User")
                .issuedAt(now)
                .expiresAt(now.plus(12, ChronoUnit.HOURS)) // 12 hours expiration
                .sign();
    }

    public String refreshToken(String email) {
        return generateToken(email); // Same logic for refreshing
    }
}
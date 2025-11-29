package org.quarkus.rest.service;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.eclipse.microprofile.jwt.Claims;

@ApplicationScoped
public class TokenService {

    public String generateToken(String email) {
        try {
            System.out.println("DEBUG: TokenService.generateToken called for email: " + email);
            Instant now = Instant.now();

            System.out.println("DEBUG: Building JWT claims");
            String token = Jwt.claims()
                    .upn(email)
                    .claim(Claims.email.name(), email)
                    .groups("User")
                    .issuedAt(now)
                    .expiresAt(now.plus(12, ChronoUnit.HOURS)) // 12 hours expiration
                    .sign();

            System.out.println("DEBUG: JWT token generated successfully, length: " + token.length());
            return token;
        } catch (Exception e) {
            System.out.println("DEBUG: Exception in TokenService.generateToken: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public String refreshToken(String email) {
        return generateToken(email); // Same logic for refreshing
    }
}
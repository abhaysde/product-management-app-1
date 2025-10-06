package com.gspann.auth_service.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtUtil {
    private static final long EXPIRATION_TIME = 1000 * 60 * 10;
    @Value("${jwt.secretKey}")
    private String secret;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username) {
        Date issuedAt = new Date();
        Date expirationDate = new Date(issuedAt.getTime() + EXPIRATION_TIME);

        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(issuedAt)
                .setExpiration(expirationDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                .compact();

        log.info("Generated token for user '{}'. Issued at: {}, Expires at: {}", username, issuedAt, expirationDate);
        return token;
    }

    // ✅ Extract username (subject) from JWT token
    public String extractUsername(String token) {
        try {
            token = stripBearerPrefix(token);

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();

        } catch (JwtException e) {
            log.error("Failed to extract username from token: {}", e.getMessage());
            return null;
        }
    }

    public boolean isTokenValid(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                log.warn("Token is null or empty");
                return false;
            }

            token = stripBearerPrefix(token);

            Jws<Claims> parsedToken = Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .setAllowedClockSkewSeconds(60) // 60s tolerance
                    .build()
                    .parseClaimsJws(token);

            Claims claims = parsedToken.getBody();

            Date expiration = claims.getExpiration();
            if (expiration == null) {
                log.warn("Token has no expiration claim");
                return false;
            }

            boolean notExpired = expiration.after(new Date());
            if (!notExpired) {
                log.warn("Token has expired at: {}", expiration);
            }

            return notExpired;

        } catch (ExpiredJwtException e) {
            log.warn("Token has expired at {}. Now: {}", e.getClaims().getExpiration(), new Date());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT token: {}", e.getMessage());
        }  catch (JwtException e) {
            log.error("JWT processing error: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during token validation: {}", e.getMessage(), e);
        }

        return false;
    }
    
    private String stripBearerPrefix(String token) {
        return token != null && token.startsWith("Bearer ") ? token.substring(7) : token;
    }
}

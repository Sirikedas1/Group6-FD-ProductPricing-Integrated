package com.banklab.calculator.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

/**
 * Creates and checks JWT login tokens (Lab L3 section 4.4).
 * A token looks like xxxxx.yyyyy.zzzzz ; the middle part holds
 *   { "sub": "alice123", "role": "CUSTOMER", "exp": ... }
 * and the last part is a signature made with the shared secret, so nobody can fake it.
 */
@Service
public class JwtService {

    /** Who the token belongs to. */
    public record TokenUser(String username, String role) {
    }

    private final SecretKey key;
    private final long expirySeconds;

    public JwtService(@Value("${calculator.jwt.secret}") String secret,
                      @Value("${calculator.jwt.secret-base64:false}") boolean secretIsBase64,
                      @Value("${calculator.jwt.expiry-seconds:36000}") long expirySeconds) {
        byte[] bytes = secretIsBase64
                ? Decoders.BASE64.decode(secret)
                : secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException(
                    "calculator.jwt.secret must be at least 32 characters (256 bits) long");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.expirySeconds = expirySeconds;
    }

    /** Makes a new token (used only by the dev login stand-in). */
    public String createToken(String username, String role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirySeconds * 1000))
                .signWith(key)
                .compact();
    }

    /**
     * Checks the signature and expiry of a token and reads the user from it.
     * Throws JwtException if the token is fake, damaged or expired.
     */
    public TokenUser parse(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return new TokenUser(claims.getSubject(), extractRole(claims));
    }

    public long getExpirySeconds() {
        return expirySeconds;
    }

    /** Accepts "role": "CUSTOMER", "role": "ROLE_CUSTOMER" or "roles": ["CUSTOMER"]. */
    private static String extractRole(Claims claims) {
        Object role = claims.get("role");
        if (role == null) {
            Object roles = claims.get("roles");
            if (roles instanceof Collection<?> list && !list.isEmpty()) {
                role = list.iterator().next();
            } else if (roles instanceof String text) {
                role = text;
            }
        }
        if (role == null) {
            return null;
        }
        String r = role.toString().trim().toUpperCase(Locale.ROOT);
        return r.startsWith("ROLE_") ? r.substring(5) : r;
    }
}

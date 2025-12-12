package co.penny.dronedelivery.auth.jwt;

import co.penny.dronedelivery.common.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * JWT creation and validation service.
 */
@Service
public class JwtService {

    private final SecretKey signingKey; // Key signingKey;
    private final long expirationSeconds;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-seconds}") long expirationSeconds
    ) {
        this.signingKey =
                Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(String name, UserRole role) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(name)
                .claim("role", role.value())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(signingKey, Jwts.SIG.HS256) // .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token) {
        return parseClaims(token)
                .getExpiration()
                .toInstant()
                .isAfter(Instant.now());
    }

    public String extractName(String token) {
        return parseClaims(token).getSubject();
    }

    public UserRole extractRole(String token) {
        return UserRole.fromValue(
                parseClaims(token).get("role", String.class)
        );
    }
}

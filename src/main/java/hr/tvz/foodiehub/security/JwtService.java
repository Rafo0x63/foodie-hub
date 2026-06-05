package hr.tvz.foodiehub.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final String secret;
    private final Clock clock;

    public JwtService(@Value("${jwt.secret}") String secret, Clock clock) {
        this.secret = secret;
        this.clock = clock;
    }

    public boolean isValid(String token) {
        try {
            Claims claims = extractAllClaims(token);

            return claims.getExpiration() != null
                    && claims.getExpiration().after(Date.from(clock.instant()))
                    && "access".equals(claims.get("type", String.class));

        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);

        return claims.get("roles", List.class);
    }

    public String generateToken(String username, List<String> roles) {
        Instant now = clock.instant();

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .claim("type", "access")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofHours(10))))
                .signWith(getSigningKey())
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}

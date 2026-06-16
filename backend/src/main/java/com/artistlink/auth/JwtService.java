package com.artistlink.auth;

import com.artistlink.common.AuthorType;
import com.artistlink.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final SecretKey key;
    private final JwtProperties props;

    public JwtService(JwtProperties props) {
        this.props = props;
        this.key = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(props.getSecret()));
    }

    public String issueAccessToken(UUID userId, AuthorType authorType, UUID authorId, String email) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId.toString())
                .claim("authorType", authorType.name())
                .claim("authorId", authorId.toString())
                .claim("email", email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(props.getAccessTokenTtlSeconds())))
                .signWith(key)
                .compact();
    }

    public AuthPrincipal parse(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return new AuthPrincipal(
                UUID.fromString(claims.getSubject()),
                AuthorType.valueOf(claims.get("authorType", String.class)),
                UUID.fromString(claims.get("authorId", String.class)),
                claims.get("email", String.class)
        );
    }
}

package com.jobmanager.job_manager.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key key;

    private static final long ACCESS_30_MIN = 30 * 60 * 1000L;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey
    ) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // ✅ Access Token 발급 ONLY
    public String generateAccessToken(
            Long accountId,
            String username,
            String accountType,
            String role,
            boolean onboarded,
            String companyName,
            String businessNumber
    ) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ACCESS_30_MIN);

        var builder = Jwts.builder()
                .setSubject(String.valueOf(accountId))
                .claim("username", username)
                .claim("type", accountType)
                .claim("role", role)
                .claim("onboarded", onboarded)
                .setIssuedAt(now)
                .setExpiration(expiry);

        if (companyName != null) builder.claim("companyName", companyName);
        if (businessNumber != null) builder.claim("businessNumber", businessNumber);

        return builder
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ================= 파싱 =================

    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getAccountId(String token) {
        return Long.valueOf(parse(token).getSubject());
    }

    public String getUsername(String token) {
        return parse(token).get("username", String.class);
    }

    public String getAccountType(String token) {
        return parse(token).get("type", String.class);
    }

    public String getRole(String token) {
        return parse(token).get("role", String.class);
    }
}

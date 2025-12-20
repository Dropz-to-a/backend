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
    private final long expirationMs; // yml 유지용 (실제 사용 안 함)

    private static final long ACCESS_30_MIN = 30 * 60 * 1000L;
    private static final long ACCESS_1_DAY  = 24 * 60 * 60 * 1000L;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.expirationMs = expirationMs;
    }

    /** 로그인 시 AccessToken (30분) */
    public String generateLoginToken(
            Long accountId,
            String username,
            String accountType,
            String roleCode,
            boolean onboarded,
            String companyName,
            String businessNumber
    ) {
        return generateWithExpiration(
                accountId, username, accountType, roleCode,
                onboarded, companyName, businessNumber,
                ACCESS_30_MIN
        );
    }

    /** Refresh 후 AccessToken (1일) */
    public String generateRefreshToken(
            Long accountId,
            String username,
            String accountType,
            String roleCode,
            boolean onboarded,
            String companyName,
            String businessNumber
    ) {
        return generateWithExpiration(
                accountId, username, accountType, roleCode,
                onboarded, companyName, businessNumber,
                ACCESS_1_DAY
        );
    }

    private String generateWithExpiration(
            Long accountId,
            String username,
            String accountType,
            String roleCode,
            boolean onboarded,
            String companyName,
            String businessNumber,
            long customExpirationMs
    ) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + customExpirationMs);

        var builder = Jwts.builder()
                .setSubject(String.valueOf(accountId))
                .claim("username", username)
                .claim("type", accountType)
                .claim("role", roleCode)
                .claim("onboarded", onboarded)
                .setIssuedAt(now)
                .setExpiration(expiry);

        if (companyName != null) builder.claim("companyName", companyName);
        if ("COMPANY".equals(accountType) && businessNumber != null) {
            builder.claim("businessNumber", businessNumber);
        }

        return builder.signWith(key, SignatureAlgorithm.HS256).compact();
    }

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

    public Boolean isOnboarded(String token) {
        return parse(token).get("onboarded", Boolean.class);
    }
}

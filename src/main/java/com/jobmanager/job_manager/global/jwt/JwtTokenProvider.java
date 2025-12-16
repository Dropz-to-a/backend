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
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.expirationMs = expirationMs;
    }

    /** JWT 생성 */
    public String generate(Long accountId,
                           String username,
                           String accountType,
                           String roleCode) {

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(String.valueOf(accountId))
                .claim("username", username)
                .claim("type", accountType)
                .claim("role", roleCode)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** JWT 파싱 (필터에서 사용) */
    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /** 토큰에서 accountId 가져오기 */
    public Long getAccountId(String token) {
        return Long.valueOf(parse(token).getSubject());
    }

    /** 토큰에서 username 가져오기 */
    public String getUsername(String token) {
        return parse(token).get("username", String.class);
    }

    /** 토큰에서 accountType 가져오기 (USER / COMPANY / ADMIN) */
    public String getAccountType(String token) {
        return parse(token).get("type", String.class);
    }

    /** 토큰에서 roleCode 가져오기 (ROLE_USER / ROLE_COMPANY / ROLE_ADMIN) */
    public String getRole(String token) {
        return parse(token).get("role", String.class);
    }

}

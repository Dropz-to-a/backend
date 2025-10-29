package com.jobmanager.job_manager.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private static final String SECRET = "jobmanager_super_secret_key_please_change_!!!";
    private static final long EXP_MS = 1000L * 60 * 60 * 24; // 1일

    public String generate(String subject, String role) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(subject)              // username or email
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + EXP_MS))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }
}

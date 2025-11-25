package com.jobmanager.job_manager.global.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class JwtHeaderUtils {

    public static String getTokenFromHeader() {
        HttpServletRequest req =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();

        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer "))
            throw new IllegalArgumentException("JWT 토큰이 필요합니다.");

        return auth.substring(7);
    }
}

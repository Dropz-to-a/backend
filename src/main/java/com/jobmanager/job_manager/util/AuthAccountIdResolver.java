package com.jobmanager.job_manager.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthAccountIdResolver {

    /**
     * companyId 추출
     * - 1순위: SecurityContext (JWT 인증 기반)
     * - 2순위: Header fallback (개발용)
     */
    public Long resolveCompanyIdOrThrow(Long headerFallback) {

        Long fromSecurity = resolveFromSecurityContext();
        if (fromSecurity != null) {
            return fromSecurity;
        }

        if (headerFallback != null) {
            return headerFallback;
        }

        throw new IllegalStateException("companyId를 확인할 수 없습니다.");
    }

    /**
     * SecurityContext에서 companyId 추출
     */
    private Long resolveFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        // case 1: principal 자체가 Long
        if (principal instanceof Long) {
            return (Long) principal;
        }

        // case 2: principal이 String (id를 문자열로 넣어둔 경우)
        if (principal instanceof String str) {
            try {
                return Long.parseLong(str);
            } catch (NumberFormatException ignored) {
            }
        }

        // case 3: CustomUserDetails.getId()
        try {
            var method = principal.getClass().getMethod("getId");
            Object value = method.invoke(principal);
            if (value instanceof Long) {
                return (Long) value;
            }
        } catch (Exception ignored) {
        }

        return null;
    }
}

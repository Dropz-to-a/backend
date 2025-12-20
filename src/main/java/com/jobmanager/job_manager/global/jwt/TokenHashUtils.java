package com.jobmanager.job_manager.global.jwt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class TokenHashUtils {

    private TokenHashUtils() {}

    public static String sha256(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(raw.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("RefreshToken hash 실패", e);
        }
    }
}

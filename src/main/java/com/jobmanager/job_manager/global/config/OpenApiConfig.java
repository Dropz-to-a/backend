package com.jobmanager.job_manager.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI 설정
 * - JWT Bearer 인증 스킴 등록
 * - 기본 SecurityRequirement 로 설정해서 모든 API가 토큰을 요구하도록 표시
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "JobManager API",
                version = "v0.0.1",
                description = "JobManager 백엔드 API 문서"
        ),
        servers = {
                @Server(url = "http://localhost:8083", description = "Local"),
                @Server(url = "/", description = "Current server")
        },
        security = {
                @SecurityRequirement(name = "bearerAuth")   // 🔐 기본으로 JWT 필요하게 표시
        }
)
@SecurityScheme(
        name = "bearerAuth",                 // 위에서 reference 한 이름
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
    // 따로 Bean 안 만들어도 됨. 어노테이션만으로 설정 끝.
}

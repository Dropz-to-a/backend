package com.jobmanager.job_manager.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "JobManager API", version = "v1.0.0"),
        servers = {
                @Server(url = "/", description = "Dynamic (ngrok / prod / local 자동 대응)")
        },
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {

    @Bean
    public OpenApiCustomizer dynamicServerCustomizer() {
        return openApi -> {
            openApi.setServers(
                    java.util.Collections.singletonList(
                            new io.swagger.v3.oas.models.servers.Server()
<<<<<<< Updated upstream
                                    .url("/")
=======
                                    .url("/")    // 중요: "현재 접속한 서버" 기준으로 URL 설정
>>>>>>> Stashed changes
                    )
            );
        };
    }
}
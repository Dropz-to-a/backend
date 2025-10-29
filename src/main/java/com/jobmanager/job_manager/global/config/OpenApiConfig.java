package com.jobmanager.job_manager.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.ExternalDocumentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    // Swagger UI: /swagger-ui/index.html
    @Bean
    public OpenAPI jobManagerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("JobManager API")
                        .version("v0.0.1")
                        .description("JobManager 백엔드 API 문서"))
                .externalDocs(new ExternalDocumentation()
                        .description("Swagger UI Path")
                        .url("/swagger-ui/index.html")); // 단순 안내용
    }
}

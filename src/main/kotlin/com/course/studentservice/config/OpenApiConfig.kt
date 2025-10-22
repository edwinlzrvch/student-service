package com.course.studentservice.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.security.SecurityRequirement
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    
    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Student Service API")
                    .description("A comprehensive REST API for managing students, courses, and enrollments with JWT authentication")
                    .version("1.0.0")
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        "bearerAuth",
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description("Enter JWT token")
                    )
            )
            .addSecurityItem(
                SecurityRequirement()
                    .addList("bearerAuth")
            )
    }
}

package com.banklab.calculator.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Adds an "Authorize" button to Swagger UI so you can paste a login token
 * and test the protected endpoints from the browser.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "FD Calculator Service", version = "1.0",
                description = "Banking Technology Lab (CSF 3142) - Group 6 - Labs L6, L10, L11"),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {
}

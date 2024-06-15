package org.example.projectnu.common.swagger

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
class SwaggerConfig {
    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .components(Components())
            .info(configurationInfo())
            .addSecurityItem(io.swagger.v3.oas.models.security.SecurityRequirement().addList("bearerAuth"))
    }

    private fun configurationInfo(): Info {
        return Info()
            .title("Demo Web")
            .description("Demo Web 테스트")
            .version("1.0.0")
    }
}

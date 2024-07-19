package br.com.santanna.ponto_eletronico.infrastructure.config
import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.security.SecurityScheme.In
import io.swagger.v3.oas.models.security.SecurityScheme.Type
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApi {
    @Bean
    fun customOpenAPI(): OpenAPI {

        return OpenAPI()
            .info(
                Info()
                    .title("Kronos Tech Time Solutions API")
                    .version("1.0")
                    .description("Sistema que cadastra funcionários e controla o registro de horas")
                    .contact(Contact().name("Equipe Kronos").email("kronos.time.tech.solutions@gmail.com"))
                    .license(License().name("Apache 2.0").url("http://springdoc.org"))
            )
            .externalDocs(
                ExternalDocumentation()
                    .description("SpringDoc Documentation")
                    .url("https://springdoc.org")
            )
            .addSecurityItem(
                SecurityRequirement().addList("Bearer Authentication")
            )
            .components(
                io.swagger.v3.oas.models.Components()
                    .addSecuritySchemes(
                        "Bearer Authentication",
                        SecurityScheme()
                            .type(Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                    )
            )
    }


    @Bean
    fun publicApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("public")
            .pathsToMatch("/api/**")
            .build()
    }
}
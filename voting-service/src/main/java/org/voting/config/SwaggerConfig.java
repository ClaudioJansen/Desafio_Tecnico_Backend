package org.voting.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi votingApi() {
        return GroupedOpenApi.builder()
                .group("voting-api-v1")
                .pathsToMatch("/api/v1/**")
                .build();
    }

    @Bean
    public io.swagger.v3.oas.models.OpenAPI votingOpenAPI() {
        return new io.swagger.v3.oas.models.OpenAPI()
                .info(new Info().title("Voting System API")
                        .description("API for managing cooperative agendas and voting sessions")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Cláudio Jansen")
                                .email("caujansen@gmail.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}

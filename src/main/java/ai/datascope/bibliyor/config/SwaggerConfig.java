package ai.datascope.bibliyor.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info().title("BIBLIYOR REST APIs")
                        .description("GPT for Bibliographic Analysis")
                        .version("1.0"));
    }
}

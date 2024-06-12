package ai.datascope.bibliyor.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "OpenAI API", version = "v1", description = "GPT for Bibliographic Analysis"))
public class SwaggerConfig {

}

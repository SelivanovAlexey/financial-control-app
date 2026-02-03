package app.core.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "openapi.title",
                version = "openapi.version",
                description = "openapi.description"
        ))
//TODO: low-prio: нужна ли здесь возможность authorize button?
public class OpenApiConfig {

}
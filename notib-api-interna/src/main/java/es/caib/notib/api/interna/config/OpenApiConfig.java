package es.caib.notib.api.interna.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API Interna NOTIB",
                description = "API Interna de NOTIB",
                version = "v2.0",
                contact = @Contact(name = "Limit Tecnologies", url = "http://limit.es", email = "limit@limit.es")
        ),
        security = @SecurityRequirement(name = "basic"),
        servers = {
                @Server(url = "/notibapi/interna", description = "Servidor per defecte"),
                @Server(url = "https://proves.caib.es/notibapi/interna", description = "Servidor de l'entorn de PROVES")
        }
)
@SecurityScheme(name = "basic", scheme = "basic", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {

}
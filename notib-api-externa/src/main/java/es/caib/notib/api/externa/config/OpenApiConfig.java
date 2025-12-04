package es.caib.notib.api.externa.config;

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
                title = "API Externa NOTIB",
                description = "API Externa de NOTIB",
                version = "v2.0",
                contact = @Contact(name = "Limit Tecnologies", url = "http://limit.es", email = "limit@limit.es")
        ),
        security = @SecurityRequirement(name = "basic"),
        servers = {
                @Server(url = "/notibapi/externa", description = "Servidor per defecte"),
                @Server(url = "https://proves.caib.es/notibapi/externa", description = "Servidor de l'entorn de PROVES"),
                @Server(url = "https://dev.caib.es/notibapi/externa", description = "Servidor de l'entorn de DESENVOLUPAMENT"),
                @Server(url = "https://se.caib.es/notibapi/externa", description = "Servidor de l'entorn de SERVEIS ESTABLES")
        }
)
@SecurityScheme(name = "basic", scheme = "basic", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {

}

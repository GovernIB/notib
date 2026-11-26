package es.caib.notib.api.interna.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.webjars.WebJarAssetLocator;

@Slf4j
@Configuration
public class SwaggerUiResourceConfig implements WebMvcConfigurer {

    private static final String SWAGGER_UI_VERSION;

    static {
        // Resol la versió UNA vegada al startup, amb el classloader de NOTIB (correcte).
        String version = null;
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:/META-INF/resources/webjars/swagger-ui/*/index.html");
            if (resources.length > 0) {
                String path = resources[0].getURL().getPath();
                // extreu la versió del path .../swagger-ui/<version>/index.html
                version = path.substring(path.indexOf("swagger-ui/") + "swagger-ui/".length(),
                        path.lastIndexOf("/index.html"));
            }
        } catch (Exception e) {
            log.error("[SwaggerUiResourceConfig] Error resolguent la versió del swagger", ex);
        }
        SWAGGER_UI_VERSION = version;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (SWAGGER_UI_VERSION != null) {
            // Serveix /swagger-ui/** directament des del classpath versionat de NOTIB.
            // No passa pel webjars-locator (que és el que causa el conflicte).
            registry.addResourceHandler("/swagger-ui/**")
                    .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/" + SWAGGER_UI_VERSION + "/")
                    .setCachePeriod(3600);
        }
    }

    @Bean
    @Primary
    public WebJarAssetLocator webJarResourceLocator() {
        // Fixa el classloader al del propi desplegament (NOTIB), no al TCCL.
        // Un webjar que abans es resolia per TCCL ara es resol pel classloader de NOTIB, que és el correcte per a aquesta app.
        return new WebJarAssetLocator(SwaggerUiResourceConfig.class.getClassLoader());
    }
}
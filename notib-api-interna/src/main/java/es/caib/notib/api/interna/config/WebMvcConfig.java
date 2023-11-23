/**
 * 
 */
package es.caib.notib.api.interna.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuració de Spring web MVC.
 * 
 * @author Limit Tecnologies
 */
@Configuration
//@Configuration("apiInternaWebMvcConfig")
//@DependsOn("apiInternaEjbClientConfig")
public class WebMvcConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
	}
//	public void addCorsMappings(CorsRegistry registry) {
//		registry.addMapping("/**");
//	}

}

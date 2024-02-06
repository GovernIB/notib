/**
 * 
 */
package es.caib.notib.api.interna.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

/**
 * Configuració de Spring Security per a executar l'aplicació amb Spring Boot.
 * 
 * @author Limit Tecnologies
 */
public class BaseWebSecurityConfig {

	public static final String ROLE_PREFIX = "";
	public static final String LOGOUT_URL = "/logout";
	protected static final String[] AUTH_WHITELIST = {
			"/swagger-resources/**",
			"/swagger-ui/**",
			"/rest",
			"/api/rest",
			"/api/rest/appinfo",
			"/api/rest/notificaCanvi",
			"/api-docs",
			"/api-docs/**",
			"/webjars/**",
			"/adviser",
			"/adviser/**"
	};

	@Bean
	public GrantedAuthorityDefaults grantedAuthorityDefaults() {
		return new GrantedAuthorityDefaults(ROLE_PREFIX);
	}

	@Bean
	public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
	}

}

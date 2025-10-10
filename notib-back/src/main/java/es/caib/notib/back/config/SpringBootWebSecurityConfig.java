package es.caib.notib.back.config;

import es.caib.notib.back.base.config.BaseWebSecurityConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWarDeployment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static es.caib.notib.back.base.config.MethodSecurityConfig.DEFAULT_ROLE_PREFIX;

/**
 * Configuració de Spring Security per a executar l'aplicació amb Spring Boot.
 * 
 * @author Limit Tecnologies
 */
@Slf4j
@Configuration
@ConditionalOnNotWarDeployment
public class SpringBootWebSecurityConfig extends BaseWebSecurityConfig {

	public static final String LOGOUT_URL = "/logout";

	@Value("${es.caib.notib.security.mappableRoles:NOT_SUPER,NOT_ADMIN,NOT_CARPETA,NOT_APL,tothom}")
	protected String mappableRoles;

	@Bean
	public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
		DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
		handler.setDefaultRolePrefix(DEFAULT_ROLE_PREFIX);
		return handler;
	}

	@Override
	protected void customHttpSecurityConfiguration(HttpSecurity http) throws Exception {
		http.logout().
				invalidateHttpSession(true).
				clearAuthentication(true).
				deleteCookies("OAuth_Token_Request_State", "JSESSIONID").
				//addLogoutHandler(oauth2LogoutHandler()).
				logoutUrl(LOGOUT_URL).
				logoutSuccessUrl("/");
		http.authorizeHttpRequests().
				requestMatchers(publicRequestMatchers()).permitAll();
		super.customHttpSecurityConfiguration(http);
	}

	protected RequestMatcher[] publicRequestMatchers() {
		return new RequestMatcher[] {
				new AntPathRequestMatcher("/swagger-resources/**"),
				new AntPathRequestMatcher("/swagger-ui/**"),
				new AntPathRequestMatcher("/api/rest"),
				new AntPathRequestMatcher("/api/rest/**/*"),
				new AntPathRequestMatcher("/api-docs"),
				new AntPathRequestMatcher("/api-docs/**/*"),
				new AntPathRequestMatcher("/css/**/*"),
				new AntPathRequestMatcher("/fonts/**/*"),
				new AntPathRequestMatcher("/img/**/*"),
				new AntPathRequestMatcher("/js/**/*"),
				new AntPathRequestMatcher("/webjars/**"),
		};
	}

	@Override
	protected boolean isOauth2ResourceServerActive() {
		return false;
	}
	@Override
	protected boolean isOauth2ClientActive() {
		return true;
	}

	@Override
	protected List<GrantedAuthority> getAllowedRoles() {
		return Arrays.stream(mappableRoles.split(",")).
				map(r -> new SimpleGrantedAuthority(r.trim())).
				collect(Collectors.toList());
	}

}

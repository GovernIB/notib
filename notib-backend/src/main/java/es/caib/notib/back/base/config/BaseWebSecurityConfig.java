package es.caib.notib.back.base.config;

import es.caib.notib.logic.intf.base.config.BaseBootConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService;
import org.springframework.security.web.authentication.preauth.j2ee.J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.j2ee.J2eePreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuració de Spring Security.
 * 
 * @author Límit Tecnologies
 */
@EnableWebSecurity
@RequiredArgsConstructor
public abstract class BaseWebSecurityConfig {

	protected final JwtAuthConverter jwtAuthConverter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		if (isWebContainerAuthActive()) {
			http.addFilterBefore(
					webContainerProcessingFilter(),
					BasicAuthenticationFilter.class);
		}
		if (isBearerTokenAuthActive() && jwtAuthConverter != null) {
			http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthConverter);
		}
		var auth = http.authorizeHttpRequests()
				.requestMatchers(internalRequestMatchers()).permitAll()
				.requestMatchers(publicRequestMatchers()).permitAll()
				.requestMatchers(privateRequestMatchers()).authenticated();
		if (isPermitAllRequestsByDefault()) {
			auth.anyRequest().permitAll();
		} else {
			auth.anyRequest().denyAll();
		}
		customHttpSecurityConfiguration(http);
		return http.build();
	}

	@Bean
	public J2eePreAuthenticatedProcessingFilter webContainerProcessingFilter() {
		J2eePreAuthenticatedProcessingFilter preAuthenticatedProcessingFilter = new J2eePreAuthenticatedProcessingFilter();
		preAuthenticatedProcessingFilter.setAuthenticationDetailsSource(getPreauthFilterAuthenticationDetailsSource());
		final List<AuthenticationProvider> providers = new ArrayList<>(1);
		PreAuthenticatedAuthenticationProvider preauthAuthProvider = new PreAuthenticatedAuthenticationProvider();
		preauthAuthProvider.setPreAuthenticatedUserDetailsService(getPreauthAuthenticationUserDetailsService());
		providers.add(preauthAuthProvider);
		preAuthenticatedProcessingFilter.setAuthenticationManager(new ProviderManager(providers));
		preAuthenticatedProcessingFilter.setContinueFilterChainOnUnsuccessfulAuthentication(false);
		return preAuthenticatedProcessingFilter;
	}

	protected boolean isWebContainerAuthActive() {
		return false;
	}
	protected boolean isBearerTokenAuthActive() {
		return true;
	}

	protected RequestMatcher[] internalRequestMatchers() {
		return new RequestMatcher[] {
				new AntPathRequestMatcher("/"),
				new AntPathRequestMatcher("/apidocs"),
				new AntPathRequestMatcher("/apidocs/*"),
				new AntPathRequestMatcher("/swagger-ui/*"),
				new AntPathRequestMatcher(BaseBootConfig.API_PATH),
				new AntPathRequestMatcher(BaseBootConfig.PING_PATH),
				new AntPathRequestMatcher(BaseBootConfig.SYSENV_PATH),
				new AntPathRequestMatcher(BaseBootConfig.MANIFEST_PATH)
		};
	}

	protected abstract RequestMatcher[] publicRequestMatchers();
	protected abstract RequestMatcher[] privateRequestMatchers();

	protected void customHttpSecurityConfiguration(HttpSecurity http) throws Exception {
		http.cors();
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
	}

	protected boolean isPermitAllRequestsByDefault() {
		return true;
	}

	protected AuthenticationDetailsSource<HttpServletRequest, ?> getPreauthFilterAuthenticationDetailsSource() {
		return new J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource();
	}

	protected AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> getPreauthAuthenticationUserDetailsService() {
		return new PreAuthenticatedGrantedAuthoritiesUserDetailsService();
	}

}

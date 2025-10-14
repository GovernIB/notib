package es.caib.notib.back.config;

import es.caib.notib.back.base.config.BaseWebSecurityConfig;
import es.caib.notib.back.base.config.MethodSecurityConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAttributes2GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleMappableAttributesRetriever;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.j2ee.J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Configuració de Spring Security per a executar l'aplicació amb Spring Boot.
 * 
 * @author Limit Tecnologies
 */
@Slf4j
@Configuration
public class WebSecurityConfig extends BaseWebSecurityConfig {

	public static final String LOGOUT_URL = "/logout";

	@Value("${es.caib.notib.security.mappableRoles:" +
			BaseConfig.ROLE_SUPER + "," +
			BaseConfig.ROLE_ADMIN + "," +
			BaseConfig.ROLE_CARPETA + "," +
			BaseConfig.ROLE_APL + "," +
			BaseConfig.ROLE_TOTHOM + "}")
	protected String mappableRoles;

	@Bean
	public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
		DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
		handler.setDefaultRolePrefix(MethodSecurityConfig.DEFAULT_ROLE_PREFIX);
		return handler;
	}

	@Override
	protected void customHttpSecurityConfiguration(HttpSecurity http) throws Exception {
		LogoutHandler logoutHandler = (request, response, authentication) -> {
			try {
				log.info("Logout called");
				Cookie[] cookies = request.getCookies();
				if (cookies != null) {
					for (Cookie cookie: cookies) {
						Cookie deletedCookie = new Cookie(cookie.getName(), "");
						deletedCookie.setPath(cookie.getPath() != null ? cookie.getPath() : "/");
						deletedCookie.setMaxAge(0);
						deletedCookie.setHttpOnly(cookie.isHttpOnly());
						deletedCookie.setSecure(cookie.getSecure());
						response.addCookie(deletedCookie);
					}
				}
				request.logout();
			} catch (ServletException ex) {
				log.error("Error en el logout", ex);
			}
		};
		http.logout(lo -> lo.addLogoutHandler(logoutHandler).
				logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_URL)).
				invalidateHttpSession(true).
				clearAuthentication(true).
				deleteCookies("OAuth_Token_Request_State", "JSESSIONID").
				logoutSuccessUrl("/"));
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
	protected boolean isWebContainerAuthActive() {
		return isJboss();
	}
	@Override
	protected boolean isOauth2ResourceServerActive() {
		return false;
	}
	@Override
	protected boolean isOauth2ClientActive() {
		return !isJboss();
	}

	@Override
	protected List<GrantedAuthority> getAllowedRoles() {
		return Arrays.stream(mappableRoles.split(",")).
				map(r -> new SimpleGrantedAuthority(r.trim())).
				collect(Collectors.toList());
	}

	@Value("${jboss.home.dir:#{null}}")
	private String jbossHomeDir;
	private boolean isJboss() {
		return jbossHomeDir != null;
	}

	@Override
	protected AuthenticationDetailsSource<HttpServletRequest, ?> getPreauthFilterAuthenticationDetailsSource() {
		J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource authenticationDetailsSource = new J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource() {
			@Override
			public PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails buildDetails(HttpServletRequest context) {
				Collection<String> j2eeUserRoles = getUserRoles(context);
				logger.debug("Roles from ServletRequest for " + context.getUserPrincipal().getName() + ": " + j2eeUserRoles);
				PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails result;
				if (context.getUserPrincipal() instanceof KeycloakPrincipal) {
					KeycloakPrincipal<?> keycloakPrincipal = ((KeycloakPrincipal<?>)context.getUserPrincipal());
					Set<String> roles = new HashSet<>(j2eeUserRoles);
					AccessToken.Access realmAccess = keycloakPrincipal.getKeycloakSecurityContext().getToken().getRealmAccess();
					if (realmAccess != null && realmAccess.getRoles() != null) {
						logger.debug("Keycloak token realm roles: " + realmAccess.getRoles());
						realmAccess.getRoles().stream().
								map(r -> MethodSecurityConfig.DEFAULT_ROLE_PREFIX + r).
								forEach(roles::add);
					}
					logger.debug("Creating WebAuthenticationDetails for " + keycloakPrincipal.getName() + " with roles " + roles);
					result = new PreauthWebAuthenticationDetails(
							context,
							j2eeUserRoles2GrantedAuthoritiesMapper.getGrantedAuthorities(roles),
							keycloakPrincipal.getKeycloakSecurityContext().getIdTokenString());
				} else {
					logger.debug("Creating WebAuthenticationDetails for " + context.getUserPrincipal().getName() + " with roles " + j2eeUserRoles);
					result = new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(
							context,
							j2eeUserRoles2GrantedAuthoritiesMapper.getGrantedAuthorities(j2eeUserRoles));
				}
				return result;
			}
		};
		SimpleMappableAttributesRetriever mappableAttributesRetriever = new SimpleMappableAttributesRetriever();
		mappableAttributesRetriever.setMappableAttributes(new HashSet<>(Arrays.asList(mappableRoles.split(","))));
		authenticationDetailsSource.setMappableRolesRetriever(mappableAttributesRetriever);
		SimpleAttributes2GrantedAuthoritiesMapper attributes2GrantedAuthoritiesMapper = new SimpleAttributes2GrantedAuthoritiesMapper();
		attributes2GrantedAuthoritiesMapper.setAttributePrefix(MethodSecurityConfig.DEFAULT_ROLE_PREFIX);
		authenticationDetailsSource.setUserRoles2GrantedAuthoritiesMapper(attributes2GrantedAuthoritiesMapper);
		return authenticationDetailsSource;
	}

	@Getter
	public static class PreauthWebAuthenticationDetails extends PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails {
		private final String jwtToken;
		public PreauthWebAuthenticationDetails(
				HttpServletRequest request,
				Collection<? extends GrantedAuthority> authorities,
				String jwtToken) {
			super(request, authorities);
			this.jwtToken = jwtToken;
		}
	}

}

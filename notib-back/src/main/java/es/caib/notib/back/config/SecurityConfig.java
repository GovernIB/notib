/**
 * 
 */
package es.caib.notib.back.config;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.AccessToken.Access;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAttributes2GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleMappableAttributesRetriever;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.j2ee.J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.j2ee.J2eePreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Configuració de seguretat.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Profile("!boot")
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Value("${es.caib.notib.security.mappableRoles:NOT_SUPER,NOT_ADMIN,NOT_CARPETA,NOT_APL,tothom}")
	private String mappableRoles;
	@Value("${es.caib.notib.security.useResourceRoleMappings:false}")
	private boolean useResourceRoleMappings;

	private static final String ROLE_PREFIX = "";

	private static final String[] AUTH_WHITELIST = {
			"/swagger-resources/**",
			"/swagger-ui/**",
			"/api/rest",
			"/api-docs",
			"/webjars/**"
	};

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.cors(withDefaults())
				.csrf(csrf -> csrf.disable())
				.addFilterBefore(preAuthenticatedProcessingFilter(), BasicAuthenticationFilter.class)
				.authenticationProvider(preauthAuthProvider())
				.logout(lo -> lo.addLogoutHandler(getLogoutHandler())
						.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
						.invalidateHttpSession(true).logoutSuccessUrl("/")
						.permitAll(false))
				.authorizeRequests(authz -> authz.antMatchers(AUTH_WHITELIST)
						.permitAll()
						.anyRequest().authenticated())
				.headers(hd -> hd.frameOptions().disable())
				.build();
	}

	@Bean
	protected AuthenticationManager authenticationManager() throws Exception {
		final List<AuthenticationProvider> providers = new ArrayList<>(1);
		providers.add(preauthAuthProvider());
		return new ProviderManager(providers);
	}

	@Bean
	public PreAuthenticatedAuthenticationProvider preauthAuthProvider() {
		PreAuthenticatedAuthenticationProvider preauthAuthProvider = new PreAuthenticatedAuthenticationProvider();
		preauthAuthProvider.setPreAuthenticatedUserDetailsService(
				preAuthenticatedGrantedAuthoritiesUserDetailsService());
		return preauthAuthProvider;
	}

	@Bean
	public GrantedAuthorityDefaults grantedAuthorityDefaults() {
		return new GrantedAuthorityDefaults(ROLE_PREFIX);
	}

	@Bean
	public PreAuthenticatedGrantedAuthoritiesUserDetailsService preAuthenticatedGrantedAuthoritiesUserDetailsService() {
		return new PreAuthenticatedGrantedAuthoritiesUserDetailsService() {
			protected UserDetails createUserDetails(
					Authentication token,
					Collection<? extends GrantedAuthority> authorities) {
				if (token.getDetails() instanceof KeycloakWebAuthenticationDetails) {
					KeycloakWebAuthenticationDetails keycloakWebAuthenticationDetails = (KeycloakWebAuthenticationDetails)token.getDetails();
					return new PreauthKeycloakUserDetails(
							token.getName(),
							"N/A",
							true,
							true,
							true,
							true,
							authorities,
							keycloakWebAuthenticationDetails.getKeycloakPrincipal());
				} else {
					return new User(token.getName(), "N/A", true, true, true, true, authorities);
				}
			}
		};
	}

	@Bean
	public J2eePreAuthenticatedProcessingFilter preAuthenticatedProcessingFilter() throws Exception {
		J2eePreAuthenticatedProcessingFilter preAuthenticatedProcessingFilter = new J2eePreAuthenticatedProcessingFilter();
		preAuthenticatedProcessingFilter.setAuthenticationDetailsSource(authenticationDetailsSource());
		preAuthenticatedProcessingFilter.setAuthenticationManager(authenticationManager());
		preAuthenticatedProcessingFilter.setContinueFilterChainOnUnsuccessfulAuthentication(false);
		return preAuthenticatedProcessingFilter;
	}

	@Bean
	public AuthenticationDetailsSource<HttpServletRequest, PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails> authenticationDetailsSource() {
		J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource authenticationDetailsSource = new J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource() {
			@Override
			public PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails buildDetails(HttpServletRequest context) {
				Collection<String> j2eeUserRoles = getUserRoles(context);
				// Afegit el rol tothom a qualsevol usuari autenticat per a mantenir el sistema de menus
				// amb usuari i administrador d'òrgan, sense necessitat de canviar-ho
				if (!j2eeUserRoles.contains("tothom")) {
					j2eeUserRoles.add("tothom");
				}
				logger.debug("Roles from ServletRequest for " + context.getUserPrincipal().getName() + ": " + j2eeUserRoles);
				PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails result;
				if (context.getUserPrincipal() instanceof KeycloakPrincipal) {
					KeycloakPrincipal<?> keycloakPrincipal = ((KeycloakPrincipal<?>)context.getUserPrincipal());
					Set<String> roles = new HashSet<>();
					roles.addAll(j2eeUserRoles);
					Access realmAccess = keycloakPrincipal.getKeycloakSecurityContext().getToken().getRealmAccess();
					if (realmAccess != null && realmAccess.getRoles() != null) {
						logger.debug("Keycloak token realm roles: " + realmAccess.getRoles());
						roles.addAll(realmAccess.getRoles());
					}
					if (useResourceRoleMappings) {
						Access resourceAccess = keycloakPrincipal.getKeycloakSecurityContext().getToken().getResourceAccess(
								keycloakPrincipal.getKeycloakSecurityContext().getToken().getIssuedFor());
						if (resourceAccess != null && resourceAccess.getRoles() != null) {
							logger.debug("Keycloak token resource roles: " + resourceAccess.getRoles());
							roles.addAll(resourceAccess.getRoles());
						}
					}
					logger.debug("Creating WebAuthenticationDetails for " + keycloakPrincipal.getName() + " with roles " + roles);
					result = new KeycloakWebAuthenticationDetails(
							context,
							j2eeUserRoles2GrantedAuthoritiesMapper.getGrantedAuthorities(roles),
							keycloakPrincipal);
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
		attributes2GrantedAuthoritiesMapper.setAttributePrefix(ROLE_PREFIX);
		authenticationDetailsSource.setUserRoles2GrantedAuthoritiesMapper(attributes2GrantedAuthoritiesMapper);
		return authenticationDetailsSource;
	}

	@Bean
	public LogoutHandler getLogoutHandler() {
		return (request, response, authentication) -> {
			try {
				request.logout();
			} catch (ServletException ex) {
				log.error("Error al sortir de l'aplicació", ex);
			}
		};
	}

	@SuppressWarnings("serial")
	public static class KeycloakWebAuthenticationDetails extends PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails {
		private KeycloakPrincipal<?> keycloakPrincipal;
		public KeycloakWebAuthenticationDetails(
				HttpServletRequest request,
				Collection<? extends GrantedAuthority> authorities,
				KeycloakPrincipal<?> keycloakPrincipal) {
			super(request, authorities);
			this.keycloakPrincipal = keycloakPrincipal;
		}
		public KeycloakPrincipal<?> getKeycloakPrincipal() {
			return keycloakPrincipal;
		}
	}
	@SuppressWarnings("serial")
	public static class PreauthKeycloakUserDetails extends User implements es.caib.notib.logic.intf.keycloak.KeycloakUserDetails {
		private KeycloakPrincipal<?> keycloakPrincipal;
		public PreauthKeycloakUserDetails(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired,
										  boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, KeycloakPrincipal<?> keycloakPrincipal) {

			super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
			this.keycloakPrincipal = keycloakPrincipal;
		}

		public KeycloakPrincipal<?> getKeycloakPrincipal() {
			return keycloakPrincipal;
		}

		public String getGivenName() {
			return keycloakPrincipal instanceof KeycloakPrincipal  ? keycloakPrincipal.getKeycloakSecurityContext().getToken().getGivenName() : null;
		}

		public String getFamilyName() {
			return keycloakPrincipal instanceof KeycloakPrincipal ? keycloakPrincipal.getKeycloakSecurityContext().getToken().getFamilyName() : null;
		}

		public String getFullName() {
			return keycloakPrincipal instanceof KeycloakPrincipal ? keycloakPrincipal.getKeycloakSecurityContext().getToken().getName() : null;
		}

		public String getEmail() {
			return keycloakPrincipal instanceof KeycloakPrincipal ? keycloakPrincipal.getKeycloakSecurityContext().getToken().getEmail() : null;
		}
	}

	@Bean
	public BuildProperties buildProperties() {
		return new BuildProperties(new Properties());
	}

}

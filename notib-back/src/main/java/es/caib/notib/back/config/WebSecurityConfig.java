package es.caib.notib.back.config;

import es.caib.notib.back.base.config.BaseWebSecurityConfig;
import es.caib.notib.back.base.config.MethodSecurityConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.util.HttpRequestUtil;
import es.caib.notib.logic.intf.model.auth.NotibAuthenticationDetails;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAttributes2GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleMappableAttributesRetriever;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.j2ee.J2eeBasedPreAuthenticatedWebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
			BaseConfig.ROLE_ADMIN_LECTURA + "," +
			BaseConfig.ROLE_ORGAN + "," +
			BaseConfig.ROLE_CARPETA + "," +
			BaseConfig.ROLE_APL + "," +
			BaseConfig.ROLE_USER + "}")
	protected String mappableRoles;
	@Value("${" + BaseConfig.PROP_SECURITY_ROLE_HTTP_HEADER + ":X-App-Role}")
	private String selectedRoleHttpHeader;
	@Value("${" + BaseConfig.PROP_SECURITY_NAME_ATTRIBUTE_KEY + ":preferred_username}")
	private String nameAttributeKey;

	@Autowired(required = false)
	private ClientRegistrationRepository clientRegistrationRepository;

	// Mateixes variables d'entorn amb les que l'adaptador Keycloak de JBoss es configura al subsystem
	// "urn:jboss:domain:keycloak:1.1" de standalone.xml (secure-deployment "notib-back.war"): no hi ha
	// cap manera d'obtenir aquesta informació directament d'aquell adaptador des de codi de l'aplicació.
	@Value("${JBOSS_AUTH_URL:#{null}}")
	private String jbossAuthUrl;
	@Value("${JBOSS_AUTH_REALM:#{null}}")
	private String jbossAuthRealm;
	@Value("${JBOSS_AUTH_CLIENTID:#{null}}")
	private String jbossAuthClientId;

	@Override
	protected void customHttpSecurityConfiguration(HttpSecurity http) throws Exception {
		if (!isJboss()) {
			LogoutHandler deleteCookiesLogoutHandler = (request, response, authentication) -> {
				try {
					log.info("Logout called");
					Cookie[] cookies = request.getCookies();
					if (cookies != null) {
						for (Cookie cookie : cookies) {
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
			OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(
				clientRegistrationRepository);
			oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/");
			http.logout(lo -> lo.
				addLogoutHandler(deleteCookiesLogoutHandler).
				logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_URL)).
				invalidateHttpSession(true).
				clearAuthentication(true).
				deleteCookies("OAuth_Token_Request_State", "JSESSIONID").
				logoutSuccessHandler(oidcLogoutSuccessHandler).
				logoutSuccessUrl("/"));
		} else {
			// Sense aquesta configuració explícita, LOGOUT_URL ("/logout") encara queda gestionat pel
			// LogoutFilter que Spring Security afegeix per defecte (mai s'ha desactivat amb
			// logout(AbstractHttpConfigurer::disable)), però amb els seus valors per defecte:
			// url_success="/login?logout", sense tocar per res la sessió SSO de Keycloak. Com que
			// aquesta aplicació no té cap "/login" propi, l'usuari acaba a una pàgina 404 i, sobretot,
			// la sessió SSO de Keycloak queda viva -en tornar a entrar-hi es reautentica en silenci amb
			// el mateix usuari, sense demanar credencials.
			http.logout(lo -> lo.
				logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_URL)).
				logoutSuccessHandler(jbossKeycloakLogoutSuccessHandler()));
		}
		http.authorizeHttpRequests().
				requestMatchers(publicRequestMatchers()).permitAll();
		if (!isJboss()) {
			http.sessionManagement(session -> session
							.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
							.invalidSessionUrl("/"));
		}
		super.customHttpSecurityConfiguration(http);
	}

	/**
	 * Tanca la sessió SSO de Keycloak per a l'adaptador Keycloak de JBoss/Undertow. A diferència del mode
	 * Spring Boot (OidcClientInitiatedLogoutSuccessHandler), aquí no hi ha cap OidcUser ni
	 * ClientRegistration a l'Authentication -l'autenticació ve del contenidor (KeycloakPrincipal), no
	 * d'un login OAuth2 de Spring- de manera que cal construir i redirigir manualment a l'"end session
	 * endpoint" de Keycloak.
	 *
	 * @return el LogoutSuccessHandler a fer servir sota JBoss.
	 */
	private LogoutSuccessHandler jbossKeycloakLogoutSuccessHandler() {

		return (request, response, authentication) -> {

			// Cal llegir l'id_token ABANS d'invalidar la sessió (Keycloak >= 18 exigeix "id_token_hint"
			// per fer el logout sense demanar confirmació a l'usuari), i sobretot NO criar
			// request.logout(): a l'adaptador Keycloak d'Undertow/WildFly això fa una petició
			// backchannel REAL a Keycloak (amb el refresh_token) que tanca la sessió SSO immediatament,
			// abans que el redirect explícit de sota hi arribi mai. Com que aquesta petició backchannel
			// mai interactua amb el navegador, la cookie de sessió SSO de Keycloak (al domini de l'IdP)
			// no s'arriba a esborrar mai per aquesta via: el redirect posterior a l'"end session
			// endpoint" rep "Session not active" (Keycloak ja no la troba, l'acaba de matar
			// request.logout()) en lloc d'executar el camí d'èxit -l'únic que sí esborra aquesta cookie.
			// Resultat: l'usuari, en tornar a entrar-hi, hi torna a entrar en silenci amb el mateix
			// usuari (la cookie SSO de Keycloak encara és vàlida). La sessió SSO de Keycloak l'ha de
			// tancar únicament el redirect explícit de sota, executant-se contra una sessió que encara
			// és viva.
			var idTokenHint = getKeycloakIdTokenHint(request);
			var session = request.getSession(false);
			if (session != null) {
				try {
					session.invalidate();
				} catch (IllegalStateException ex) {
					// Ja invalidada; res a fer.
				}
			}
			if (jbossAuthUrl == null || jbossAuthRealm == null) {
				response.sendRedirect(request.getContextPath() + "/");
				return;
			}
			var authUrlSensePrefix = jbossAuthUrl.endsWith("/") ? jbossAuthUrl.substring(0, jbossAuthUrl.length() - 1) : jbossAuthUrl;
			var postLogoutRedirectUri = getBaseUrl(request) + request.getContextPath() + "/";
			var logoutUrl = new StringBuilder(authUrlSensePrefix).
					append("/realms/").append(jbossAuthRealm).append("/protocol/openid-connect/logout").
					append("?post_logout_redirect_uri=").append(URLEncoder.encode(postLogoutRedirectUri, StandardCharsets.UTF_8));
			if (idTokenHint != null) {
				logoutUrl.append("&id_token_hint=").append(URLEncoder.encode(idTokenHint, StandardCharsets.UTF_8));
			}
			if (jbossAuthClientId != null) {
				logoutUrl.append("&client_id=").append(URLEncoder.encode(jbossAuthClientId, StandardCharsets.UTF_8));
			}
			response.sendRedirect(logoutUrl.toString());
		};
	}

	private static String getKeycloakIdTokenHint(HttpServletRequest request) {

		var keycloakSecurityContext = request.getAttribute(KeycloakSecurityContext.class.getName());
		return keycloakSecurityContext instanceof KeycloakSecurityContext
				? ((KeycloakSecurityContext) keycloakSecurityContext).getIdTokenString()
				: null;
	}

	private static String getBaseUrl(HttpServletRequest request) {

		var port = request.getServerPort();
		var portSuffix = (port == 80 || port == 443) ? "" : ":" + port;
		return request.getScheme() + "://" + request.getServerName() + portSuffix;
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
		return !isJboss();
	}
	@Override
	protected boolean isOidcClientActive() {
		return !isJboss();
	}

	@Override
	protected Set<String> getAllowedRoles() {

		Optional<HttpServletRequest> optionalRequest = HttpRequestUtil.getCurrentHttpRequest();
		Set<String> allowedRoles = Set.of(mappableRoles.split(","));
		if (optionalRequest.isPresent()) {
			// Si la petició HTTP conté la capçalera amb el rol seleccionat retorna únicament aquest rol en la llista de rols permesos.
			HttpServletRequest request = optionalRequest.get();
			String selectedRole = request.getHeader(selectedRoleHttpHeader);
			if (selectedRole != null) {
				HashSet<String> editableAllowedRoles = new HashSet<>(allowedRoles);
				editableAllowedRoles.removeIf(s -> !s.equals(selectedRole));
				return editableAllowedRoles;
			}
		}
		return allowedRoles;
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
					IDToken idToken = keycloakPrincipal.getKeycloakSecurityContext().getIdToken();
					String preferredUsername = nameAttributeKey.equals("preferred_username") ?
							idToken.getPreferredUsername() :
							(String)idToken.getOtherClaims().get(nameAttributeKey);
					applyPermisBasedRoles(preferredUsername, roles);
					Collection<? extends GrantedAuthority> grantedAuthorities = j2eeUserRoles2GrantedAuthoritiesMapper.
							getGrantedAuthorities(roles);
					filterAllowedGrantedAuthorities(new HashSet<>(grantedAuthorities));
					result = new PreauthWebAuthenticationDetails(
							context,
							j2eeUserRoles2GrantedAuthoritiesMapper.getGrantedAuthorities(roles),
							// Access token, no id token: veure el comentari a ReactController.getAuthToken()
							// -en un refresc, Keycloak actualitza sempre l'access token de la sessió, però
							// no sempre reemet un id token nou, cosa que deixava aquest camp (usat només com
							// a fallback quan encara no hi ha l'atribut de petició KeycloakSecurityContext)
							// congelat amb el valor del primer login.
							keycloakPrincipal.getKeycloakSecurityContext().getTokenString(),
							preferredUsername,
							idToken.getName(),
							idToken.getEmail(),
							(String)idToken.getOtherClaims().get("nif"),
							roles.toArray(new String[0]));
				} else {
					Collection<? extends GrantedAuthority> grantedAuthorities = j2eeUserRoles2GrantedAuthoritiesMapper.
							getGrantedAuthorities(j2eeUserRoles);
					filterAllowedGrantedAuthorities(new HashSet<>(grantedAuthorities));
					result = new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(
							context,
							grantedAuthorities);
				}
				log.debug("Created WebAuthenticationDetails for {} with roles {}",
						context.getUserPrincipal().getName(),
						result.getGrantedAuthorities());
				return result;
			}
		};
		SimpleMappableAttributesRetriever mappableAttributesRetriever = new SimpleMappableAttributesRetriever();
		mappableAttributesRetriever.setMappableAttributes(getAllowedRoles());
		authenticationDetailsSource.setMappableRolesRetriever(mappableAttributesRetriever);
		SimpleAttributes2GrantedAuthoritiesMapper attributes2GrantedAuthoritiesMapper = new SimpleAttributes2GrantedAuthoritiesMapper();
		attributes2GrantedAuthoritiesMapper.setAttributePrefix(MethodSecurityConfig.DEFAULT_ROLE_PREFIX);
		authenticationDetailsSource.setUserRoles2GrantedAuthoritiesMapper(attributes2GrantedAuthoritiesMapper);
		return authenticationDetailsSource;
	}

	@Getter
	public static class PreauthWebAuthenticationDetails
		extends PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails
		implements NotibAuthenticationDetails {
		private final String jwtToken;
		private final String preferredUsername;
		private final String name;
		private final String email;
		private final String nif;
		private final String[] originalRoles;
		public PreauthWebAuthenticationDetails(
				HttpServletRequest request,
				Collection<? extends GrantedAuthority> authorities,
				String jwtToken,
				String preferredUsername,
				String name,
				String email,
				String nif,
				String[] originalRoles) {
			super(request, authorities);
			this.jwtToken = jwtToken;
			this.preferredUsername = preferredUsername;
			this.name = name;
			this.email = email;
			this.nif = nif;
			this.originalRoles = originalRoles;
		}
	}

}

package es.caib.notib.back.controller;

import es.caib.notib.back.base.controller.BaseUtilsController;
import es.caib.notib.back.config.WebSecurityConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.config.PropertyConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.security.Principal;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ReactController extends BaseUtilsController {

	private final ServletContext servletContext;

	@RequestMapping(BaseConfig.REACT_APP_PATH + "/**")
	public ResponseEntity<?> serveReact(HttpServletRequest request, HttpServletResponse response) {
		String path = request.getRequestURI().replaceFirst(request.getContextPath(), "");
		try {
			// Intentem obrir el recurs
			InputStream resource = servletContext.getResourceAsStream(path);
			if (resource != null) {
				// Serveix el fitxer si existeix
				String mimeType = servletContext.getMimeType(path);
				MediaType mediaType = mimeType != null ? MediaType.parseMediaType(mimeType) : MediaType.APPLICATION_OCTET_STREAM;
				return ResponseEntity
					.ok()
					.contentType(mediaType)
					.body(new InputStreamResource(resource));
			}
			// Si no existeix el fitxer, i és un recurs estàtic retornam un NOT FOUND
			String uri = request.getRequestURI();
			if (uri.matches(".*\\.(js|css|ico|png|jpg|svg|woff2?|map)$") || uri.endsWith("index.html")) {
				return ResponseEntity.notFound().build();
			}
			// En cas contrari, retornem index.html
			InputStream indexHtml = servletContext.getResourceAsStream(BaseConfig.REACT_APP_PATH + "/index.html");
			return ResponseEntity
				.ok()
				.contentType(MediaType.TEXT_HTML)
				.body(new InputStreamResource(Objects.requireNonNull(indexHtml)));
		} catch (Exception ex) {
			log.error("Error carregant recurs", ex);
			return ResponseEntity.internalServerError().body("Error carregant recurs");
		}
	}

	@Override
	protected String getAuthToken() {
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attrs == null) {
			throw new IllegalStateException("No current request attributes found");
		}
		HttpServletRequest request = attrs.getRequest();
		// L'adaptador de Keycloak de JBoss deixa el KeycloakSecurityContext com a atribut de la petició
		// a CADA petició. El llegim d'aquí en lloc de request.getUserPrincipal(): un cop la sessió ja
		// està autenticada, Spring Security substitueix getUserPrincipal() pel
		// PreAuthenticatedAuthenticationToken que va quedar en cache la primera vegada
		// (J2eePreAuthenticatedProcessingFilter no torna a construir els details mentre la sessió ja
		// estigui autenticada), de manera que el token que hi guarda quedava congelat amb el del primer
		// login.
		//
		// Usam l'ACCESS token (getTokenString()), no l'ID token (getIdTokenString()): a
		// RefreshableKeycloakSecurityContext.refreshExpiredToken(), un refresc reeixit actualitza SEMPRE
		// l'access token, però només actualitza l'id token "if (idToken != null)" -és a dir, només si
		// Keycloak n'inclou un de nou a la resposta del refresc, cosa que en aquest entorn no fa mai
		// (verificat als logs: refreshExpiredToken() retornava true -èxit- però l'id token no canviava
		// mai). Per això l'id token quedava sempre caducat i el checkActive() intern (que es basa
		// només en l'access token) no detectava mai que calia renovar-lo: exactament la causa del bucle
		// de peticions cada 5 segons.
		Object keycloakSecurityContext = request.getAttribute(KeycloakSecurityContext.class.getName());
		if (keycloakSecurityContext instanceof RefreshableKeycloakSecurityContext) {
			RefreshableKeycloakSecurityContext ctx = (RefreshableKeycloakSecurityContext) keycloakSecurityContext;
			// getTokenString() ja renova internament (refreshExpiredToken(true)) si cal: ara la
			// comprovació ("és actiu l'access token?") i el token que retornam són el mateix, així que
			// aquest mecanisme intern ja funciona correctament sense necessitat de forçar res nosaltres.
			return ctx.getTokenString();
		}
		// Fallback pel cas (p.ex. la primera petició de la sessió, o un KeycloakSecurityContext no
		// renovable) en què l'atribut de la petició encara no hi sigui; manté el comportament anterior.
		Principal principal = request.getUserPrincipal();
		if (principal instanceof PreAuthenticatedAuthenticationToken) {
			PreAuthenticatedAuthenticationToken token = (PreAuthenticatedAuthenticationToken) principal;
			if (token.getDetails() instanceof WebSecurityConfig.PreauthWebAuthenticationDetails) {
				WebSecurityConfig.PreauthWebAuthenticationDetails tokenDetails = (WebSecurityConfig.PreauthWebAuthenticationDetails) token.getDetails();
				return tokenDetails.getJwtToken();
			}
		}
		return null;
	}

	@Override
	protected String[] getAuthRoles() {
		return new String[] {
			BaseConfig.ROLE_SUPER,
			BaseConfig.ROLE_ADMIN,
			BaseConfig.ROLE_ADMIN_LECTURA,
			BaseConfig.ROLE_CARPETA,
			BaseConfig.ROLE_ORGAN,
			BaseConfig.ROLE_APL,
			BaseConfig.ROLE_COM,
			BaseConfig.ROLE_USER
		};
	}

	@Override
	protected boolean isReactAppMappedFrontProperty(String propertyName) {
		return propertyName.startsWith(PropertyConfig.PROPERTY_PREFIX_FRONT) && PropertyConfig.REACT_APP_PROPS_MAP.containsKey(propertyName);
	}

	@Override
	protected String getReactAppMappedFrontProperty(String propertyName) {
		return PropertyConfig.REACT_APP_PROPS_MAP.get(propertyName);
	}

	@Override
	protected boolean isViteMappedFrontProperty(String propertyName) {
		return propertyName.startsWith(PropertyConfig.PROPERTY_PREFIX_FRONT) && PropertyConfig.VITE_PROPS_MAP.containsKey(propertyName);
	}

	@Override
	protected String getViteMappedFrontProperty(String propertyName) {
		return PropertyConfig.VITE_PROPS_MAP.get(propertyName);
	}

}

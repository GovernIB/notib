package es.caib.notib.back.controller;

import es.caib.notib.back.base.controller.BaseUtilsController;
import es.caib.notib.back.config.WebSecurityConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.config.PropertyConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
		Principal principal = request.getUserPrincipal();
		if (principal instanceof PreAuthenticatedAuthenticationToken) {
			PreAuthenticatedAuthenticationToken token = ((PreAuthenticatedAuthenticationToken) request.getUserPrincipal());
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

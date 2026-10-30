package es.caib.notib.back.controller;

import es.caib.notib.back.config.WebMvcConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.auth.NotibAuthenticationDetails;
import es.caib.notib.logic.intf.service.AplicacioService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Usuari de la sessió de servidor amb què opera la interfície REACT.
 * <p>
 * La interfície REACT no gestiona cap token al navegador: s'autentica amb la mateixa sessió HTTP
 * que la interfície JSP (adaptador Keycloak de JBoss en mode EAR, {@code oauth2Login} de Spring en
 * mode Spring Boot), com fan RIPEA, DISTRIBUCIO i ConCSV. Aquest endpoint li dona el que abans
 * treia del token i de {@code /authRoles} (codi, nom, correu i rols) i li serveix per a comprovar
 * si la sessió continua viva (veure {@code NotibAuthProvider.tsx}).
 * <p>
 * Els rols són tots els de l'usuari, no només el seleccionat: la petició no du la capçalera del rol
 * seleccionat, així que {@code WebSecurityConfig.getAllowedRoles()} no hi restringeix res. Inclouen
 * els que es concedeixen des de NOTIB (administrador d'òrgan, administrador de lectura, veure
 * {@code BaseWebSecurityConfig.applyPermisBasedRoles}), que a JBoss es tornen a calcular a cada
 * petició.
 * <p>
 * També ofereix {@link #login(String, HttpServletRequest)}, perquè la interfície servida pel
 * servidor de desenvolupament de Vite pugui iniciar la sessió i tornar-hi.
 *
 * @author Límit Tecnologies
 */
@RestController
@RequiredArgsConstructor
public class SessioUsuariController {

	/** Rols que la interfície REACT pot oferir al selector de rol. */
	public static final String[] ROLS_INTERFICIE = new String[] {
			BaseConfig.ROLE_SUPER,
			BaseConfig.ROLE_ADMIN,
			BaseConfig.ROLE_ADMIN_LECTURA,
			BaseConfig.ROLE_CARPETA,
			BaseConfig.ROLE_ORGAN,
			BaseConfig.ROLE_APL,
			BaseConfig.ROLE_COM,
			BaseConfig.ROLE_USER
	};

	private final AplicacioService aplicacioService;

	@GetMapping(BaseConfig.API_PATH + "/sessioUsuari")
	public ResponseEntity<SessioUsuari> sessioUsuari() {

		var auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		Set<String> autoritats = auth.getAuthorities().stream().
				map(GrantedAuthority::getAuthority).
				collect(Collectors.toSet());
		List<String> rols = Arrays.stream(ROLS_INTERFICIE).
				filter(autoritats::contains).
				collect(Collectors.toList());
		// El nom i el correu són només per a mostrar-los: surten del token de Keycloak (a JBoss) i,
		// si no hi són, de not_usuari. L'usuari pot no existir encara a not_usuari (primer accés):
		// sense ells la interfície mostra el codi.
		String nom = null;
		String email = null;
		if (auth.getDetails() instanceof NotibAuthenticationDetails) {
			var details = (NotibAuthenticationDetails) auth.getDetails();
			nom = details.getName();
			email = details.getEmail();
		}
		if (nom == null || email == null) {
			var usuari = aplicacioService.getUsuariActual();
			if (usuari != null) {
				nom = nom != null ? nom : usuari.getNom();
				email = email != null ? email : usuari.getEmail();
			}
		}
		return ResponseEntity.ok(new SessioUsuari(auth.getName(), nom, email, rols));
	}

	/**
	 * Inicia la sessió i torna a la URL indicada.
	 * <p>
	 * No fa res per si mateix: com qualsevol altra URL de l'aplicació fora de l'API, la protegeix el
	 * contenidor, així que si no hi ha sessió el servidor passa pel login (adaptador Keycloak de JBoss
	 * o {@code oauth2Login} de Spring) i hi torna en acabar. Aleshores redirigeix a {@code retorn}.
	 * No és sota {@link BaseConfig#API_PATH} perquè allà una petició sense sessió rep un 401 i no la
	 * redirecció al login.
	 * <p>
	 * Només es torna a URLs del mateix backend o dels orígens permesos a
	 * {@link WebMvcConfig#ORIGENS_CORS}. Qualsevol altra (o cap) porta a l'arrel de la interfície
	 * REACT: així l'endpoint no es pot fer servir per a redirigir a un lloc extern.
	 */
	@GetMapping("/sessioUsuari/login")
	public ResponseEntity<Void> login(
			@RequestParam(required = false) String retorn,
			HttpServletRequest request) {

		var desti = isRetornPermes(retorn, request) ?
				retorn :
				request.getContextPath() + BaseConfig.REACT_APP_PATH + "/";
		return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(desti)).build();
	}

	private boolean isRetornPermes(String retorn, HttpServletRequest request) {

		if (retorn == null || retorn.isBlank()) {
			return false;
		}
		try {
			var uri = new URI(retorn);
			if (!uri.isAbsolute()) {
				// Camí relatiu al mateix servidor; "//host" seria un altre servidor.
				return retorn.startsWith("/") && !retorn.startsWith("//");
			}
			var origen = uri.getScheme() + "://" + uri.getRawAuthority();
			var origenPropi = request.getScheme() + "://" + request.getServerName() +
					(request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort());
			return origen.equalsIgnoreCase(origenPropi) || Arrays.asList(WebMvcConfig.ORIGENS_CORS).contains(origen);
		} catch (URISyntaxException ex) {
			return false;
		}
	}

	@Value
	public static class SessioUsuari {
		String codi;
		String nom;
		String email;
		List<String> rols;
	}

}

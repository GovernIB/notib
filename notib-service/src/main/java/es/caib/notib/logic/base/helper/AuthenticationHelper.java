package es.caib.notib.logic.base.helper;

import es.caib.notib.logic.intf.base.util.HttpRequestUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Mètodes per a interactuar amb l'usuari autenticat.
 *
 * @author Límit Tecnologies
 */
@Component
public class AuthenticationHelper {

	/**
	 * Retorna el nom de l'usuari actual.
	 *
	 * @return el nom de l'usuari actual.
	 */
	public String getCurrentUserName() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	/**
	 * Retorna la llista de rols de l'usuari actual.
	 *
	 * @return la llista de rols.
	 */
	public String[] getCurrentUserRoles() {

		var auth = SecurityContextHolder.getContext().getAuthentication();
		return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);
	}

	/**
	 * Retorna true si l'usuari actual te el rol especificat.
	 *
	 * @param role el rol a verificar.
	 * @return true si l'usuari actual te el rol especificat i false en cas contrari.
	 */
	public boolean isCurrentUserInRole(String role) {

		var auth = SecurityContextHolder.getContext().getAuthentication();

		boolean result = auth.getAuthorities()
			.stream()
			.anyMatch(ga -> ga.getAuthority().equals(role));


		return result;
	}


	/**
	 * Retorna true si l'usuari de l'objecte d'autenticació te el rol especificat.
	 *
	 * @param auth l'objecte d'autenticació.
	 * @param role el rol a verificar.
	 * @return true si l'usuari actual te el rol especificat i false en cas contrari.
	 */
	public boolean isCurrentUserInRole(Authentication auth, String role) {

		var isInRole = false;
		for (var ga: auth.getAuthorities()) {
			if (ga != null && ga.getAuthority().equals(role)) {
				isInRole = true;
				break;
			}
		}
		return isInRole;
	}

}

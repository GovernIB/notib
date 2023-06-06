/**
 * 
 */
package es.caib.notib.ejb.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.ejb.SessionContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Helper per a operacions amb usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class UsuariAuthHelper {

	public Authentication generarUsuariAutenticatEjb(SessionContext sessionContext, boolean establirComAUsuariActual) {

		if (sessionContext == null || sessionContext.getCallerPrincipal() == null) {
			return null;
		}
		List<GrantedAuthority> authorities = new ArrayList<>();
		if (sessionContext.isCallerInRole("NOT_APL")) {
			authorities.add(new SimpleGrantedAuthority("NOT_APL"));
		}
		if (sessionContext.isCallerInRole("NOT_CARPETA")) {
			authorities.add(new SimpleGrantedAuthority("NOT_CARPETA"));
		}
		if (sessionContext.isCallerInRole("NOT_ADMIN")) {
			authorities.add(new SimpleGrantedAuthority("NOT_ADMIN"));
		}
		if (sessionContext.isCallerInRole("NOT_SUPER")) {
			authorities.add(new SimpleGrantedAuthority("NOT_SUPER"));
		}
		if (sessionContext.isCallerInRole("tothom")) {
			authorities.add(new SimpleGrantedAuthority("tothom"));
		}
		if (authorities.isEmpty()) {
			authorities = null;
		}
		return generarUsuariAutenticat(sessionContext.getCallerPrincipal().getName(), authorities, establirComAUsuariActual);
	}
	public Authentication generarUsuariAutenticat(String usuariCodi, boolean establirComAUsuariActual) {
		return generarUsuariAutenticat(usuariCodi, null, establirComAUsuariActual);
	}
	public Authentication generarUsuariAutenticat(String usuariCodi, List<GrantedAuthority> authorities, boolean establirComAUsuariActual) {

		Authentication auth = new DadesUsuariAuthenticationToken(usuariCodi, authorities);
		if (establirComAUsuariActual) {
			SecurityContextHolder.getContext().setAuthentication(auth);
		}
		return auth;
	}

	public class DadesUsuariAuthenticationToken extends AbstractAuthenticationToken {

		String principal;

		public DadesUsuariAuthenticationToken(String usuariCodi, Collection<GrantedAuthority> authorities) {
			super(authorities);
			principal = usuariCodi;
		}

		@Override
		public Object getCredentials() {
			return principal;
		}
		@Override
		public Object getPrincipal() {
			return principal;
		}
		private static final long serialVersionUID = 5974089352023050267L;
	}

}

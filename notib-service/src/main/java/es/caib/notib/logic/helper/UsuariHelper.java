/**
 * 
 */
package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.persist.entity.UsuariEntity;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.UsuariRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
public class UsuariHelper {
 
	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private UsuariRepository usuariRepository;

	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private ConfigHelper configHelper;



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

		var auth = new DadesUsuariAuthenticationToken(usuariCodi, authorities);
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

	public UsuariEntity getUsuariAutenticat() {

		var auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) {
			return null;
		}
		var usuari = usuariRepository.findById(auth.getName()).orElse(null);
		if (usuari != null) {
			return usuari;
		}
		log.debug("Consultant plugin de dades d'usuari (usuariCodi=" + auth.getName() + ")");
		var dadesUsuari = cacheHelper.findUsuariAmbCodi(auth.getName());
		var idioma = configHelper.getConfig("es.caib.notib.default.user.language");
		if (dadesUsuari == null) {
			throw new NotFoundException(auth.getName(), UsuariEntity.class);
		}
		var usr = UsuariEntity.builder().codi(dadesUsuari.getCodi()).email(dadesUsuari.getEmail()).idioma(idioma).nom(dadesUsuari.getNom())
				.llinatges(dadesUsuari.getLlinatges()).nomSencer(dadesUsuari.getNomSencer()).build();
		return usuariRepository.save(usr);
	}

}

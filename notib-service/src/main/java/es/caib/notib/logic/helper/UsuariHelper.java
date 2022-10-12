/**
 * 
 */
package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.persist.entity.UsuariEntity;
import es.caib.notib.persist.repository.UsuariRepository;
import es.caib.notib.plugin.usuari.DadesUsuari;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * Helper per a operacions amb usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class UsuariHelper {
 
	@Resource
	private UsuariRepository usuariRepository;

	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private ConfigHelper configHelper;

//	public Authentication generarUsuariAutenticatEjb(SessionContext sessionContext, boolean establirComAUsuariActual) {
//
//		if (sessionContext == null || sessionContext.getCallerPrincipal() == null) {
//			return null;
//		}
//		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
//		if (sessionContext.isCallerInRole("NOT_APL")) {
//			authorities.add(new SimpleGrantedAuthority("NOT_APL"));
//		}
//		if (sessionContext.isCallerInRole("NOT_CARPETA")) {
//			authorities.add(new SimpleGrantedAuthority("NOT_CARPETA"));
//		}
//		if (sessionContext.isCallerInRole("NOT_ADMIN")) {
//			authorities.add(new SimpleGrantedAuthority("NOT_ADMIN"));
//		}
//		if (sessionContext.isCallerInRole("NOT_SUPER")) {
//			authorities.add(new SimpleGrantedAuthority("NOT_SUPER"));
//		}
//		if (sessionContext.isCallerInRole("tothom")) {
//			authorities.add(new SimpleGrantedAuthority("tothom"));
//		}
//		if (authorities.isEmpty()) {
//			authorities = null;
//		}
//		return generarUsuariAutenticat(sessionContext.getCallerPrincipal().getName(), authorities, establirComAUsuariActual);
//	}
//	public Authentication generarUsuariAutenticat(String usuariCodi, boolean establirComAUsuariActual) {
//		return generarUsuariAutenticat(usuariCodi, null, establirComAUsuariActual);
//	}
//	public Authentication generarUsuariAutenticat(String usuariCodi, List<GrantedAuthority> authorities, boolean establirComAUsuariActual) {
//
//		Authentication auth = new DadesUsuariAuthenticationToken(usuariCodi, authorities);
//		if (establirComAUsuariActual) {
//			SecurityContextHolder.getContext().setAuthentication(auth);
//		}
//		return auth;
//	}
//
//	public class DadesUsuariAuthenticationToken extends AbstractAuthenticationToken {
//
//		String principal;
//
//		public DadesUsuariAuthenticationToken(String usuariCodi, Collection<GrantedAuthority> authorities) {
//			super(authorities);
//			principal = usuariCodi;
//		}
//
//		@Override
//		public Object getCredentials() {
//			return principal;
//		}
//		@Override
//		public Object getPrincipal() {
//			return principal;
//		}
//		private static final long serialVersionUID = 5974089352023050267L;
//	}

	public UsuariEntity getUsuariAutenticat() {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) {
			return null;
		}
		UsuariEntity usuari = usuariRepository.findById(auth.getName()).orElse(null);
		if (usuari != null) {
			return usuari;
		}
		log.debug("Consultant plugin de dades d'usuari (usuariCodi=" + auth.getName() + ")");
		DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(auth.getName());
		String idioma = configHelper.getConfig("es.caib.notib.default.user.language");
		if (dadesUsuari == null) {
			throw new NotFoundException(auth.getName(), UsuariEntity.class);
		}
		UsuariEntity usr = UsuariEntity.getBuilder(dadesUsuari.getCodi(), dadesUsuari.getEmail(), idioma).nom(dadesUsuari.getNom()).
								llinatges(dadesUsuari.getLlinatges()).nomSencer(dadesUsuari.getNomSencer()).build();
		usuari = usuariRepository.save(usr);
		return usuari;
	}
}

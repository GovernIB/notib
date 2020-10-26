/**
 * 
 */
package es.caib.notib.core.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.entity.UsuariEntity;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.UsuariRepository;
import es.caib.notib.plugin.usuari.DadesUsuari;


/**
 * Helper per a operacions amb usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class UsuariHelper {
 
	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private UsuariRepository usuariRepository;

	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;



	public Authentication generarUsuariAutenticatEjb(
			SessionContext sessionContext,
			boolean establirComAUsuariActual) {
		if (sessionContext != null && sessionContext.getCallerPrincipal() != null) {
			List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			if (sessionContext.isCallerInRole("NOT_APL"))
				authorities.add(new SimpleGrantedAuthority("NOT_APL"));
			if (sessionContext.isCallerInRole("NOT_CARPETA"))
				authorities.add(new SimpleGrantedAuthority("NOT_CARPETA"));
			if (sessionContext.isCallerInRole("NOT_ADMIN"))
				authorities.add(new SimpleGrantedAuthority("NOT_ADMIN"));
			if (sessionContext.isCallerInRole("NOT_SUPER"))
				authorities.add(new SimpleGrantedAuthority("NOT_SUPER"));
			if (sessionContext.isCallerInRole("tothom"))
				authorities.add(new SimpleGrantedAuthority("tothom"));
			if (authorities.isEmpty())
				authorities = null;
			return generarUsuariAutenticat(
					sessionContext.getCallerPrincipal().getName(),
					authorities,
					establirComAUsuariActual);
		} else {
			return null;
		}
	}
	public Authentication generarUsuariAutenticat(
			String usuariCodi,
			boolean establirComAUsuariActual) {
		return generarUsuariAutenticat(
				usuariCodi,
				null,
				establirComAUsuariActual);
	}
	public Authentication generarUsuariAutenticat(
			String usuariCodi,
			List<GrantedAuthority> authorities,
			boolean establirComAUsuariActual) {
		Authentication auth = new DadesUsuariAuthenticationToken(
				usuariCodi,
				authorities);
		if (establirComAUsuariActual)
			SecurityContextHolder.getContext().setAuthentication(auth);
		return auth;
	}

	public class DadesUsuariAuthenticationToken extends AbstractAuthenticationToken {
		String principal;
		public DadesUsuariAuthenticationToken(
				String usuariCodi,
				Collection<GrantedAuthority> authorities) {
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
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null)
			return null;
		UsuariEntity usuari = usuariRepository.findOne(auth.getName());
		if (usuari == null) {
			logger.debug("Consultant plugin de dades d'usuari (" +
					"usuariCodi=" + auth.getName() + ")");
			DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(auth.getName());
			String idioma = PropertiesHelper.getProperties().getProperty("es.caib.notib.default.user.language");
			if (dadesUsuari != null) {
				usuari = usuariRepository.save(
						UsuariEntity.getBuilder(
								dadesUsuari.getCodi(),
								dadesUsuari.getEmail(),
								idioma).
						nom(dadesUsuari.getNom()).
						llinatges(dadesUsuari.getLlinatges()).
						nomSencer(dadesUsuari.getNomSencer()).
						build());
			} else {
				throw new NotFoundException(
						auth.getName(),
						UsuariEntity.class);
			}
		}
		return usuari;
	}

	private static final Logger logger = LoggerFactory.getLogger(UsuariHelper.class);

}

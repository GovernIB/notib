/**
 * 
 */
package es.caib.notib.core.audit;

import es.caib.notib.core.entity.UsuariEntity;
import es.caib.notib.core.repository.UsuariRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Resource;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una entitat que està emmagatzemada a dins la base de
 * dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class NotibAuditorAware implements AuditorAware<UsuariEntity> {

	@Resource
	private UsuariRepository usuariRepository;

	@Override
	public UsuariEntity getCurrentAuditor() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String auditorActual = (auth != null) ? auth.getName() : null;
		log.debug("Obtenint l'usuari auditor per a l'usuari (codi=" + auditorActual + ")");
		if (auditorActual == null) {
			return null;
		}
		UsuariEntity usuari = usuariRepository.findOne(auditorActual);
		return usuari;
	}

}

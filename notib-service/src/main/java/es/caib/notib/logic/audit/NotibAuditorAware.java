/**
 * 
 */
package es.caib.notib.logic.audit;

import es.caib.notib.persist.entity.UsuariEntity;
import es.caib.notib.persist.repository.UsuariRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una entitat que està emmagatzemada a dins la base de
 * dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotibAuditorAware implements AuditorAware<UsuariEntity> {

	@Resource
	private UsuariRepository usuariRepository;

	@Override
	public Optional<UsuariEntity> getCurrentAuditor() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String auditorActual = (auth != null) ? auth.getName() : null;
		LOGGER.debug("Obtenint l'usuari auditor per a l'usuari (codi=" + auditorActual + ")");
		if (auditorActual == null) {
			return Optional.empty();
		} else {
			return usuariRepository.findById(auditorActual);
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(NotibAuditorAware.class);

}

/**
 * 
 */
package es.caib.notib.logic.audit;

import es.caib.notib.persist.entity.UsuariEntity;
import es.caib.notib.persist.repository.UsuariRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
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
@Slf4j
public class NotibAuditorAware implements AuditorAware<UsuariEntity> {

	@Resource
	private UsuariRepository usuariRepository;

	@Override
	public Optional<UsuariEntity> getCurrentAuditor() {

		var auth = SecurityContextHolder.getContext().getAuthentication();
		var auditorActual = (auth != null) ? auth.getName() : null;
		log.debug("Obtenint l'usuari auditor per a l'usuari (codi=" + auditorActual + ")");
		return auditorActual == null ? Optional.empty() : usuariRepository.findById(auditorActual);
	}

}

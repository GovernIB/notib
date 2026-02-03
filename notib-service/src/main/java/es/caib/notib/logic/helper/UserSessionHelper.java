package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.base.util.ThreadLocalUtil;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.model.UserSession;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.persist.resourcerepository.EntitatResourceRepository;
import liquibase.pro.packaged.M;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Helper per a obtenir informació de la sessió d'usuari (capçalera HTTP).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserSessionHelper {

	private final EntitatResourceRepository entitatResourceRepository;

	/**
	 * Retorna l'id d'entitat actual.
	 *
	 * @return l'id de l'entitat si n'hi ha alguna de seleccionada o null en cas contrari.
	 */
	public Long getCurrentEntitatId() {
		UserSession session = getUserSession();
		return session != null ? session.getEntitatId() : null;
	}

	/**
	 * Retorna l'entitat actual.
	 *
	 * @return l'entitat si n'hi ha alguna de seleccionada.
	 * @throws NotFoundException
	 *            si no s'ha trobat l'entitat a la sessió o a la base de dades.
	 */
	public EntitatResourceEntity getCurrentEntitat() throws NotFoundException {
		Long currentEntitatId = getCurrentEntitatId();
		if (currentEntitatId != null) {
			Optional<EntitatResourceEntity> entitat = entitatResourceRepository.findById(currentEntitatId);
			if (entitat.isPresent()) {
				return entitat.get();
			}
		}
		throw new NotFoundException(
			currentEntitatId,
			EntitatResourceEntity.class,
			"Couldn't find current entitat in session");
	}

	/**
	 * Retorna l'id de l'òrgan gestor actual.
	 *
	 * @return l'id de l'òrgan gestor si n'hi ha algun de seleccionat o null en cas contrari.
	 */
	public Long getCurrentOrganGestorId() {
		UserSession session = getUserSession();
		return session != null ? session.getOrganGestorId() : null;
	}

	/**
	 * Retorna l'òrgan gestor actual.
	 *
	 * @return l'òrgan gestor si n'hi ha algun de seleccionat.
	 * @throws NotFoundException
	 *            si no s'ha trobat l'òrgan gestor a la sessió o a la base de dades.
	 */
	public EntitatResourceEntity getCurrentOrganGestor() throws NotFoundException {
		Long currentEntitatId = getCurrentEntitatId();
		if (currentEntitatId != null) {
			Optional<EntitatResourceEntity> entitat = entitatResourceRepository.findById(currentEntitatId);
			if (entitat.isPresent()) {
				return entitat.get();
			}
		}
		throw new NotFoundException(
			currentEntitatId,
			EntitatResourceEntity.class,
			"Couldn't find current òrgan gestor in session");
	}

	private UserSession getUserSession() {
		return ThreadLocalUtil.getAttribute(ThreadLocalUtil.SESSION_KEY, UserSession.class);
	}

}

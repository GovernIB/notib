package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.base.util.ThreadLocalUtil;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.model.UserSession;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import es.caib.notib.persist.resourcerepository.EntitatResourceRepository;
import es.caib.notib.persist.resourcerepository.OrganGestorResourceRepository;
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
	private final OrganGestorResourceRepository organGestorResourceRepository;

	/**
	 * Retorna l'id d'entitat actual.
	 *
	 * @return l'id de l'entitat si n'hi ha alguna de seleccionada o null en cas contrari.
	 */
	public Long getCurrentEntitatId() {

		var userSession = getUserSession();
		return userSession != null ? userSession.getEntitatId() : null;
	}

	/**
	 * Retorna l'entitat actual.
	 *
	 * @return Retorna la entitat seleccionada.
	 * @throws NotFoundException si no s'ha trobat l'entitat a la sessió o a la base de dades.
	 */
	public EntitatResourceEntity getCurrentEntitat() throws NotFoundException {

		var currentEntitatId = getCurrentEntitatId();
		if (currentEntitatId == null) {
			throw new NotFoundException(null, EntitatResourceEntity.class, "No s'ha trobat entitat a la sessio");
		}
		var entitat = entitatResourceRepository.findById(currentEntitatId);
		if (entitat.isEmpty()) {
			throw new NotFoundException(currentEntitatId, EntitatResourceEntity.class, "La entitat de la sessio no existeix a la BDD");
		}
		return entitat.get();
	}

	/**
	 * Retorna l'id de l'òrgan gestor actual.
	 *
	 * @return l'id de l'òrgan gestor si n'hi ha algun de seleccionat o null en cas contrari.
	 */
	public Long getCurrentOrganGestorId() {

		var userSession = getUserSession();
		return userSession != null ? userSession.getOrganGestorId() : null;
	}

	/**
	 * Retorna l'òrgan gestor actual.
	 *
	 * @return l'òrgan gestor si n'hi ha algun de seleccionat.
	 * @throws NotFoundException si s'ha trobat òrgan gestor a la sessió però no a la base de dades.
	 */
	public OrganGestorResourceEntity getCurrentOrganGestor() throws NotFoundException {

		var currentOrganGestorId = getCurrentOrganGestorId();
		if (currentOrganGestorId == null) {
			return null;
		}
		var organ = organGestorResourceRepository.findById(currentOrganGestorId);
		if (organ.isEmpty()) {
			throw new NotFoundException(currentOrganGestorId, OrganGestorResourceEntity.class, "Organ gestor no trobat a la bdd");
		}
		return organ.get();
	}

	private UserSession getUserSession() {
		return ThreadLocalUtil.getAttribute(ThreadLocalUtil.SESSION_KEY, UserSession.class);
	}

}

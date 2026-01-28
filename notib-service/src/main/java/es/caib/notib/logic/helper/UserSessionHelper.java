package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.base.util.ThreadLocalUtil;
import es.caib.notib.logic.intf.model.UserSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Helper per a obtenir informació de la sessió d'usuari (capçalera HTTP).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class UserSessionHelper {

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
	 * Retorna l'id de l'òrgan gestor actual.
	 *
	 * @return l'id de l'òrgan gestor si n'hi ha algun de seleccionat o null en cas contrari.
	 */
	public Long getCurrentOrganGestorId() {
		UserSession session = getUserSession();
		return session != null ? session.getOrganGestorId() : null;
	}

	private UserSession getUserSession() {
		return ThreadLocalUtil.getAttribute(ThreadLocalUtil.SESSION_KEY, UserSession.class);
	}

}

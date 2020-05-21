/**
 * 
 */
package es.caib.notib.core.helper;

import java.security.GeneralSecurityException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;

/**
 * Helper per a interactuar amb el servei web de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class NotificaHelper {

	@Autowired
	private NotificaV0Helper notificaV0Helper; // Mock
	@Autowired
	private NotificaV1Helper notificaV1Helper;
	@Autowired
	private NotificaV2Helper notificaV2Helper;


	@Transactional
	public boolean notificacioEnviar(
			Long notificacioId) {
		return getNotificaHelper().notificacioEnviar(notificacioId);
	}

	@Transactional
	public boolean enviamentRefrescarEstat(
			Long enviamentId) throws SistemaExternException {
		return getNotificaHelper().enviamentRefrescarEstat(enviamentId);
	}

	public String xifrarId(Long id) throws GeneralSecurityException {
		return getNotificaHelper().xifrarId(id);
	}
	public Long desxifrarId(String identificador) throws GeneralSecurityException {
		return getNotificaHelper().desxifrarId(identificador);
	}

	public boolean isConnexioNotificaDisponible() {
		return getNotificaHelper().isConnexioNotificaDisponible();
	}

	public boolean isAdviserActiu() {
		return getNotificaHelper().isAdviserActiu();
	}

	public void enviamentUpdateDatat(
			NotificacioEnviamentEstatEnumDto notificaEstat,
			Date notificaEstatData,
			String notificaEstatDescripcio,
			String notificaDatatOrigen,
			String notificaDatatReceptorNif,
			String notificaDatatReceptorNom,
			String notificaDatatNumSeguiment,
			String notificaDatatErrorDescripcio,
			NotificacioEnviamentEntity enviament) {
		getNotificaHelper().enviamentUpdateDatat(
				notificaEstat,
				notificaEstatData,
				notificaEstatDescripcio,
				notificaDatatOrigen,
				notificaDatatReceptorNif,
				notificaDatatReceptorNom,
				notificaDatatNumSeguiment,
				notificaDatatErrorDescripcio,
				enviament);
	}



	private AbstractNotificaHelper getNotificaHelper() {
		String versio = getNotificaVersioProperty();
		if ("0".equals(versio)) {
			return notificaV0Helper;
		} else if ("1".equals(versio)) {
			return notificaV1Helper;
		} else if ("2".equals(versio)) {
			return notificaV2Helper;
		} else {
			return notificaV2Helper;
		}
	}

	private String getNotificaVersioProperty() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.versio");
	}

}

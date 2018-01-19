/**
 * 
 */
package es.caib.notib.core.helper;

import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.NotificaRespostaEstatDto;
import es.caib.notib.core.api.exception.SistemaExternException;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;

/**
 * Helper per a interactuar amb el servei web de Notifica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class NotificaHelper {

	@Autowired
	private NotificaV1Helper notificaV1Helper;
	@Autowired
	private NotificaV2Helper notificaV2Helper;



	public boolean enviament(
			Long notificacioId) {
		return getNotificaHelper().enviament(notificacioId);
	}

	public NotificaRespostaEstatDto refrescarEstat(
			NotificacioEnviamentEntity destinatari) throws SistemaExternException {
		return getNotificaHelper().refrescarEstat(destinatari);
	}

	public boolean comunicacioSeu(
			Long notificacioDestinatariId) {
		return getNotificaHelper().comunicacioSeu(notificacioDestinatariId);
	}

	public String generarReferencia(
			NotificacioEnviamentEntity notificacioDestinatari) throws GeneralSecurityException {
		return getNotificaHelper().xifrarIdPerNotifica(notificacioDestinatari.getId());
	}

	public boolean isConnexioNotificaDisponible() {
		return getNotificaHelper().isConnexioNotificaDisponible();
	}

	public boolean isAdviserActiu() {
		return getNotificaHelper().isAdviserActiu();
	}



	private AbstractNotificaHelper getNotificaHelper() {
		String versio = getNotificaVersioProperty();
		if ("1".equals(versio)) {
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

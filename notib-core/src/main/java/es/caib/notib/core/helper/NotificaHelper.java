/**
 * 
 */
package es.caib.notib.core.helper;

import java.security.GeneralSecurityException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

<<<<<<< HEAD
import es.caib.notib.core.api.dto.ArxiuDto;
=======
>>>>>>> branch 'master' of https://github.com/GovernIB/notib.git
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
	private NotificaV1Helper notificaV1Helper;
	@Autowired
	private NotificaV2Helper notificaV2Helper;



	public boolean notificacioEnviar(
			Long notificacioId) {
		return getNotificaHelper().notificacioEnviar(notificacioId);
	}

	public boolean enviamentRefrescarEstat(
			NotificacioEnviamentEntity enviament) throws SistemaExternException {
		return getNotificaHelper().enviamentRefrescarEstat(enviament);
	}

	public boolean enviamentComunicacioSeu(
<<<<<<< HEAD
			NotificacioEnviamentEntity enviament,
			Date comunicacioData) {
		return getNotificaHelper().enviamentComunicacioSeu(
				enviament,
				comunicacioData);
=======
			Long notificacioEnviamentId) {
		return getNotificaHelper().enviamentComunicacioSeu(notificacioEnviamentId);
>>>>>>> branch 'master' of https://github.com/GovernIB/notib.git
	}

<<<<<<< HEAD
	public boolean enviamentCertificacioSeu(
			NotificacioEnviamentEntity enviament,
			ArxiuDto certificacioArxiu,
			Date certificacioData) {
		return getNotificaHelper().enviamentCertificacioSeu(
				enviament,
				certificacioArxiu,
				certificacioData);
=======
	public String xifrarId(Long id) throws GeneralSecurityException {
		return getNotificaHelper().xifrarId(id);
>>>>>>> branch 'master' of https://github.com/GovernIB/notib.git
	}
<<<<<<< HEAD

	public String xifrarId(Long id) throws GeneralSecurityException {
		return getNotificaHelper().xifrarId(id);
	}
=======
>>>>>>> branch 'master' of https://github.com/GovernIB/notib.git
	public Long desxifrarId(String identificador) throws GeneralSecurityException {
		return getNotificaHelper().desxifrarId(identificador);
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

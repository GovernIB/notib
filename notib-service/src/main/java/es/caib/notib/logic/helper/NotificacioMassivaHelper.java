package es.caib.notib.logic.helper;

import es.caib.notib.persist.repository.NotificacioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Helper per a notificacions massives
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class NotificacioMassivaHelper {

	@Autowired
	private NotificacioRepository notificacioRepository;

	@Transactional
	public void posposarNotificacions(Long notificacioMassivaId) {

		var notificacions = notificacioRepository.findByNotificacioMassivaEntityId(notificacioMassivaId);
		for (var notificacio: notificacions) {
			// postposam el temps 8 hores
			notificacio.decreaseRegistreEnviamentPrioritat(8*60*60);
		}
	}

	/**
	 * Fixa la prioritat de les notitificacions de la notificacio massiva per a que s'executin igual que la resta.
	 *
	 * @param notificacioMassivaId Identificador de la notificació massiva que es vol reactivar.
	 */
	@Transactional
	public void reactivarNotificacions(Long notificacioMassivaId) {

		var notificacions = notificacioRepository.findByNotificacioMassivaEntityId(notificacioMassivaId);
		for (var notificacio: notificacions) {
			notificacio.restablirPrioritat();
		}
	}
}

package es.caib.notib.core.helper;

import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioMassivaEntity;
import es.caib.notib.core.repository.NotificacioMassivaRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Helper per a notificacions massives
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class NotificacioMassivaHelper {
	@Autowired
	private NotificacioMassivaRepository notificacioMassivaRepository;
	@Autowired
	private NotificacioRepository notificacioRepository;

	@Transactional
	public void updateProgress(Long notificacioMassivaId) {
		NotificacioMassivaEntity notificacioMassiva = notificacioMassivaRepository.findOne(notificacioMassivaId);
		int numProcessades = notificacioMassivaRepository.countNotificacionsNoPendents(notificacioMassiva);
		int progress = (int) (((double) numProcessades / (double) notificacioMassiva.getNotificacions().size()) * 100);
		notificacioMassiva.updateProgress(progress);
	}

	@Transactional
	public void posposarNotificacions(Long notificacioMassivaId) {
		List<NotificacioEntity> notificacions = notificacioRepository.findByNotificacioMassivaEntityId(notificacioMassivaId);
		for (NotificacioEntity notificacio: notificacions) {
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
		List<NotificacioEntity> notificacions = notificacioRepository.findByNotificacioMassivaEntityId(notificacioMassivaId);
		for (NotificacioEntity notificacio: notificacions) {
			notificacio.restablirPrioritat();
		}
	}
}

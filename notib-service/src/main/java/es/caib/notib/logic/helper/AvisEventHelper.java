package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.AvisResource;
import es.caib.notib.logic.intf.model.sse.AvisosActiusEvent;
import es.caib.notib.persist.entity.AvisEntity;
import es.caib.notib.persist.resourceentity.AvisResourceEntity;
import es.caib.notib.persist.resourcerepository.AvisResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AvisEventHelper {

	public static final String DESTINACIO_AVISOS = "avisos";

	private final JmsTemplate jmsTemplate;
	private final AvisResourceRepository avisRepository;

//	public void notifyAvisosActius() {
//		TransactionAfterCommitUtils.run(() -> {
//			try {
//				log.debug("Notificant als clients que els avisos actius han canviat");
//				jmsTemplate.convertAndSend(DESTINACIO_AVISOS, new AvisosActiusEvent(null));
//			} catch (Exception ex) {
//				log.error("Error notificant als clients que els avisos actius han canviat", ex);
//			}
//		});
//	}

	public void notifyAvisosActius() {
		Runnable notificar = () -> {
			try {
				log.debug("Notificant als clients que els avisos actius han canviat");
				jmsTemplate.convertAndSend(DESTINACIO_AVISOS, new AvisosActiusEvent(null));
			} catch (Exception ex) {
				log.error("Error notificant als clients que els avisos actius han canviat", ex);
			}
		};
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					notificar.run();
				}
			});
		} else {
			notificar.run();
		}
	}

	@Transactional(readOnly = true)
	public AvisosActiusEvent getAvisosActius(String rol, Long entitatId) {

		List<AvisResourceEntity> actius = avisRepository.findActius(DateUtils.truncate(new Date(), Calendar.DATE));
		boolean superusuari = BaseConfig.ROLE_SUPER.equals(rol);
		List<AvisResource> avisos = actius.stream()
			.filter(avis -> superusuari
				|| avis.getEntitat() == null
				|| avis.getEntitat().getId().equals(entitatId))
			.map(this::toAvisSse)
			.collect(Collectors.toList());
		return new AvisosActiusEvent(avisos);
	}

	private AvisResource toAvisSse(AvisResourceEntity avis) {
		return AvisResource.builder()
			.assumpte(avis.getAssumpte())
			.missatge(avis.getMissatge())
			.avisNivell(avis.getAvisNivell())
			.dataInici(avis.getDataInici())
			.dataFinal(avis.getDataFinal())
			.build();
	}
}

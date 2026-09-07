package es.caib.notib.logic.enviaments;

import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.persist.resourcerepository.NotificacioResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnviamentStartSmEventListener {

	private final NotificacioResourceRepository notificacioResourceRepository;
	private final EnviamentSmService enviamentSmService;

	@Transactional
	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onCreateRemesa(NotificacioEventStartSm event) {
		try {
			var entity = notificacioResourceRepository.findById(event.getNotificacioId()).orElseThrow();
			entity.getEnviaments().forEach(e -> enviamentSmService.altaEnviament(e.getReferenciaEnviament()));
		} catch (Exception ex) {
			log.error("[NotificacioResourceServiceImpl.postCreate] Error inesperat en el postCreate", ex);
		}
	}
}

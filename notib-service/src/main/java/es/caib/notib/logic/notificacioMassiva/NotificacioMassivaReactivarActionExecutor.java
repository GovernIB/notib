package es.caib.notib.logic.notificacioMassiva;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;
import es.caib.notib.logic.intf.model.NotificacioMassivaResource;
import es.caib.notib.logic.intf.service.NotificacioMassivaService;
import es.caib.notib.persist.resourceentity.NotificacioEnviamentResourceEntity;
import es.caib.notib.persist.resourceentity.NotificacioMassivaResourceEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class NotificacioMassivaReactivarActionExecutor implements BaseMutableResourceService.ActionExecutor<NotificacioMassivaResourceEntity, Serializable, Boolean> {

	private final NotificacioMassivaService notificacioMassivaService;

	@Override
	public Boolean exec(String code, NotificacioMassivaResourceEntity entity, Serializable params) throws ActionExecutionException {

		try {
			notificacioMassivaService.posposar(entity.getEntitat().getId(), entity.getId());
			return true;
		} catch (Exception ex) {
			log.error("[NotificacioMassivaReactivarActionExecutor] Error reactivant la notificacio massiva " + entity.getId());
			return false;
		}
	}

	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map answers, String[] previousFieldNames, Serializable target) {

	}

}

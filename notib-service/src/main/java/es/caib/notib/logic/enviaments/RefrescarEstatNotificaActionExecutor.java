package es.caib.notib.logic.enviaments;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;
import es.caib.notib.persist.resourceentity.NotificacioEnviamentResourceEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class RefrescarEstatNotificaActionExecutor implements BaseMutableResourceService.ActionExecutor<NotificacioEnviamentResourceEntity, Serializable, String> {

	@Override
	public String exec(String code, NotificacioEnviamentResourceEntity entity, Serializable params) throws ActionExecutionException {

		log.info("action exec");
		return null;
	}

	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map answers, String[] previousFieldNames, Serializable target) {

	}

}

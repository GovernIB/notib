package es.caib.notib.logic.enviaments;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.service.CallbackService;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class EnviarCallbackActionExecutor implements BaseMutableResourceService.ActionExecutor<NotificacioResourceEntity, Serializable, Boolean> {

	private final CallbackService callbackService;

	@Override
	public Boolean exec(String code, NotificacioResourceEntity entity, Serializable params) throws ActionExecutionException {

		try {
			return callbackService.reintentarCallback(entity.getId());
		} catch (Exception ex) {
			log.error("[EnviarCallbackActionExecutor] Error enviant el callback per la notificacio " + entity.getId());
			return false;
		}
	}

	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map answers, String[] previousFieldNames, Serializable target) {

	}

}

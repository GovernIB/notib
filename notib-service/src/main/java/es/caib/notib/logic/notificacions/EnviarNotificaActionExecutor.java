package es.caib.notib.logic.notificacions;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.dto.RespostaActionExecutor;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class EnviarNotificaActionExecutor implements BaseMutableResourceService.ActionExecutor<NotificacioResourceEntity, Serializable, RespostaActionExecutor> {

	private final NotificacioService notificacioService;

	@Override
	public RespostaActionExecutor exec(String code, NotificacioResourceEntity entity, Serializable params) throws ActionExecutionException {

		try {
			var ok = notificacioService.enviarNotificacioANotifica(entity.getId(), true);
			return RespostaActionExecutor.builder().ok(ok).build();
		} catch (Exception ex) {
			var msg = "Error inesperat enviant a notifica ";
			log.error("[EnviarNotificaActionExecutor] " + msg + "per la notificacio " + entity.getId());
			throw new ActionExecutionException(NotificacioResource.class, entity.getId(), "-1", msg + ": " + ex.getMessage());
		}
	}

	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map answers, String[] previousFieldNames, Serializable target) {

	}

}

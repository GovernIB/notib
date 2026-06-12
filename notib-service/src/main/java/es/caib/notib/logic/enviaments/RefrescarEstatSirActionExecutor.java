package es.caib.notib.logic.enviaments;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.dto.RespostaActionExecutor;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.persist.resourceentity.NotificacioEnviamentResourceEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class RefrescarEstatSirActionExecutor implements BaseMutableResourceService.ActionExecutor<NotificacioEnviamentResourceEntity, Serializable, RespostaActionExecutor> {

	private NotificacioService notificacioService;

	@Override
	public RespostaActionExecutor exec(String code, NotificacioEnviamentResourceEntity entity, Serializable params) throws ActionExecutionException {

		try {
			var estat = notificacioService.enviamentRefrescarEstatSir(entity.getId());
			return RespostaActionExecutor.builder().ok(estat != null).build();
		} catch (Exception ex) {
			var msg = "Error inesperat refrescant l'estat SIR ";
			log.error("[RefrescarEstatSirActionExecutor] " + msg + "per l'enviament' " + entity.getId());
			throw new ActionExecutionException(NotificacioResource.class, entity.getId(), "-1", msg + ": " + ex.getMessage());
		}
	}

	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map answers, String[] previousFieldNames, Serializable target) {

	}

}

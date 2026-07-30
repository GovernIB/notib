package es.caib.notib.logic.notificacions;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.LegacyHelper;
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
public class RefrescarEstatActionExecutor implements BaseMutableResourceService.ActionExecutor<NotificacioResourceEntity, Serializable, RespostaActionExecutor> {


	private LegacyHelper legacyHelper;

	@Override
	public RespostaActionExecutor exec(String code, NotificacioResourceEntity entity, Serializable params) throws ActionExecutionException {

		try {

			legacyHelper.actualitzarColumnaEstat(entity);
			return RespostaActionExecutor.builder().ok(true).build();
		} catch (Exception ex) {
			var msg = "Error inesperat refrescant l'estat string ";
			log.error("[RefrescarEstatEstatActionExecutor] " + msg + "per la notificacio " + entity.getId());
			throw new ActionExecutionException(NotificacioResource.class, entity.getId(), "-1", msg + ": " + ex.getMessage());
		}
	}

	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map answers, String[] previousFieldNames, Serializable target) {

	}

}

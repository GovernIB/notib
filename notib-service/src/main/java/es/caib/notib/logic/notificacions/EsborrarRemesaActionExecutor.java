package es.caib.notib.logic.notificacions;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.dto.MarcarProcessat;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class EsborrarRemesaActionExecutor implements BaseMutableResourceService.ActionExecutor<NotificacioResourceEntity, Serializable, String> {

	private NotificacioService notificacioService;
	private MessageHelper messageHelper;

	@Override
	public String exec(String code, NotificacioResourceEntity entity, Serializable params) throws ActionExecutionException {

		try {
			notificacioService.delete(entity.getEntitat().getId(), entity.getId());
			return messageHelper.getMessage("notificacio.controller.esborrar.ok");
		} catch (Exception ex) {
			log.error("[EsborrarRemesaActionExecutor] Hi ha hagut un error esborrant la notificació", ex);
			return messageHelper.getMessage("\"notificacio.controller.esborrar.ko") + " - " + ex.getMessage();
		}
	}

	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {

	}
}

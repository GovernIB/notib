package es.caib.notib.logic.notificacions;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.dto.MarcarProcessat;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class MarcarProcessatActionExecutor implements BaseMutableResourceService.ActionExecutor<NotificacioResourceEntity, MarcarProcessat, String> {

	@Override
	public String exec(String code, NotificacioResourceEntity entity, MarcarProcessat params) throws ActionExecutionException {

		log.info("action exec");
		return null;
	}

	@Override
	public void onChange(Serializable id, MarcarProcessat previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, MarcarProcessat target) {

	}
}

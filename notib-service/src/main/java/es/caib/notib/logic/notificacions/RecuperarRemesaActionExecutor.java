package es.caib.notib.logic.notificacions;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.dto.RespostaActionExecutor;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class RecuperarRemesaActionExecutor implements BaseMutableResourceService.ActionExecutor<NotificacioResourceEntity, Serializable, RespostaActionExecutor> {

	private NotificacioService notificacioService;
	private MessageHelper messageHelper;

	@Override
	public RespostaActionExecutor exec(String code, NotificacioResourceEntity entity, Serializable params) throws ActionExecutionException {

		try {
			notificacioService.restore(entity.getEntitat().getId(), entity.getId());
			return RespostaActionExecutor.builder().ok(true).build();
		} catch (Exception ex) {
			log.error("[RecuperarRemesaActionExecutor] Hi ha hagut un error recuperant la remesa", ex);
			return RespostaActionExecutor.builder().ok(false).build();
		}
	}


	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {

	}
}

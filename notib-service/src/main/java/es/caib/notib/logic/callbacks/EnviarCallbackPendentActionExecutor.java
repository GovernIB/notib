package es.caib.notib.logic.callbacks;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.dto.RespostaActionExecutor;
import es.caib.notib.logic.intf.dto.anular.AnularDto;
import es.caib.notib.logic.intf.dto.callback.CallbackResposta;
import es.caib.notib.logic.intf.model.CallbackResource;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.service.CallbackService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.persist.resourceentity.CallbackResourceEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class EnviarCallbackPendentActionExecutor implements BaseMutableResourceService.ActionExecutor<CallbackResourceEntity, Serializable, CallbackResposta> {

	private final CallbackService callbackService;

	@Override
	public CallbackResposta exec(String code, CallbackResourceEntity entity, Serializable params) throws ActionExecutionException {

		try {
 			return callbackService.enviarCallback(entity.getId());
		} catch (Exception ex) {
			var msg = "Error inesperat enviant el callback pendent ";
			log.error("[EnviarCallbackPendentActionExecutor] " + msg + "amb id " + entity.getId() + " - " + ex.getMessage());
			throw new ActionExecutionException(CallbackResource.class, entity.getId(), "-1", msg + ex.getMessage());
		}
	}

	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {

	}
}

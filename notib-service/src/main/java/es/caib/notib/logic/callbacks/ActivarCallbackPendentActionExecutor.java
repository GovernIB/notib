package es.caib.notib.logic.callbacks;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.dto.callback.CallbackResposta;
import es.caib.notib.logic.intf.model.CallbackResource;
import es.caib.notib.logic.intf.service.CallbackService;
import es.caib.notib.persist.resourceentity.CallbackResourceEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class ActivarCallbackPendentActionExecutor implements BaseMutableResourceService.ActionExecutor<CallbackResourceEntity, Serializable, CallbackResposta> {

	private final CallbackService callbackService;

	@Override
	public CallbackResposta exec(String code, CallbackResourceEntity entity, Serializable params) throws ActionExecutionException {

		try {
 			var ok = callbackService.pausarCallback(entity.getId(), false);
			return CallbackResposta.builder().ok(ok).build();
		} catch (Exception ex) {
			var msg = "Error inesperat al activar el callback pendent ";
			log.error("[ActivarCallbackPendentActionExecutor] " + msg + "amb id " + entity.getId() + " - " + ex.getMessage());
			throw new ActionExecutionException(CallbackResource.class, entity.getId(), "-1", msg + ex.getMessage());
		}
	}

	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {

	}
}

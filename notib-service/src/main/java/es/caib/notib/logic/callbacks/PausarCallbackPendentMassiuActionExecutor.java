package es.caib.notib.logic.callbacks;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.AccioMassivaParams;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.dto.callback.CallbackResposta;
import es.caib.notib.logic.intf.model.CallbackResource;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.service.CallbackService;
import es.caib.notib.persist.resourceentity.CallbackResourceEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@AllArgsConstructor
public class PausarCallbackPendentMassiuActionExecutor implements BaseMutableResourceService.ActionExecutor<CallbackResourceEntity, AccioMassivaParams, CallbackResposta> {

	private final CallbackService callbackService;

	@Override
	public CallbackResposta exec(String code, CallbackResourceEntity entity, AccioMassivaParams params) throws ActionExecutionException {

		if (params == null || params.idsEmpty()) {
			throw new ActionExecutionException(NotificacioResource.class, null, "-1", "La selecció no pot ser buida");
		}
		try {
 			return callbackService.pausarCallback(new HashSet<>(params.getIds()), true);
		} catch (Exception ex) {
			var msg = "Error inesperat pausant els callbacks pendents ";
			log.error("[PausarCallbackPendentMassiuActionExecutor] " + msg + "amb id " + entity.getId() + " - " + ex.getMessage());
			throw new ActionExecutionException(CallbackResource.class, entity.getId(), "-1", msg + ex.getMessage());
		}
	}

	@Override
	public void onChange(Serializable id, AccioMassivaParams previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, AccioMassivaParams target) {

	}
}

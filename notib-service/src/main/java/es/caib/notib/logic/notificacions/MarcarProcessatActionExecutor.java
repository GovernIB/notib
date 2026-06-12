package es.caib.notib.logic.notificacions;

import com.google.common.base.Strings;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.dto.MarcarProcessatReposta;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.dto.MarcarProcessat;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class MarcarProcessatActionExecutor implements BaseMutableResourceService.ActionExecutor<NotificacioResourceEntity, MarcarProcessat, MarcarProcessatReposta> {

	private final NotificacioService notificacioService;

	@Override
	public MarcarProcessatReposta exec(String code, NotificacioResourceEntity entity, MarcarProcessat params) throws ActionExecutionException {

		try {
			var msg = notificacioService.marcarComProcessada(entity.getId(), params.getMotiu(), true);
			return MarcarProcessatReposta.builder().error(!Strings.isNullOrEmpty(msg)).errorDescripcio(msg).build();
		} catch (Exception ex) {
			var msg = "Error inesperat marcant com a processat ";
			log.info("[MarcarProcessatActionExecutor] " + msg + "per la remesa " + entity.getId() + " - " + ex.getMessage());
			throw new ActionExecutionException(NotificacioResource.class, entity.getId(), "-1", msg + ": " + ex.getMessage());
		}
	}

	@Override
	public void onChange(Serializable id, MarcarProcessat previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, MarcarProcessat target) {

	}
}

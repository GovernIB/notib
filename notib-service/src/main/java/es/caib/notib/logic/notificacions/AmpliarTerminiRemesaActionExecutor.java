package es.caib.notib.logic.notificacions;

import es.caib.notib.client.domini.ampliarPlazo.RespuestaAmpliarPlazoOE;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.dto.AmpliacionPlazoDto;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class AmpliarTerminiRemesaActionExecutor implements BaseMutableResourceService.ActionExecutor<NotificacioResourceEntity, AmpliacionPlazoDto, RespuestaAmpliarPlazoOE> {

	private final NotificacioService notificacioService;

	@Override
	public RespuestaAmpliarPlazoOE exec(String code, NotificacioResourceEntity entity, AmpliacionPlazoDto params) throws ActionExecutionException {

		try {
			if (params.getEnviamentId() == null) {
				params.setNotificacioId(entity.getId());
			}
			return notificacioService.ampliacionPlazoOE(params);
		} catch (Exception ex) {
			var msg = "Error inesperat ampliant el termini ";
			log.info("[AmpliarTerminiRemesaActionExecutor] " + msg + "per la remesa " + entity.getId());
			throw new ActionExecutionException(NotificacioResource.class, entity.getId(), "-1", msg + ": " + ex.getMessage());
		}
	}

	@Override
	public void onChange(Serializable id, AmpliacionPlazoDto previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, AmpliacionPlazoDto target) {
		log.info("change");
	}
}

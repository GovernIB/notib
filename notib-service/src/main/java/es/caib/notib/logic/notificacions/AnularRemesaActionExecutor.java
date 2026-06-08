package es.caib.notib.logic.notificacions;

import es.caib.notib.client.domini.RespostaAnulacio;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.dto.anular.Anulacio;
import es.caib.notib.logic.intf.dto.anular.AnularDto;
import es.caib.notib.logic.intf.dto.anular.RespostaAnular;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class AnularRemesaActionExecutor implements BaseMutableResourceService.ActionExecutor<NotificacioResourceEntity, AnularDto, RespostaAnular> {

	private final NotificacioService notificacioService;

	@Override
	public RespostaAnular exec(String code, NotificacioResourceEntity entity, AnularDto params) throws ActionExecutionException {

		log.info("anualcio action exec");
		return null;
//		try {
//			return notificacioService.anular(params);
//		} catch (Exception ex) {
//			var msg = "Error inesperat anulant la remesa ";
//			log.error("[AnularRemesaActionExecutor] " + msg + "amb id " + entity.getId());
//			return RespostaAnular.builder().error(true).descripcioResposta(msg + ex.getMessage()).build();
//		}
	}

	@Override
	public void onChange(Serializable id, AnularDto previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, AnularDto target) {

	}
}

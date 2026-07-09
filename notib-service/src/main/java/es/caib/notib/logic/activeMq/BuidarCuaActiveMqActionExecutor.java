package es.caib.notib.logic.activeMq;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.dto.RespostaAccio;
import es.caib.notib.logic.intf.model.SeleccioStringForm;
import es.caib.notib.logic.intf.service.ActiveMqService;
import es.caib.notib.persist.resourceentity.ActiveMqResourceEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class BuidarCuaActiveMqActionExecutor implements BaseMutableResourceService.ActionExecutor<ActiveMqResourceEntity, SeleccioStringForm, Serializable> {

	private final ActiveMqService activeMqService;

	@Override
	public Serializable exec(String code, ActiveMqResourceEntity entity, SeleccioStringForm params) throws ActionExecutionException {

		try {
			if (params == null || params.getIds() == null) {
				throw new ActionExecutionException(ActiveMqResourceEntity.class, null, code, "La selecció no pot ser buida");
			}
			for (var seleccio : params.getIds()) {
				activeMqService.buidarCua(seleccio);
			}
			return RespostaAccio.builder().build();
		} catch (Exception ex) {
			var msg = "Error inesperat buidant la cua ";
			log.error("[BuidarCuaActiveMqActionExecutor] " + msg + " - " + ex.getMessage());
			throw new ActionExecutionException(ActiveMqResourceEntity.class, null, code, msg + ex.getMessage());
		}
	}

	@Override
	public void onChange(Serializable id, SeleccioStringForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, SeleccioStringForm target) {

	}

}

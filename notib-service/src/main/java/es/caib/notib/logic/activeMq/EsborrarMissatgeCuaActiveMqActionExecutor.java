package es.caib.notib.logic.activeMq;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.dto.RespostaAccio;
import es.caib.notib.logic.intf.model.SeleccioStringForm;
import es.caib.notib.logic.intf.service.ActiveMqService;
import es.caib.notib.persist.resourceentity.ActiveMqDetailResourceEntity;
import es.caib.notib.persist.resourceentity.ActiveMqResourceEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class EsborrarMissatgeCuaActiveMqActionExecutor implements BaseMutableResourceService.ActionExecutor<ActiveMqDetailResourceEntity, SeleccioStringForm, RespostaAccio<String>> {

	private final ActiveMqService activeMqService;

	@Override
	public RespostaAccio<String> exec(String code, ActiveMqDetailResourceEntity entity, SeleccioStringForm params) throws ActionExecutionException {

		try {
			if (params == null || params.getIds() == null) {
				throw new ActionExecutionException(ActiveMqResourceEntity.class, null, code, "La selecció no pot ser buida");
			}
			var ok = true;
			var resposta = new RespostaAccio<String>();
			List<String> errors = new ArrayList<>();
			for (var seleccio : params.getIds()) {
				ok = activeMqService.deleteMessage(params.getCodi(), seleccio);
				if (!ok) {
					errors.add(seleccio);
				}
			}
			resposta.setErrors(errors);
			return resposta;
		} catch (Exception ex) {
			var msg = "Error inesperat buidant eliminant missatges de la cua ";
			log.error("[EsborrarMissatgeCuaActiveMqActionExecutor] " + msg + " - " + ex.getMessage());
			throw new ActionExecutionException(ActiveMqResourceEntity.class, null, code, msg + ex.getMessage());
		}
	}

	@Override
	public void onChange(Serializable id, SeleccioStringForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, SeleccioStringForm target) {

	}

}

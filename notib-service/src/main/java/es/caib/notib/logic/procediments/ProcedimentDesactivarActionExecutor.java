package es.caib.notib.logic.procediments;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.model.ProcedimentResource;
import es.caib.notib.logic.intf.service.ProcedimentService;
import es.caib.notib.persist.resourceentity.ProcedimentResourceEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

/**
 * Acció per a desactivar un procediment.
 */
@Slf4j
@AllArgsConstructor
public class ProcedimentDesactivarActionExecutor implements BaseMutableResourceService.ActionExecutor<ProcedimentResourceEntity, Serializable, Boolean> {

	private final ProcedimentService procedimentService;

	@Override
	public Boolean exec(String code, ProcedimentResourceEntity entity, Serializable params) throws ActionExecutionException {

		try {
			procedimentService.updateActiu(entity.getId(), false);
			return true;
		} catch (Exception ex) {
			var msg = "Error al desactivar el procediment " + entity.getId() + ": ";
			log.error(msg, ex);
			throw new ActionExecutionException(ProcedimentResource.class, entity.getId(), code, msg + ex.getMessage(), ex);
		}
	}

	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {
	}
}

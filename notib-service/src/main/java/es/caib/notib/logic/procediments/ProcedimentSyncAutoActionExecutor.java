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
 * Acció per a marcar un procediment perquè s'actualitzi de forma automàtica.
 */
@Slf4j
@AllArgsConstructor
public class ProcedimentSyncAutoActionExecutor implements BaseMutableResourceService.ActionExecutor<ProcedimentResourceEntity, Serializable, Boolean> {

	private final ProcedimentService procedimentService;

	@Override
	public Boolean exec(String code, ProcedimentResourceEntity entity, Serializable params) throws ActionExecutionException {

		try {
			procedimentService.updateManual(entity.getId(), false);
			return true;
		} catch (Exception ex) {
			var msg = "Error al marcar el procediment " + entity.getId() + " per a sincronització automàtica: ";
			log.error(msg, ex);
			throw new ActionExecutionException(ProcedimentResource.class, entity.getId(), code, msg + ex.getMessage(), ex);
		}
	}

	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {
	}
}

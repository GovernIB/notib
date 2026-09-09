package es.caib.notib.logic.procediments;


import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.model.ProcedimentResource;
import es.caib.notib.logic.intf.service.ProcedimentService;
import es.caib.notib.persist.resourceentity.ProcedimentResourceEntity;
import es.caib.notib.persist.resourcerepository.EntitatResourceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

/**
 * Acció per a actualitzar un únic procediment amb la informació de ROLSAC.
 */
@Slf4j
@AllArgsConstructor
public class ProcedimentActualitzarActionExecutor implements BaseMutableResourceService.ActionExecutor<ProcedimentResourceEntity, Serializable, Boolean> {

	private final EntitatResourceRepository entitatResourceRepository;
	private final UserSessionHelper userSessionHelper;
	private final ProcedimentService procedimentService;

	@Override
	public Boolean exec(String code, ProcedimentResourceEntity entity, Serializable params) throws ActionExecutionException {

		var entitat = entitatResourceRepository.findById(userSessionHelper.getCurrentEntitatId());
		if (entitat.isEmpty()) {
			throw new ActionExecutionException(ProcedimentResource.class, entity != null ? entity.getId() : null, code, "Couldn't find current entitat in user session");
		}
		var entitatEntity = entitat.get();
		try {
			ConfigHelper.setEntitatCodi(entitatEntity.getCodi());
			var dto = new EntitatDto();
			dto.setId(entitatEntity.getId());
			dto.setCodi(entitatEntity.getCodi());
			dto.setDir3Codi(entitatEntity.getDir3Codi());
			return procedimentService.actualitzarProcediment(entity.getCodi(), dto);
		} catch (Exception ex) {
			var msg = "Error al actualitzar el procediment " + entity.getCodi() + ": ";
			log.error(msg, ex);
			throw new ActionExecutionException(ProcedimentResource.class, entity.getId(), code, msg + ex.getMessage(), ex);
		}
	}

	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {
	}
}

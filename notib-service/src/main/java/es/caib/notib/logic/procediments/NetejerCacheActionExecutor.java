package es.caib.notib.logic.procediments;


import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.model.OrganGestorResource;
import es.caib.notib.logic.intf.model.ProcedimentResource;
import es.caib.notib.logic.intf.service.ProcedimentService;
import es.caib.notib.persist.resourceentity.ProcedimentResourceEntity;
import es.caib.notib.persist.resourcerepository.EntitatResourceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

/**
 * Acció per a sincronitzar els òrgans gestors amb la informació de DIR3.
 */
@Slf4j
@AllArgsConstructor
public class NetejerCacheActionExecutor implements BaseMutableResourceService.ActionExecutor<ProcedimentResourceEntity, OrganGestorResource.OrganGestorDir3SyncForm, Boolean> {

	private final EntitatResourceRepository entitatResourceRepository;
	private final UserSessionHelper userSessionHelper;
	private final ProcedimentService procedimentService;
	private final Class<ProcedimentResource>  resourceClass;

	@Override
	public Boolean exec(String code, ProcedimentResourceEntity entity, OrganGestorResource.OrganGestorDir3SyncForm params) throws ActionExecutionException {

		var entitat = entitatResourceRepository.findById(userSessionHelper.getCurrentEntitatId());
		if (entitat.isEmpty()) {
			throw new ActionExecutionException(OrganGestorResource.class, null, code, "Couldn't find current entitat in user session");
		}
		var entitatEntity = entitat.get();
		try {
			ConfigHelper.setEntitatCodi(entitatEntity.getCodi());
			var dto = new EntitatDto();
			dto.setId(entitatEntity.getId());
			dto.setCodi(entitatEntity.getCodi());
			dto.setDir3Codi(entitatEntity.getDir3Codi());
			procedimentService.refrescarCache(dto);
			return true;
		} catch (Exception ex) {
			var msg = "Error al sincronitzar les oficines de la entitat " + entitatEntity.getCodi()+ ": ";
			log.error(msg, ex);
			msg += ex.getMessage();
			throw new ActionExecutionException(resourceClass, entity != null ? entity.getId() : null, code, msg, ex);
		}
	}
	@Override
	public void onChange(Serializable id, OrganGestorResource.OrganGestorDir3SyncForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, OrganGestorResource.OrganGestorDir3SyncForm target) {
	}
}

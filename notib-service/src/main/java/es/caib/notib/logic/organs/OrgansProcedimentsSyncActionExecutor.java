package es.caib.notib.logic.organs;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.OrganGestorFullSyncHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.model.OrganGestorResource;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import es.caib.notib.persist.resourcerepository.EntitatResourceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

/**
 * Acció per a sincronitzar en una sola execució òrgans, permisos, procediments, serveis
 * i oficines SIR amb DIR3/ROLSAC.
 */
@Slf4j
@AllArgsConstructor
public class OrgansProcedimentsSyncActionExecutor implements BaseMutableResourceService.ActionExecutor<OrganGestorResourceEntity, OrganGestorResource.OrganGestorDir3SyncForm, Boolean> {

	private final EntitatResourceRepository entitatResourceRepository;
	private final UserSessionHelper userSessionHelper;
	private final OrganGestorFullSyncHelper organGestorFullSyncHelper;
	private final Class<OrganGestorResource> resourceClass;

	@Override
	public Boolean exec(String code, OrganGestorResourceEntity entity, OrganGestorResource.OrganGestorDir3SyncForm params) throws ActionExecutionException {

		var entitat = entitatResourceRepository.findById(userSessionHelper.getCurrentEntitatId());
		if (entitat.isEmpty()) {
			throw new ActionExecutionException(OrganGestorResource.class, null, code, "Couldn't find current entitat in user session");
		}
		try {
			ConfigHelper.setEntitatCodi(entitat.get().getCodi());
			organGestorFullSyncHelper.sincronitzarTot(entitat.get());
			return true;
		} catch (Exception ex) {
			var msg = "Error a la sincronització combinada d'òrgans i procediments de l'entitat " + entitat.get().getCodi() + ": ";
			log.error(msg, ex);
			msg += ex.getMessage();
			throw new ActionExecutionException(resourceClass, entity != null ? entity.getId() : null, code, msg, ex);
		}
	}

	@Override
	public void onChange(Serializable id, OrganGestorResource.OrganGestorDir3SyncForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, OrganGestorResource.OrganGestorDir3SyncForm target) {
	}
}

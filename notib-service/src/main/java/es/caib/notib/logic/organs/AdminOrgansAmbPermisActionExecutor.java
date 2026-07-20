package es.caib.notib.logic.organs;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.base.service.BaseReadonlyResourceService;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.PerspectiveApplicationException;
import es.caib.notib.logic.intf.dto.anular.AnularDto;
import es.caib.notib.logic.intf.dto.anular.RespostaAnular;
import es.caib.notib.logic.intf.model.OrganGestorResource;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class AdminOrgansAmbPermisPerspectiveApplicator implements BaseMutableResourceService.ActionExecutor<OrganGestorResourceEntity, OrganGestorResource, List<OrganGestorResource> {
	private final OrganGestorService organGestorService;

	@Override
	public List<OrganGestorResource> exec(String code, OrganGestorResourceEntity entity, OrganGestorResource params) throws ActionExecutionException {
		return List.of();
	}

	@Override
	public void onChange(Serializable id, OrganGestorResource previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, OrganGestorResource target) {

	}

//		var organs = organGestorService.findAccessiblesByUsuariAndEntitatActual(21L);
}

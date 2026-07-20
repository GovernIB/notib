package es.caib.notib.logic.organs;

import es.caib.notib.logic.base.service.BaseReadonlyResourceService;
import es.caib.notib.logic.intf.base.exception.PerspectiveApplicationException;
import es.caib.notib.logic.intf.model.OrganGestorResource;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class AdminOrgansAmbPermisPerspectiveApplicator implements BaseReadonlyResourceService.PerspectiveApplicator<OrganGestorResourceEntity, OrganGestorResource> {

	private final OrganGestorService organGestorService;

	@Override
	public boolean applyMultiple(String code, List<OrganGestorResourceEntity> entities, List<OrganGestorResource> resources) throws PerspectiveApplicationException {

		var organs = organGestorService.findAccessiblesByUsuariAndEntitatActual(21L);
		return true;
	}

	@Override
	public void applySingle(String code, OrganGestorResourceEntity entity, OrganGestorResource resource) throws PerspectiveApplicationException {

	}
}

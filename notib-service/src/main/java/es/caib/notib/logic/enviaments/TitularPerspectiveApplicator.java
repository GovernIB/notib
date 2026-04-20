package es.caib.notib.logic.enviaments;

import es.caib.notib.logic.base.service.BaseReadonlyResourceService;
import es.caib.notib.logic.intf.base.exception.PerspectiveApplicationException;
import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;
import es.caib.notib.logic.intf.model.PersonaResource;
import es.caib.notib.persist.resourceentity.NotificacioEnviamentResourceEntity;
import lombok.RequiredArgsConstructor;


/**
 * Perspectiva per a emplenar els camps del titular d'un enviament.
 */
@RequiredArgsConstructor
public class TitularPerspectiveApplicator implements BaseReadonlyResourceService.PerspectiveApplicator<NotificacioEnviamentResourceEntity, NotificacioEnviamentResource> {

	@Override
	public void applySingle(String code, NotificacioEnviamentResourceEntity entity, NotificacioEnviamentResource resource) throws PerspectiveApplicationException {

		var titularEntity = entity.getTitular();
		var titularInfo = resource.getTitularInfo();
		if (titularInfo == null) {
			titularInfo = new PersonaResource();
		}
		titularInfo.setNif(titularEntity.getNif());
		titularInfo.setNom(titularEntity.getNom());
		titularInfo.setEmail(titularEntity.getEmail());
		titularInfo.setTelefon(titularEntity.getTelefon());
		titularInfo.setLlinatge1(titularEntity.getLlinatge1());
		titularInfo.setLlinatge2(titularEntity.getLlinatge2());
		resource.setTitularInfo(titularInfo);
	}
}

package es.caib.notib.logic.notificacions;

import es.caib.notib.logic.base.service.BaseReadonlyResourceService;
import es.caib.notib.logic.intf.base.exception.PerspectiveApplicationException;
import es.caib.notib.logic.intf.model.GrupResource;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.model.PagadorCieResource;
import es.caib.notib.logic.intf.model.PagadorPostalResource;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * Perspectiva per a emplenar els camps dels documents d'una remesa.
 */
@Slf4j
@RequiredArgsConstructor
public class GrupPerspectiveApplicator implements BaseReadonlyResourceService.PerspectiveApplicator<NotificacioResourceEntity, NotificacioResource> {

	@Override
	public void applySingle(String code, NotificacioResourceEntity entity, NotificacioResource resource) throws PerspectiveApplicationException {

			var grupEntity = entity.getGrup();
		if (grupEntity == null) {
			return;
		}
		resource.setGrupInfo(GrupResource.builder().nom(grupEntity.getNom()).codi(grupEntity.getCodi()).build());
	}

}

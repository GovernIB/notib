package es.caib.notib.logic.notificacions;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.base.service.BaseReadonlyResourceService;
import es.caib.notib.logic.intf.base.exception.PerspectiveApplicationException;
import es.caib.notib.logic.intf.dto.cie.CieDataDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalDataDto;
import es.caib.notib.logic.intf.model.DocumentResource;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.model.PagadorCieResource;
import es.caib.notib.logic.intf.model.PagadorPostalResource;
import es.caib.notib.persist.resourceentity.DocumentResourceEntity;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


/**
 * Perspectiva per a emplenar els camps dels documents d'una remesa.
 */
@Slf4j
@RequiredArgsConstructor
public class OperadorPostalCiePerspectiveApplicator implements BaseReadonlyResourceService.PerspectiveApplicator<NotificacioResourceEntity, NotificacioResource> {

	@Override
	public void applySingle(String code, NotificacioResourceEntity entity, NotificacioResource resource) throws PerspectiveApplicationException {

		var procedimentEntity = entity.getProcediment();
		var procComuOrganCie = procedimentEntity != null && procedimentEntity.isComu() && entity.getOrganGestor().getEntregaCie() != null;
		if (entity.isEntregaPostal() && (procedimentEntity != null && procedimentEntity.isEntregaCieActivaAlgunNivell() || procComuOrganCie)) {
			var entregaCieEntity = procedimentEntity.getEntregaCieEfectiva();
			entregaCieEntity = entregaCieEntity == null ? entity.getOrganGestor().getEntregaCie() : entregaCieEntity;
			var pagadorPostalEntity = entregaCieEntity.getPagadorPostal();
			var pagadorPostal = PagadorPostalResource.builder()
//					.organGestor() //TODO FALTA EMPLENAR AQUEST CAMP
					.contracteNum(pagadorPostalEntity.getContracteNum())
					.facturacioClientCodi(pagadorPostalEntity.getFacturacioClientCodi())
					.contracteDataVig(pagadorPostalEntity.getContracteDataVig())
					.build();
			resource.setOperadorPostalInfo(pagadorPostal);
			var pagadorCieEntity = entregaCieEntity.getPagadorCie();
			var pagadorCie = PagadorCieResource.builder()
//					.organGestorPagador() //TODO FALTA EMPLENAR AQUEST CAMP
					.contracteDataVig(pagadorCieEntity.getContracteDataVig())
					.build();
			resource.setOperadorCieInfo(pagadorCie);
		}
	}

}

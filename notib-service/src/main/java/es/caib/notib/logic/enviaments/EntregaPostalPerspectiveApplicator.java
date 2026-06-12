package es.caib.notib.logic.enviaments;

import es.caib.notib.logic.base.service.BaseReadonlyResourceService;
import es.caib.notib.logic.intf.model.EntregaPostalResource;
import es.caib.notib.logic.intf.base.exception.PerspectiveApplicationException;
import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;
import es.caib.notib.persist.resourceentity.NotificacioEnviamentResourceEntity;
import lombok.RequiredArgsConstructor;

/**
 * Perspectiva per a emplenar els camps de la entrega postal d'un enviament.
 */
@RequiredArgsConstructor
public class EntregaPostalPerspectiveApplicator implements BaseReadonlyResourceService.PerspectiveApplicator<NotificacioEnviamentResourceEntity, NotificacioEnviamentResource> {

	@Override
	public void applySingle(String code, NotificacioEnviamentResourceEntity entity, NotificacioEnviamentResource resource) throws PerspectiveApplicationException {

		var entregaPostalEntity = entity.getEntregaPostal();
		if (entregaPostalEntity == null) {
			return;
		}
		var entregaPostalInfo = resource.getEntregaPostalInfo();
		if (entregaPostalInfo == null) {
			entregaPostalInfo = new EntregaPostalResource();
		}
		entregaPostalInfo.setCieId(entregaPostalEntity.getCieId());
		entregaPostalInfo.setCieEstat(entregaPostalEntity.getCieEstat());
		entregaPostalInfo.setCieEstatData(entregaPostalEntity.getCieEstatData());
		entregaPostalInfo.setCieDatatOrigen(entregaPostalEntity.getCieDatatOrigen());
		entregaPostalInfo.setCieDatatReceptorNif(entregaPostalEntity.getCieDatatReceptorNif());
		entregaPostalInfo.setCieDatatReceptorNom(entregaPostalEntity.getCieDatatReceptorNom());
		entregaPostalInfo.setCieDatatNumSeguiment(entregaPostalEntity.getCieDatatNumSeguiment());
		entregaPostalInfo.setCieDatatErrorDescripcio(entregaPostalEntity.getCieDatatErrorDescripcio());
		entregaPostalInfo.setCieCertificacioData(entregaPostalEntity.getCieCertificacioData());
		entregaPostalInfo.setCieCertificacioMime(entregaPostalEntity.getCieCertificacioMime());
		entregaPostalInfo.setCieCertificacioOrigen(entregaPostalEntity.getCieCertificacioOrigen());
		entregaPostalInfo.setCieCertificacioMetadades(entregaPostalEntity.getCieCertificacioMetadades());
		entregaPostalInfo.setCieCertificacioCsv(entregaPostalEntity.getCieCertificacioCsv());
		entregaPostalInfo.setCieCertificacioTipus(entregaPostalEntity.getCieCertificacioTipus());
		entregaPostalInfo.setCieCertificacioArxiuTipus(entregaPostalEntity.getCieCertificacioArxiuTipus());
		entregaPostalInfo.setCieCertificacioNumSeguiment(entregaPostalEntity.getCieCertificacioNumSeguiment());
		entregaPostalInfo.setCieCertificacioArxiuNom(entregaPostalEntity.getCieCertificacioArxiuNom());
		resource.setEntregaPostalInfo(entregaPostalInfo);
	}
}


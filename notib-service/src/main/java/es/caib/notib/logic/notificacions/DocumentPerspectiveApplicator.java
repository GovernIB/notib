package es.caib.notib.logic.notificacions;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.base.service.BaseReadonlyResourceService;
import es.caib.notib.logic.intf.base.exception.PerspectiveApplicationException;
import es.caib.notib.logic.intf.model.DocumentResource;
import es.caib.notib.logic.intf.model.NotificacioResource;
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
public class DocumentPerspectiveApplicator implements BaseReadonlyResourceService.PerspectiveApplicator<NotificacioResourceEntity, NotificacioResource> {

	@Override
	public void applySingle(String code, NotificacioResourceEntity entity, NotificacioResource resource) throws PerspectiveApplicationException {

		List<DocumentResource> documents = new ArrayList<>();
		resource.setDocumentsInfo(documents);
		if (entity.getDocument() == null) {
			log.error("No es pot emplenar la prespectiva de documents per la remesa amb id " + entity.getId());
			return;
		}
		documents.add(emplenarResource(entity.getDocument()));

		if (!EnviamentTipus.SIR.equals(entity.getEnviamentTipus())) {
			return;
		}
		if (entity.getDocument2() != null) {
			documents.add(emplenarResource(entity.getDocument2()));
		}
		if (entity.getDocument3() != null) {
			documents.add(emplenarResource(entity.getDocument3()));
		}
		if (entity.getDocument4() != null) {
			documents.add(emplenarResource(entity.getDocument4()));
		}
		if (entity.getDocument5() != null) {
			documents.add(emplenarResource(entity.getDocument5()));
		}
	}

	private DocumentResource emplenarResource(DocumentResourceEntity document) {

		var docResource = new DocumentResource();
		docResource.setArxiuNom(document.getArxiuNom());
		docResource.setNormalitzat(document.getNormalitzat());
		docResource.setCsv(document.getCsv());
		return docResource;
	}
}

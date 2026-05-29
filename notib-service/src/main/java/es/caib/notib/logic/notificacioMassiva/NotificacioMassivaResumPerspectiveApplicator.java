package es.caib.notib.logic.notificacioMassiva;

import es.caib.notib.client.domini.CieEstat;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.logic.base.service.BaseReadonlyResourceService;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.intf.base.exception.PerspectiveApplicationException;
import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;
import es.caib.notib.logic.intf.model.NotificacioMassivaResource;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.model.PersonaResource;
import es.caib.notib.logic.intf.service.NotificacioMassivaService;
import es.caib.notib.persist.entity.NotificacioMassivaEntity;
import es.caib.notib.persist.resourceentity.EntregaPostalResourceEntity;
import es.caib.notib.persist.resourceentity.NotificacioMassivaResourceEntity;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import es.caib.notib.persist.resourceentity.PersonaResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


/**
 * Perspectiva per a emplenar el resum d'una notificacio massiva
 */
@Slf4j
@RequiredArgsConstructor
public class NotificacioMassivaResumPerspectiveApplicator implements BaseReadonlyResourceService.PerspectiveApplicator<NotificacioMassivaResourceEntity, NotificacioMassivaResource> {

	private final NotificacioMassivaService notificacioMassivaService;

	@Override
	public void applySingle(String code, NotificacioMassivaResourceEntity entity, NotificacioMassivaResource resource) throws PerspectiveApplicationException {

		try {
			ConfigHelper.setEntitatCodi(entity.getEntitat().getCodi());
			var info = notificacioMassivaService.getNotificacioMassivaInfo(entity.getEntitat().getId(), entity.getId());
			resource.setResum(info.getSummary());
		} catch (Exception ex) {
			log.error("[NotificacioMassivaResumPerspectiveApplicator] Error emplenant la prespectiva pel resum de la notifciacio massiva " + entity.getId(), ex);
		}
	}
}

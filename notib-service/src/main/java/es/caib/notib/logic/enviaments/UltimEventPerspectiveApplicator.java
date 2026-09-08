package es.caib.notib.logic.enviaments;

import es.caib.notib.logic.base.service.BaseReadonlyResourceService;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.intf.base.exception.PerspectiveApplicationException;
import es.caib.notib.logic.intf.dto.CallbackEstatEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.logic.intf.model.EntregaPostalResource;
import es.caib.notib.logic.intf.model.EventResource;
import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;
import es.caib.notib.persist.repository.NotificacioEventRepository;
import es.caib.notib.persist.resourceentity.NotificacioEnviamentResourceEntity;
import es.caib.notib.persist.resourcerepository.CallbackResourceRepository;
import es.caib.notib.persist.resourcerepository.EventResourceRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * Perspectiva per a emplenar els camps de la entrega postal d'un enviament.
 */
@RequiredArgsConstructor
public class UltimEventPerspectiveApplicator implements BaseReadonlyResourceService.PerspectiveApplicator<NotificacioEnviamentResourceEntity, NotificacioEnviamentResource> {

	private final MessageHelper messageHelper;
	private final EventResourceRepository eventResourceRepository;
	private final CallbackResourceRepository callbackResourceRepository;

	@Override
	public void applySingle(String code, NotificacioEnviamentResourceEntity entity, NotificacioEnviamentResource resource) throws PerspectiveApplicationException {

		var event = entity.getUltimEvent();
		var eventResource = new EventResource();
		eventResource.setTipus(event.getTipus());
		eventResource.setError(event.isError());
		eventResource.setErrorDescripcio(event.getErrorDescripcio());
		eventResource.setFiReintents(event.getFiReintents());
		eventResource.setUltimEventCie(NotificacioEventTipusEnumDto.CIE_ENVIAMENT.equals(event.getTipus()));
		if (entity.isSirFiPooling()) {
			eventResource.setFiReintents(true);
			var msg = messageHelper.getMessage("es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto." + NotificacioEventTipusEnumDto.SIR_FI_POOLING);
			eventResource.setFiReintentsDesc(msg);

		} else if (Boolean.TRUE.equals(event.getFiReintents())) {
			var msg = messageHelper.getMessage("notificacio.event.fi.reintents");
			var tipus = messageHelper.getMessage("es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto." + event.getTipus());
			eventResource.setFiReintentsDesc(msg + " -> " + tipus);
		}
		var e = eventResourceRepository.findLastApiCarpetaByEnviamentId(entity.getId());
		if (e != null && !e.isEmpty() && e.get(0).isError()) {
			eventResource.setNotificacioMovilErrorDesc(e.get(0).getErrorDescripcio());
		}
		var callback = callbackResourceRepository.findByEnviamentIdAndEstat(entity.getId(), CallbackEstatEnumDto.ERROR);
		if (callback != null) {
			eventResource.setErrorLastCallback(true);
			eventResource.setCallbackFiReintents(true);
			eventResource.setCallbackFiReintentsDesc(messageHelper.getMessage("callback.fi.reintents"));
		}
		resource.setUltimEventInfo(eventResource);
	}
}


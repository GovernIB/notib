package es.caib.notib.logic.notificacions;

import es.caib.notib.client.domini.CieEstat;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.logic.base.service.BaseReadonlyResourceService;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.intf.base.exception.PerspectiveApplicationException;
import es.caib.notib.logic.intf.dto.CallbackEstatEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioErrorTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.model.PersonaResource;
import es.caib.notib.logic.intf.util.DatesUtils;
import es.caib.notib.persist.resourceentity.CallbackResourceEntity;
import es.caib.notib.persist.resourceentity.EntregaPostalResourceEntity;
import es.caib.notib.persist.resourceentity.EventResourceEntity;
import es.caib.notib.persist.resourceentity.NotificacioEnviamentResourceEntity;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import es.caib.notib.persist.resourceentity.PersonaResourceEntity;
import es.caib.notib.persist.resourcerepository.CallbackResourceRepository;
import es.caib.notib.persist.resourcerepository.EventResourceRepository;
import es.caib.notib.persist.resourcerepository.NotificacioEnviamentResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


/**
 * Perspectiva per a emplenar els camps dels enviaments d'una remesa.
 */
@Slf4j
@RequiredArgsConstructor
public class NotificacioDetallPerspectiveApplicator implements BaseReadonlyResourceService.PerspectiveApplicator<NotificacioResourceEntity, NotificacioResource> {

	private final NotificacioEnviamentResourceRepository notificacioEnviamentResourceRepository;
	private final ConfigHelper configHelper;
	private final CallbackResourceRepository callbackResourceRepository;
	private final EventResourceRepository eventResourceRepository;
	private final MessageHelper messageHelper;
	private final NotibPermissionHelper notibPermissionHelper;

	@Override
	public void applySingle(String code, NotificacioResourceEntity entity, NotificacioResource resource) throws PerspectiveApplicationException {

		var enviamentsPendentsNotifica = notificacioEnviamentResourceRepository.findEnviamentsPendentsNotificaByNotificacio(entity);
		resource.setHasEnviamentsPendents(enviamentsPendentsNotifica != null && !enviamentsPendentsNotifica.isEmpty());
		var llindarDies = configHelper.getConfigAsInteger("es.caib.notib.llindar.dies.enviament.remeses");
		resource.setNotificacioAntiga(DatesUtils.isNowAfterDate(entity.getCreatedDate(), llindarDies));
		resource.setComunicacioSir(entity.isComunicacioSir());
		resource.setPermisProcessar(hasPermisProcessar(entity));
//		legacyHelper.actualitzarColumnaEstat(entity);
		//CALLBACKS
		var pendents = callbackResourceRepository.findByNotificacioIdAndEstatOrderByDataDesc(entity.getId(), CallbackEstatEnumDto.PENDENT);
		resource.setEventsCallbackPendent(entity.isTipusUsuariAplicacio() && pendents != null && !pendents.isEmpty());
		var data = pendents != null && !pendents.isEmpty() && pendents.get(0).getData() != null ? pendents.get(0).getData() : null;
		resource.setDataCallbackPendent(data);
		int callbackFiReintents = 0;
		NotificacioEnviamentResourceEntity enviament;
		var motiuAnulacio = "";
		var entregaPostal = false;
		EventResourceEntity eventError;
		CallbackResourceEntity callback;
		List<EventResourceEntity> eventNotMovil;
		List<EventResourceEntity> lastErrorEvent = new ArrayList<>();
		for (var enviamentResource : entity.getEnviaments()) {
			enviament = notificacioEnviamentResourceRepository.findById(enviamentResource.getId()).get();
			;
			if (entity.isComunicacioSir()) {
				resource.setRegistreEstat(enviament.getRegistreEstat());
			}
			if (!entregaPostal && enviament.getEntregaPostal() != null) {
				entregaPostal = true;
			}
			boolean plazoAmpliado = resource.isPlazoAmpliado();
			resource.setPlazoAmpliado(plazoAmpliado || enviament.isPlazoAmpliado());
			boolean anulat = resource.isAnulat();
			resource.setAnulat(anulat || enviament.isAnulat());
			motiuAnulacio = enviament.getMotiuAnulacio();
			eventError = enviament.getUltimEvent();
			if (eventError != null && eventError.isError()) {
				lastErrorEvent.add(eventError);
			}
			eventNotMovil = eventResourceRepository.findLastApiCarpetaByEnviamentId(enviament.getId());
			if (eventNotMovil != null && !eventNotMovil.isEmpty() && eventNotMovil.get(0).isError()) {
				resource.getNotificacionsMovilErrorDesc().add(eventNotMovil.get(0).getErrorDescripcio());
			}
			if (enviament
				.isSirFiPooling()) {
				resource.setFiReintents(true);
				resource.setFiReintentsDesc(messageHelper.getMessage("es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto." + NotificacioEventTipusEnumDto.SIR_FI_POOLING));
			}
			callback = callbackResourceRepository.findByEnviamentIdAndEstat(enviament.getId(), CallbackEstatEnumDto.ERROR);
			if (callback == null) {
				continue;
			}
			resource.setErrorLastCallback(callback.isError());
			resource.setCallbackFiReintents(true);
			resource.setCallbackFiReintentsDesc(messageHelper.getMessage("callback.fi.reintents"));
			callbackFiReintents++;
		}
		resource.setMotiuAnulacio(motiuAnulacio);
		if (resource.getNotificacionsMovilErrorDesc().size() > 1) {
			List<String> desc = new ArrayList<>();
			desc.add(messageHelper.getMessage("api.carpeta.send.notificacio.movil.error"));
			resource.setNotificacionsMovilErrorDesc(desc);
		}
		if (callbackFiReintents > 0) {
			resource.setCallbackFiReintents(true);
			resource.setCallbackFiReintentsDesc(messageHelper.getMessage("callback.fi.reintents"));
		}
		if (!lastErrorEvent.isEmpty()) {
			String msg = "";
			String tipus = "";
			StringBuilder m = new StringBuilder();
			int env = 1;
			var fiReintents = false;
			for (var event : lastErrorEvent) {

				msg = messageHelper.getMessage("notificacio.event.fi.reintents");
				var et = NotificacioEventTipusEnumDto.SIR_CONSULTA.equals(event.getTipus()) && event.getEnviament().isSirFiPooling() ? NotificacioEventTipusEnumDto.SIR_FI_POOLING : event.getTipus();
				tipus = messageHelper.getMessage("es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto." + et);
				m.append("Env ").append(env).append(": ").append(msg).append(" -> ").append(tipus).append("\n");
				env++;
				fiReintents = fiReintents || event.getFiReintents();
				if (entregaPostal && NotificacioEventTipusEnumDto.CIE_ENVIAMENT.equals(event.getTipus())) {
					resource.setErrorEntregaPostal(true);
				}
			}
			resource.setFiReintentsDesc(m.toString());
			resource.setFiReintents(fiReintents);
			resource.setNotificaErrorDescripcio(lastErrorEvent.size() > 1 ? messageHelper.getMessage("error.notificacio.enviaments") : lastErrorEvent.get(0).getErrorDescripcio());
			// TODO S'HA DE POSAR PER TOTS ELS EVENTS
			resource.setNotificaErrorData(lastErrorEvent.get(0).getData());
			resource.setNoticaErrorEventTipus(lastErrorEvent.get(0).getTipus());
			// Obtenir error dels events
			resource.setNotificaErrorTipus(getErrorTipus(lastErrorEvent.get(0)));
		}

	}


	private boolean hasPermisProcessar(NotificacioResourceEntity notificacio) {
		return notibPermissionHelper.organGestorPermissionAllowed(notificacio.getOrganGestor().getId(), es.caib.notib.logic.intf.acl.ExtendedPermission.PROCESSAR);
	}

	private NotificacioErrorTipusEnumDto getErrorTipus(EventResourceEntity lastErrorEvent) {

		if (lastErrorEvent == null || !NotificacioEstatEnumDto.ENVIADA.equals(lastErrorEvent.getNotificacio().getEstat())) {
			return null;
		}
		if (NotificacioEventTipusEnumDto.SIR_CONSULTA.equals(lastErrorEvent.getTipus()) && Boolean.TRUE.equals(lastErrorEvent.getFiReintents())) {
			return NotificacioErrorTipusEnumDto.ERROR_REINTENTS_SIR;
		}
		if (NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA.equals(lastErrorEvent.getTipus()) && Boolean.TRUE.equals(lastErrorEvent.getFiReintents())) {
			return NotificacioErrorTipusEnumDto.ERROR_REINTENTS_CONSULTA;
		}
		return null;
	}

}

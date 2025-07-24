package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioCertificacioDto;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioCertificacioDto.TipusActInfo;
import es.caib.notib.logic.intf.statemachine.dto.ConsultaNotificaDto;
import es.caib.notib.logic.intf.statemachine.events.ConsultaNotificaRequest;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Helper per notificacions
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class EnviamentHelper {

	@Resource
	private MessageHelper messageHelper;
	@Autowired
	private NotificaHelper notificaHelper;
	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private ConfigHelper configHelper;

	public void refrescarEnviamentsExpirats() {
		refrescarEnviamentsExpirats(new ProgresActualitzacioCertificacioDto());
	}

	public void refrescarEnviamentsExpirats(@NonNull ProgresActualitzacioCertificacioDto progres) {

		log.info("[EXPIRATS] Execució procés actualització enviaments expirats");
		var auth = SecurityContextHolder.getContext().getAuthentication();
		var username = auth == null ? "schedulled" : auth.getName();
		var info = new IntegracioInfo(IntegracioCodi.NOTIFICA, "Actualització d'enviaments expirats sense certificació",
				IntegracioAccioTipusEnumDto.PROCESSAR, new AccioParam("Usuari encarregat: ", username));
		var enviamentsIds = notificacioEnviamentRepository.findIdExpiradesAndNotificaCertificacioDataNull();
		if (enviamentsIds == null || enviamentsIds.isEmpty()) {
			log.debug("[EXPIRATS] No s'han trobat enviaments expirats.");
			var msgInfoEnviamentsEmpty = messageHelper.getMessage("procediment.actualitzacio.auto.processar.enviaments.expirats.empty");
			progres.addInfo(TipusActInfo.WARNING, msgInfoEnviamentsEmpty);
			info.getParams().add(new AccioParam("Msg. Títol:", msgInfoEnviamentsEmpty));
			progres.setProgres(100);
			integracioHelper.addAccioOk(info);
			return;
		}
		log.debug(String.format("[EXPIRATS] Actualitzant %d enviaments expirats", enviamentsIds.size()));
		var msgInfoInici = messageHelper.getMessage("procediment.actualitzacio.auto.processar.enviaments.expirats.inici");
		progres.setNumEnviamentsExpirats(enviamentsIds.size());
		progres.addInfo(TipusActInfo.TITOL, msgInfoInici);
		info.getParams().add(new AccioParam("Msg. Títol:", msgInfoInici));
		for (var enviamentId : enviamentsIds) {
			progres.incrementProcedimentsActualitzats();
			try {
				enviamentRefrescarEstat(enviamentId, progres, info);
			} catch (Exception ex) {
				progres.addInfo(TipusActInfo.ERROR, messageHelper.getMessage("procediment.actualitzacio.auto.processar.enviaments.expirats.actualitzant.ko", new Object[] {enviamentId}));
				log.error("No s'ha pogut refrescar l'estat de l'enviament (enviamentId=" + enviamentId + ")", ex);
			}
		}
		progres.setProgres(100);
		integracioHelper.addAccioOk(info);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateDEHCertNovaConsulta(Long enviamentId) {

		var enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
		enviament.updateDEHCertNovaConsulta(configHelper.getConfigAsInteger(PropertiesConstants.ENVIAMENT_DEH_REFRESCAR_CERT_PENDENTS_RATE));
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateCIECertNovaConsulta(Long enviamentId) {

		var enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
		enviament.updateCIECertNovaConsulta(configHelper.getConfigAsInteger(PropertiesConstants.ENVIAMENT_CIE_REFRESCAR_CERT_PENDENTS_RATE));
	}

	private void enviamentRefrescarEstat(Long enviamentId, ProgresActualitzacioCertificacioDto progres, IntegracioInfo info) {

		long t0 = System.currentTimeMillis();
		log.debug("Refrescant l'estat de la notificació de Notific@ (enviamentId=" + enviamentId + ")");
		try {
			var msgInfoUpdating = messageHelper.getMessage("procediment.actualitzacio.auto.processar.enviaments.expirats.actualitzant", new Object[] {enviamentId});
			progres.addInfo(TipusActInfo.INFO, msgInfoUpdating);
			info.getParams().add(new AccioParam("Msg. procés:", msgInfoUpdating + " [" + progres.getProgres() + "%]"));
			var consulta = ConsultaNotificaRequest.builder().consultaNotificaDto(ConsultaNotificaDto.builder().id(enviamentId).build()).build();
			notificaHelper.enviamentRefrescarEstat(consulta, true);
			var msgInfoUpdated = messageHelper.getMessage("procediment.actualitzacio.auto.processar.enviaments.expirats.actualitzant.ok", new Object[] {enviamentId});
			progres.addInfo(TipusActInfo.SUB_INFO, msgInfoUpdated);
			info.getParams().add(new AccioParam("Msg. procés:", msgInfoUpdated));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			log.debug(String.format("Fi intent actualització estat de la notificació de Notific@ (enviamentId=%d). Temps = %.1f s", enviamentId, (System.currentTimeMillis() - t0) / 1e3));
		}
	}
}

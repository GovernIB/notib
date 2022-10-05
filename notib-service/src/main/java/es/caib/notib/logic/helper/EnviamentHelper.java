package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioCertificacioDto;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioCertificacioDto.TipusActInfo;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Helper per notificacions
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class EnviamentHelper {

	@Autowired
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
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth == null ? "schedulled" : auth.getName();
		IntegracioInfo info = new IntegracioInfo(
				IntegracioHelper.INTCODI_NOTIFICA,
				"Actualització d'enviaments expirats sense certificació",
				IntegracioAccioTipusEnumDto.PROCESSAR,
				new AccioParam("Usuari encarregat: ", username));
		List<Long> enviamentsIds = notificacioEnviamentRepository.findIdExpiradesAndNotificaCertificacioDataNull();
		if (enviamentsIds == null || enviamentsIds.isEmpty()) {
			log.debug("[EXPIRATS] No s'han trobat enviaments expirats.");
			String msgInfoEnviamentsEmpty = messageHelper.getMessage("procediment.actualitzacio.auto.processar.enviaments.expirats.empty");
			progres.addInfo(TipusActInfo.WARNING, msgInfoEnviamentsEmpty);
			info.getParams().add(new AccioParam("Msg. Títol:", msgInfoEnviamentsEmpty));
			progres.setProgres(100);
		} else {
			log.debug(String.format("[EXPIRATS] Actualitzant %d enviaments expirats", enviamentsIds.size()));
			String msgInfoInici = messageHelper.getMessage("procediment.actualitzacio.auto.processar.enviaments.expirats.inici");
			progres.setNumEnviamentsExpirats(enviamentsIds.size());
			progres.addInfo(TipusActInfo.TITOL, msgInfoInici);
			info.getParams().add(new AccioParam("Msg. Títol:", msgInfoInici));
			for (Long enviamentId : enviamentsIds) {
				progres.incrementProcedimentsActualitzats();
				try {
					enviamentRefrescarEstat(
							enviamentId,
							progres,
							info);
				} catch (Exception ex) {
					progres.addInfo(TipusActInfo.ERROR, messageHelper.getMessage("procediment.actualitzacio.auto.processar.enviaments.expirats.actualitzant.ko", new Object[] {enviamentId}));
					log.error("No s'ha pogut refrescar l'estat de l'enviament (enviamentId=" + enviamentId + ")", ex);
				}
			}
			progres.setProgres(100);
		}
		integracioHelper.addAccioOk(info);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateDEHCertNovaConsulta(Long enviamentId) {
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
		enviament.updateDEHCertNovaConsulta(configHelper.getConfigAsInteger(PropertiesConstants.ENVIAMENT_DEH_REFRESCAR_CERT_PENDENTS_RATE));
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateCIECertNovaConsulta(Long enviamentId) {
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
		enviament.updateCIECertNovaConsulta(configHelper.getConfigAsInteger(PropertiesConstants.ENVIAMENT_CIE_REFRESCAR_CERT_PENDENTS_RATE));
	}

	private void enviamentRefrescarEstat(
			Long enviamentId,
			ProgresActualitzacioCertificacioDto progres,
			IntegracioInfo info) {
		long t0 = System.currentTimeMillis();
		log.debug("Refrescant l'estat de la notificació de Notific@ (enviamentId=" + enviamentId + ")");
		try {
			String msgInfoUpdating = messageHelper.getMessage("procediment.actualitzacio.auto.processar.enviaments.expirats.actualitzant", new Object[] {enviamentId});
			progres.addInfo(TipusActInfo.INFO, msgInfoUpdating);
			info.getParams().add(new AccioParam("Msg. procés:", msgInfoUpdating + " [" + progres.getProgres() + "%]"));
			notificaHelper.enviamentRefrescarEstat(enviamentId, true);
			String msgInfoUpdated = messageHelper.getMessage("procediment.actualitzacio.auto.processar.enviaments.expirats.actualitzant.ok", new Object[] {enviamentId});
			progres.addInfo(TipusActInfo.SUB_INFO, msgInfoUpdated);
			info.getParams().add(new AccioParam("Msg. procés:", msgInfoUpdated));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			log.debug(String.format("Fi intent actualització estat de la notificació de Notific@ (enviamentId=%d). Temps = %.1f s",
					enviamentId, (System.currentTimeMillis() - t0) / 1e3));
		}
	}
}

package es.caib.notib.core.helper;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.ProgresActualitzacioCertificacioDto;
import es.caib.notib.core.api.dto.ProgresActualitzacioCertificacioDto.TipusActInfo;

/**
 * Helper per notificacions
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class NotificacioHelper {
	
	@Resource
	private MessageHelper messageHelper;
	@Autowired
	private NotificaHelper notificaHelper;
	
	@Transactional
	public void enviamentRefrescarEstat(
			Long enviamentId, 
			ProgresActualitzacioCertificacioDto progres) {
		logger.debug("Refrescant l'estat de la notificació de Notific@ (enviamentId=" + enviamentId + ")");
		try {
			progres.incrementProcedimentsActualitzats();
			progres.addInfo(TipusActInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.enviaments.expirats.actualitzant", new Object[] {enviamentId}));
			notificaHelper.enviamentRefrescarEstat(enviamentId);
			progres.addInfo(TipusActInfo.SUB_INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.enviaments.expirats.actualitzant.ok", new Object[] {enviamentId}));
		} catch (Exception ex) {
			progres.addInfo(TipusActInfo.ERROR, messageHelper.getMessage("procediment.actualitzacio.auto.processar.enviaments.expirats.actualitzant.ko", new Object[] {enviamentId}));
			logger.error("No s'ha pogut refrescar l'estat de l'enviament (enviamentId=" + enviamentId + ")", ex);
		}
	}
	
	private static final Logger logger = LoggerFactory.getLogger(NotificacioHelper.class);

}

package es.caib.notib.core.helper;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.AccioParam;
import es.caib.notib.core.api.dto.IntegracioInfo;
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
	@Autowired
	private IntegracioHelper integracioHelper;
	
	@Transactional
	public void enviamentRefrescarEstat(
			Long enviamentId, 
			ProgresActualitzacioCertificacioDto progres,
			IntegracioInfo info) {
		logger.debug("Refrescant l'estat de la notificació de Notific@ (enviamentId=" + enviamentId + ")");
		try {
			progres.incrementProcedimentsActualitzats();
			String msgInfoUpdating = messageHelper.getMessage("procediment.actualitzacio.auto.processar.enviaments.expirats.actualitzant", new Object[] {enviamentId});
			progres.addInfo(TipusActInfo.INFO, msgInfoUpdating);
			info.getParams().add(new AccioParam("Msg. procés:", msgInfoUpdating + " [" + progres.getProgres() + "%]"));
			notificaHelper.enviamentRefrescarEstat(enviamentId);
			String msgInfoUpdated = messageHelper.getMessage("procediment.actualitzacio.auto.processar.enviaments.expirats.actualitzant.ok", new Object[] {enviamentId});
			progres.addInfo(TipusActInfo.SUB_INFO, msgInfoUpdated);
			info.getParams().add(new AccioParam("Msg. procés:", msgInfoUpdated));
		} catch (Exception ex) {
			progres.addInfo(TipusActInfo.ERROR, messageHelper.getMessage("procediment.actualitzacio.auto.processar.enviaments.expirats.actualitzant.ko", new Object[] {enviamentId}));
			logger.error("No s'ha pogut refrescar l'estat de l'enviament (enviamentId=" + enviamentId + ")", ex);

			integracioHelper.addAccioError(info, "Error actualitzant enviaments expirats: ", ex);
		}
	}
	
	private static final Logger logger = LoggerFactory.getLogger(NotificacioHelper.class);

}

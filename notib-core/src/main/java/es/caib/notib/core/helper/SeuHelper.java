/**
 * 
 */
package es.caib.notib.core.helper;

import java.util.Date;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioSeuEstatEnumDto;
import es.caib.notib.core.entity.NotificacioDestinatariEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.plugin.seu.SeuNotificacioEstat;
import es.caib.notib.plugin.seu.SeuNotificacioResultat;

/**
 * Mètodes per a interactuar amb la seu electrònica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class SeuHelper {

	@Autowired
	private PluginHelper pluginHelper;



	@Transactional
	public void enviament(NotificacioDestinatariEntity destinatari) {
		String registreNumero = null;
		Date registreData = null;
		NotificacioSeuEstatEnumDto estat = null;
		NotificacioEventEntity event;
		try {
			SeuNotificacioResultat resultat = pluginHelper.seuNotificacioDestinatariEnviar(destinatari);
			registreNumero = resultat.getRegistreNumero();
			registreData = resultat.getRegistreData();
			estat = NotificacioSeuEstatEnumDto.ENVIADA;
			event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.SEU_CAIB_ENVIAMENT,
					destinatari.getNotificacio()).
					notificacioDestinatari(destinatari).
					descripcio("registreNumero=" + registreNumero + ", registreData=" + registreData).
					build();
		} catch (Exception ex) {
			estat = NotificacioSeuEstatEnumDto.ERROR_ENVIAMENT;
			event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.SEU_CAIB_ENVIAMENT,
					destinatari.getNotificacio()).
					notificacioDestinatari(destinatari).
					error(true).
					errorDescripcio(ExceptionUtils.getStackTrace(ex)).
					build();
			destinatari.updateSeuError(
					true,
					event);
		}
		destinatari.updateSeuEnviament(
				registreNumero,
				registreData,
				estat);
	}

	@Transactional
	public boolean consultaEstat(NotificacioDestinatariEntity destinatari) {
		Date dataFi = null;
		NotificacioSeuEstatEnumDto estat;
		NotificacioEventEntity event;
		boolean estatActualitzat;
		try {
			SeuNotificacioEstat notificacioEstat = pluginHelper.seuNotificacioComprovarEstat(destinatari);
			if (notificacioEstat.getEstat() != null) {
				switch (notificacioEstat.getEstat()) {
				case LLEGIDA:
					estat = NotificacioSeuEstatEnumDto.LLEGIDA;
					dataFi = notificacioEstat.getData();
					break;
				case REBUTJADA:
					estat = NotificacioSeuEstatEnumDto.REBUTJADA;
					dataFi = notificacioEstat.getData();
					break;
				case PENDENT:
				default:
					estat = NotificacioSeuEstatEnumDto.ENVIADA;
					break;
				}
			} else {
				estat = destinatari.getSeuEstat();
			}
			event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.SEU_CAIB_CONSULTA_ESTAT,
					destinatari.getNotificacio()).
					notificacioDestinatari(destinatari).
					descripcio((estat != null) ? estat.toString() : null).
					build();
			estatActualitzat = true;
		} catch (Exception ex) {
			estat = NotificacioSeuEstatEnumDto.ERROR_PROCESSAMENT;
			event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.SEU_CAIB_CONSULTA_ESTAT,
					destinatari.getNotificacio()).
					notificacioDestinatari(destinatari).
					error(true).
					errorDescripcio(ExceptionUtils.getStackTrace(ex)).
					build();
			destinatari.updateSeuError(
					true,
					event);
			estatActualitzat = false;
		}
		destinatari.updateSeuEstat(
				dataFi,
				estat);
		destinatari.getNotificacio().updateEventAfegir(event);
		return estatActualitzat;
	}

}

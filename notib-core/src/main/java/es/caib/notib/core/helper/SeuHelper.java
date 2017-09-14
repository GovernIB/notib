/**
 * 
 */
package es.caib.notib.core.helper;

import java.util.Date;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.entity.NotificacioDestinatariEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.repository.NotificacioDestinatariRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
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
	private NotificacioDestinatariRepository notificacioDestinatariRepository;
	@Autowired
	private NotificacioEventRepository notificacioEventRepository;

	@Autowired
	private PluginHelper pluginHelper;



	@Transactional
	public void enviament(Long notificacioDestinatariId) {
		NotificacioDestinatariEntity destinatari = notificacioDestinatariRepository.findOne(
				notificacioDestinatariId);
		String registreNumero = null;
		Date registreData = null;
		NotificacioDestinatariEstatEnumDto estat = destinatari.getSeuEstat();
		NotificacioEventEntity event;
		try {
			SeuNotificacioResultat resultat = pluginHelper.seuNotificacioDestinatariEnviar(destinatari);
			registreNumero = resultat.getRegistreNumero();
			registreData = resultat.getRegistreData();
			estat = NotificacioDestinatariEstatEnumDto.NOTIB_ENVIADA;
			event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.SEU_CAIB_ENVIAMENT,
					destinatari.getNotificacio()).
					notificacioDestinatari(destinatari).
					descripcio("registreNumero=" + registreNumero + ", registreData=" + registreData).
					build();
		} catch (Exception ex) {
			event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.SEU_CAIB_ENVIAMENT,
					destinatari.getNotificacio()).
					notificacioDestinatari(destinatari).
					error(true).
					errorDescripcio(ExceptionUtils.getStackTrace(ex)).
					build();
			destinatari.updateSeuError(
					true,
					event,
					false);
		}
		notificacioEventRepository.save(event);
		destinatari.getNotificacio().updateEventAfegir(event);
		destinatari.updateSeuEnviament(
				registreNumero,
				registreData,
				estat);
	}

	@Transactional
	public boolean consultaEstat(Long notificacioDestinatariId) {
		NotificacioDestinatariEntity destinatari = notificacioDestinatariRepository.findOne(
				notificacioDestinatariId);
		Date dataFi = null;
		NotificacioDestinatariEstatEnumDto estat = destinatari.getSeuEstat();
		NotificacioEventEntity event;
		boolean estatActualitzat;
		try {
			SeuNotificacioEstat notificacioEstat = pluginHelper.seuNotificacioComprovarEstat(
					destinatari);
			if (notificacioEstat.getEstat() != null) {
				switch (notificacioEstat.getEstat()) {
				case LLEGIDA:
					estat = NotificacioDestinatariEstatEnumDto.LLEGIDA;
					dataFi = notificacioEstat.getData();
					break;
				case REBUTJADA:
					estat = NotificacioDestinatariEstatEnumDto.REBUTJADA;
					dataFi = notificacioEstat.getData();
					break;
				case PENDENT:
				default:
					estat = NotificacioDestinatariEstatEnumDto.PENDENT_SEU;
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
			logger.error(
					"Error al consultar l'estat de la notificació a la seu electrònica (" +
					"notificacioId=" + destinatari.getNotificacio().getId() + ", " +
					"notificaIdentificador=" + destinatari.getNotificaIdentificador() + ", " +
					"expedientId=" + destinatari.getNotificacio().getSeuExpedientIdentificadorEni() + ", " +
					"expedientUnitatOrganitzativa=" + destinatari.getNotificacio().getSeuExpedientUnitatOrganitzativa() + ", " +
					"expedientSerieDocumental=" + destinatari.getNotificacio().getSeuExpedientSerieDocumental() + ", " +
					"expedientTitol=" + destinatari.getNotificacio().getSeuExpedientTitol() + ", " +
					"registreNumero=" + destinatari.getSeuRegistreNumero() + ")",
					ex);
			event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.SEU_CAIB_CONSULTA_ESTAT,
					destinatari.getNotificacio()).
					notificacioDestinatari(destinatari).
					error(true).
					errorDescripcio(ExceptionUtils.getStackTrace(ex)).
					build();
			destinatari.updateSeuError(
					true,
					event,
					true);
			estatActualitzat = false;
		}
		destinatari.updateSeuEstat(
				dataFi,
				estat);
		notificacioEventRepository.save(event);
		destinatari.getNotificacio().updateEventAfegir(event);
		return estatActualitzat;
	}

	private static final Logger logger = LoggerFactory.getLogger(SeuHelper.class);

}

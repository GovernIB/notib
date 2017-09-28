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
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
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
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private NotificacioEventRepository notificacioEventRepository;

	@Autowired
	private PluginHelper pluginHelper;



	@Transactional
	public void enviament(Long notificacioEnviamentId) {
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(
				notificacioEnviamentId);
		String registreNumero = null;
		Date registreData = null;
		NotificacioDestinatariEstatEnumDto estat = enviament.getSeuEstat();
		NotificacioEventEntity event;
		try {
			SeuNotificacioResultat resultat = pluginHelper.seuNotificacioDestinatariEnviar(enviament);
			registreNumero = resultat.getRegistreNumero();
			registreData = resultat.getRegistreData();
			estat = NotificacioDestinatariEstatEnumDto.NOTIB_ENVIADA;
			event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.SEU_CAIB_ENVIAMENT,
					enviament.getNotificacio()).
					enviament(enviament).
					descripcio("registreNumero=" + registreNumero + ", registreData=" + registreData).
					build();
		} catch (Exception ex) {
			event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.SEU_CAIB_ENVIAMENT,
					enviament.getNotificacio()).
					enviament(enviament).
					error(true).
					errorDescripcio(ExceptionUtils.getStackTrace(ex)).
					build();
			enviament.updateSeuError(
					true,
					event,
					false);
		}
		notificacioEventRepository.save(event);
		enviament.getNotificacio().updateEventAfegir(event);
		enviament.updateSeuEnviament(
				registreNumero,
				registreData,
				estat);
	}

	@Transactional
	public boolean consultaEstat(Long notificacioDestinatariId) {
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(
				notificacioDestinatariId);
		Date dataFi = null;
		NotificacioDestinatariEstatEnumDto estat = enviament.getSeuEstat();
		NotificacioEventEntity event;
		boolean estatActualitzat;
		try {
			SeuNotificacioEstat notificacioEstat = pluginHelper.seuNotificacioComprovarEstat(
					enviament);
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
				estat = enviament.getSeuEstat();
			}
			event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.SEU_CAIB_CONSULTA_ESTAT,
					enviament.getNotificacio()).
					enviament(enviament).
					descripcio((estat != null) ? estat.toString() : null).
					build();
			estatActualitzat = true;
		} catch (Exception ex) {
			logger.error(
					"Error al consultar l'estat de la notificació a la seu electrònica (" +
					"notificacioId=" + enviament.getNotificacio().getId() + ", " +
					"notificaIdentificador=" + enviament.getNotificaIdentificador() + ", " +
					"expedientId=" + enviament.getNotificacio().getSeuExpedientIdentificadorEni() + ", " +
					"expedientUnitatOrganitzativa=" + enviament.getNotificacio().getSeuExpedientUnitatOrganitzativa() + ", " +
					"expedientSerieDocumental=" + enviament.getNotificacio().getSeuExpedientSerieDocumental() + ", " +
					"expedientTitol=" + enviament.getNotificacio().getSeuExpedientTitol() + ", " +
					"registreNumero=" + enviament.getSeuRegistreNumero() + ")",
					ex);
			event = NotificacioEventEntity.getBuilder(
					NotificacioEventTipusEnumDto.SEU_CAIB_CONSULTA_ESTAT,
					enviament.getNotificacio()).
					enviament(enviament).
					error(true).
					errorDescripcio(ExceptionUtils.getStackTrace(ex)).
					build();
			enviament.updateSeuError(
					true,
					event,
					true);
			estatActualitzat = false;
		}
		enviament.updateSeuEstat(
				dataFi,
				estat);
		notificacioEventRepository.save(event);
		enviament.getNotificacio().updateEventAfegir(event);
		return estatActualitzat;
	}

	private static final Logger logger = LoggerFactory.getLogger(SeuHelper.class);

}

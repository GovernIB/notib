///**
// * 
// */
//package es.caib.notib.core.helper;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import es.caib.notib.core.repository.NotificacioEnviamentRepository;
//import es.caib.notib.core.repository.NotificacioEventRepository;
//
///**
// * Mètodes per a interactuar amb la seu electrònica.
// * 
// * @author Limit Tecnologies <limit@limit.es>
// */
//@Component
//public class SeuHelper {
//
//	@Autowired
//	private NotificacioEnviamentRepository notificacioEnviamentRepository;
//	@Autowired
//	private NotificacioEventRepository notificacioEventRepository;
//
//	@Autowired
//	private PluginHelper pluginHelper;
//
//
//
////	@Transactional(propagation=Propagation.REQUIRES_NEW)
////	public void updateSeuNouEnviament(NotificacioEnviamentEntity enviament) {
////		enviament.updateSeuNouEnviament(pluginHelper.getSeuReintentsEnviamentPeriodeProperty());
////	}
////	
////	@Transactional(propagation=Propagation.REQUIRES_NEW)
////	public void updateSeuNovaConsulta(NotificacioEnviamentEntity enviament) {
////		enviament.updateSeuNovaConsulta();
////	}
//	/*
//	@Transactional
//	public void enviament(Long notificacioEnviamentId) {
//		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(notificacioEnviamentId);
//		boolean error = false;
////		updateSeuNouEnviament(enviament);
//		String registreNumero = null;
//		Date registreData = null;
//		SeuEstatEnumDto estat = enviament.getSeuEstat();
//		NotificacioEventEntity event;
//		try {
//			SeuNotificacioResultat resultat = pluginHelper.seuNotificacioDestinatariEnviar(enviament);
//			registreNumero = resultat.getRegistreNumero();
//			registreData = resultat.getRegistreData();
//			estat = SeuEstatEnumDto.ENVIADA;
//			event = NotificacioEventEntity.getBuilder(
//					NotificacioEventTipusEnumDto.SEU_CAIB_ENVIAMENT,
//					enviament.getNotificacio()).
//					enviament(enviament).
//					descripcio("registreNumero=" + registreNumero + ", registreData=" + registreData).
//					build();
//		} catch (Exception ex) {
//			event = NotificacioEventEntity.getBuilder(
//					NotificacioEventTipusEnumDto.SEU_CAIB_ENVIAMENT,
//					enviament.getNotificacio()).
//					enviament(enviament).
//					error(true).
//					errorDescripcio(ExceptionUtils.getStackTrace(ex)).
//					build();
//			enviament.updateSeuError(
//					true,
//					event,
//					false);
//			error = true;
//		}
//		notificacioEventRepository.save(event);
//		enviament.getNotificacio().updateEventAfegir(event);
//		enviament.updateSeuEnviament(
//				registreNumero,
//				registreData,
//				estat);
//		enviament.updateSeuFiOperacio(
//				error, 
//				pluginHelper.getSeuReintentsEnviamentPeriodeProperty());
//	}
//
//	@Transactional
//	public boolean consultaEstat(Long notificacioDestinatariId) {
//		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(notificacioDestinatariId);
////		updateSeuNovaConsulta(enviament);
//		Date dataFi = null;
//		SeuEstatEnumDto estat = enviament.getSeuEstat();
//		NotificacioEventEntity event;
//		boolean estatActualitzat;
//		try {
//			SeuNotificacioEstat notificacioEstat = pluginHelper.seuNotificacioComprovarEstat(
//					enviament);
//			if (notificacioEstat.getEstat() != null) {
//				switch (notificacioEstat.getEstat()) {
//					case LLEGIDA:
//						estat = SeuEstatEnumDto.LLEGIDA;
//						dataFi = notificacioEstat.getData();
//						break;
//					case REBUTJADA:
//						estat = SeuEstatEnumDto.REBUTJADA;
//						dataFi = notificacioEstat.getData();
//						break;
//					case PENDENT:
//					default:
//						estat = SeuEstatEnumDto.ENVIADA;
//						break;
//				}
//				if (notificacioEstat.getFitxerCodi() != null) {
//					enviament.updateSeuFitxer(
//							notificacioEstat.getFitxerCodi(), 
//							notificacioEstat.getFitxerClau());
//					
////					SeuDocument fitxer = pluginHelper.obtenirJustificant(enviament);
////					FileOutputStream outputStream = new FileOutputStream("/home/siona/Feina/Documents/Lot3/Notib/justificant.pdf");
////				    byte[] strToBytes = fitxer.getArxiuContingut();
////				    outputStream.write(strToBytes);
////				    outputStream.close();
//					
//				}
//			} else {
//				estat = enviament.getSeuEstat();
//			}
//			event = NotificacioEventEntity.getBuilder(
//					NotificacioEventTipusEnumDto.SEU_CAIB_CONSULTA_ESTAT,
//					enviament.getNotificacio()).
//					enviament(enviament).
//					descripcio((estat != null) ? estat.toString() : null).
//					build();
//			estatActualitzat = !estat.equals(enviament.getSeuEstat()) && !estat.equals(SeuEstatEnumDto.ENVIADA);
//		} catch (Exception ex) {
//			logger.error(
//					"Error al consultar l'estat de la notificació a la seu electrònica (" +
//					"notificacioId=" + enviament.getNotificacio().getId() + ", " +
//					"notificaIdentificador=" + enviament.getNotificaIdentificador() + ", " +
//					"expedientId=" + enviament.getNotificacio().getSeuExpedientIdentificadorEni() + ", " +
//					"expedientUnitatOrganitzativa=" + enviament.getNotificacio().getSeuExpedientUnitatOrganitzativa() + ", " +
//					"expedientSerieDocumental=" + enviament.getNotificacio().getSeuExpedientSerieDocumental() + ", " +
//					"expedientTitol=" + enviament.getNotificacio().getSeuExpedientTitol() + ", " +
//					"registreNumero=" + enviament.getSeuRegistreNumero() + ")",
//					ex);
//			event = NotificacioEventEntity.getBuilder(
//					NotificacioEventTipusEnumDto.SEU_CAIB_CONSULTA_ESTAT,
//					enviament.getNotificacio()).
//					enviament(enviament).
//					error(true).
//					errorDescripcio(ExceptionUtils.getStackTrace(ex)).
//					build();
//			enviament.updateSeuError(
//					true,
//					event,
//					true);
////			enviament.updateSeuConsultaError(pluginHelper.getSeuReintentsConsultaPeriodeProperty());
//			if (ex.getMessage().contains("No existeix la notificació")) {
//				estat = SeuEstatEnumDto.INEXISTENT;
//			}
//			estatActualitzat = false;
//		}
//		enviament.updateSeuEstat(
//				dataFi,
//				estat);
//		notificacioEventRepository.save(event);
//		enviament.getNotificacio().updateEventAfegir(event);
//		enviament.updateSeuFiOperacio();
//		return estatActualitzat;
//	}
//	
//	@Transactional
//	public SeuDocument obtenirJustificant(Long notificacioDestinatariId) {
//		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(notificacioDestinatariId);
//		return pluginHelper.obtenirJustificant(enviament);
//		
//	}
//*/
//	private static final Logger logger = LoggerFactory.getLogger(SeuHelper.class);
//
//}

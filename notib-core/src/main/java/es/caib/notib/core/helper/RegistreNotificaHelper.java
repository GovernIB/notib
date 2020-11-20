/**
 * 
 */
package es.caib.notib.core.helper;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.AsientoRegistralBeanDto;
import es.caib.notib.core.api.dto.InteressatTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.exception.RegistreNotificaException;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;

/**
 * Helper per a interactuar amb la versió 2 del servei web de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class RegistreNotificaHelper {
	
	@Autowired
	PluginHelper pluginHelper;
	@Autowired
	ConversioTipusHelper conversioTipusHelper;
	@Autowired
	NotificaHelper notificaHelper;
	@Autowired
	RegistreHelper registreHelper;
	@Autowired
	private AuditNotificacioHelper auditNotificacioHelper;
	@Autowired
	private AuditEnviamentHelper auditEnviamentHelper;
	@Autowired
	NotificacioEventRepository notificacioEventRepository;
	
	public boolean realitzarProcesRegistrar(
			NotificacioEntity notificacioEntity,
			List<NotificacioEnviamentDtoV2> enviaments) throws RegistreNotificaException {
		logger.info(" [REG-NOT] Inici procés Registrar-Notificar [Id: " + notificacioEntity.getId() + ", Estat: " + notificacioEntity.getEstat() + "]");
		boolean enviarANotifica = false;
		String dir3Codi;
		if (notificacioEntity.getEntitat().getDir3CodiReg() != null) {
			dir3Codi = notificacioEntity.getEntitat().getDir3CodiReg();
		} else {
			dir3Codi = notificacioEntity.getEntitat().getDir3Codi();
		}
		
		if (isArxiuEmprarSir()) {
			if(NotificaEnviamentTipusEnumDto.COMUNICACIO.equals(notificacioEntity.getEnviamentTipus())) {
				//Regweb3 + SIR
				boolean totsAdministracio = true;
				for(NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
					if(!enviament.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO)) {
						totsAdministracio = false;
					}
				}
				if(totsAdministracio) {
					logger.info(" [REG-NOT] Assentament registral (SIR)");
					for(NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
						//Només crea assentament registral
						try {
							logger.info(" >>> Nou assentament registral...");
							notificacioEntity.updateRegistreNouEnviament(pluginHelper.getRegistreReintentsPeriodeProperty());
							AsientoRegistralBeanDto arb = pluginHelper.notificacioToAsientoRegistralBean(
									notificacioEntity, 
									enviament);
							RespostaConsultaRegistre arbResposta = pluginHelper.crearAsientoRegistral(
									dir3Codi, 
									arb, 
									2L,
									notificacioEntity.getId(),
									String.valueOf(enviament.getId()));
							//Registrar event
							if(arbResposta.getErrorDescripcio() != null) {
								logger.info(" >>> ... ERROR");
								updateEventWithError(
										arbResposta,
										null,
										notificacioEntity,
										enviament,
										notificacioEntity.getEnviaments());
							} else {
								logger.info(" >>> ... OK");
								updateEventWithoutError(
										arbResposta,
										notificacioEntity,
										enviament,
										null,
//										false,
										totsAdministracio);
							}
						} catch (Exception ex) {
							logger.error(ex.getMessage(), ex);
//							updateEventWithError(
//									ex, 
//									notificacioEntity, 
//									enviament, 
//									null);
							throw new RegistreNotificaException(
									ex.getMessage(),
									ex);
						}
					}
				} else {
					logger.info(" [REG-NOT] Comunicació: Assentament registral + Notifica");
					try {
						//Crea assentament registral + Notific@
						logger.info(" >>> Nou assentament registral...");
						notificacioEntity.updateRegistreNouEnviament(pluginHelper.getRegistreReintentsPeriodeProperty());
						AsientoRegistralBeanDto arb = pluginHelper.notificacioEnviamentsToAsientoRegistralBean(
								notificacioEntity, 
								notificacioEntity.getEnviaments());
						RespostaConsultaRegistre arbResposta = pluginHelper.crearAsientoRegistral(
								dir3Codi, 
								arb, 
								1L,
								notificacioEntity.getId(),
								getEnviamentIds(notificacioEntity));
						//Registrar event
						if(arbResposta.getErrorCodi() != null) {
							logger.info(" >>> ... ERROR");
							updateEventWithError(
									arbResposta,
									null,
									notificacioEntity,
									null,
									notificacioEntity.getEnviaments());
						} else {
							logger.info(" >>> ... OK");
							updateEventWithoutError(
									arbResposta,
									notificacioEntity,
									null,
									notificacioEntity.getEnviaments(),
//									true,
									false);
							enviarANotifica = true;
						}
					} catch (Exception ex) {
						logger.error(ex.getMessage(), ex);
//						updateEventWithError(
//								ex, 
//								notificacioEntity, 
//								null, 
//								notificacioEntity.getEnviaments());
						throw new RegistreNotificaException(
								ex.getMessage(),
								ex);
					}
				}
			} else {
				//Crea assentament registral + Notific@
				logger.info(" [REG-NOT] Notificació: Assentament registral + Notifica");
				try {
					logger.info(" >>> Nou assentament registral...");
					notificacioEntity.updateRegistreNouEnviament(pluginHelper.getRegistreReintentsPeriodeProperty());
					AsientoRegistralBeanDto arb = pluginHelper.notificacioEnviamentsToAsientoRegistralBean(
							notificacioEntity, 
							notificacioEntity.getEnviaments());
					RespostaConsultaRegistre arbResposta = pluginHelper.crearAsientoRegistral(
							dir3Codi, 
							arb, 
							1L,
							notificacioEntity.getId(),
							getEnviamentIds(notificacioEntity));
					//Registrar event
					if(arbResposta.getErrorCodi() != null) {
						logger.info(" >>> ... ERROR");
						updateEventWithError(
								arbResposta,
								null,
								notificacioEntity,
								null,
								notificacioEntity.getEnviaments());
					} else {
						logger.info(" >>> ... OK");
						updateEventWithoutError(
								arbResposta,
								notificacioEntity,
								null,
								notificacioEntity.getEnviaments(),
//								true,
								false);
						enviarANotifica = true;
					}
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
//					updateEventWithError(
//							ex, 
//							notificacioEntity, 
//							null, 
//							notificacioEntity.getEnviaments());
					throw new RegistreNotificaException(
							ex.getMessage(),
							ex);
				}
			}
		} else {
			//Crea un assentament sortida normal i envia a Notific@
			logger.info(" [REG-NOT] Assentament sortida (registre) + Notifica");
//			RegistreIdDto registreIdDto = new RegistreIdDto();
			try {
				logger.info(" >>> Nou registre...");
				notificacioEntity.updateRegistreNouEnviament(pluginHelper.getRegistreReintentsPeriodeProperty());
//				registreIdDto = pluginHelper.registreAnotacioSortida(
//						conversioTipusHelper.convertir(
//								notificacioEntity, 
//								NotificacioDtoV2.class), 
//						enviaments, 
//						1L);
				AsientoRegistralBeanDto arb = pluginHelper.notificacioEnviamentsToAsientoRegistralBean(
						notificacioEntity, 
						notificacioEntity.getEnviaments());
				RespostaConsultaRegistre arbResposta = pluginHelper.crearAsientoRegistral(
						dir3Codi, 
						arb, 
						null,
						notificacioEntity.getId(),
						getEnviamentIds(notificacioEntity));
				//Registrar event
				if (arbResposta.getErrorDescripcio() != null) {
					logger.info(" >>> ... ERROR");
					updateEventWithError(
							arbResposta,
							null,
							notificacioEntity,
							null,
							notificacioEntity.getEnviaments());
				} else {
					logger.info(" >>> ... OK");
					updateEventWithoutError(
							arbResposta,
							notificacioEntity,
							null,
							notificacioEntity.getEnviaments(),
//							true,
							false);
					enviarANotifica = true;
				}
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
//				updateEventWithError(
//						ex, 
//						notificacioEntity, 
//						null, 
//						notificacioEntity.getEnviaments());
				throw new RegistreNotificaException(
						ex.getMessage(),
						ex);
			}
			logger.info(" [REG-NOT] Fi procés Registrar-Notificar [Id: " + notificacioEntity.getId() + ", Estat: " + notificacioEntity.getEstat() + "]");
		}
		return enviarANotifica;
	}
	
	private String getEnviamentIds(NotificacioEntity notificacio) {
		String enviamentIds = "";
		for(NotificacioEnviamentEntity enviament : notificacio.getEnviaments()) {
			enviamentIds += enviament.getId() + ", ";
		}
		if (!enviamentIds.isEmpty())
			enviamentIds = enviamentIds.substring(0, enviamentIds.length() - 2);
		return enviamentIds;
	}
	
	public NotificacioEntity updateEventWithError(
			RespostaConsultaRegistre arbResposta,
			String errorDescripcio,
			NotificacioEntity notificacioEntity,
			NotificacioEnviamentEntity enviament,
			Set<NotificacioEnviamentEntity> enviaments) {
		
		if (arbResposta != null)
			errorDescripcio = arbResposta.getErrorDescripcio();
			
		//Crea un nou event
		NotificacioEventEntity.Builder eventBulider = NotificacioEventEntity.getBuilder(
				NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE,
				notificacioEntity).
				error(true).
				errorDescripcio(errorDescripcio);
		
		if (notificacioEntity.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB) {
			eventBulider.callbackInicialitza();
		}
		NotificacioEventEntity event = eventBulider.build();
		//Actualitza l'event per cada enviament
		if (enviament != null) {
			eventBulider.enviament(enviament);

			auditNotificacioHelper.updateNotificacioErrorRegistre(notificacioEntity, event);
			notificacioEventRepository.saveAndFlush(event);
		} else {
			for (NotificacioEnviamentEntity enviamentEntity : enviaments) {
				enviamentEntity.updateNotificaError(true, event);
				eventBulider.enviament(enviamentEntity);
				
				auditNotificacioHelper.updateNotificacioErrorRegistre(notificacioEntity, event);
				notificacioEventRepository.saveAndFlush(event);
			}
		} 
		return notificacioEntity;
	}

	public NotificacioEntity updateEventWithoutError(
			RespostaConsultaRegistre arbResposta,
			NotificacioEntity notificacioEntity,
			NotificacioEnviamentEntity enviament,
			Set<NotificacioEnviamentEntity> enviaments,
//			boolean enviarNotificacio,
			boolean totsAdministracio) {
		//Crea un nou event
		NotificacioEventEntity.Builder eventBulider = NotificacioEventEntity.getBuilder(
				NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE,
				notificacioEntity);
		
		if (notificacioEntity.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB)
			eventBulider.callbackInicialitza();
		
		NotificacioEventEntity event = eventBulider.build();
		
		if (arbResposta != null) {
			auditNotificacioHelper.updateNotificacioRegistre(arbResposta, notificacioEntity);
			logger.info(" >>> Canvi estat a REGISTRADA ");
//			if (enviarNotificacio) {
//				logger.info(" >>> Notificant...");
//				notificaHelper.notificacioEnviar(notificacioEntity.getId());
//			}
			//Comunicació + administració (SIR)
			if (totsAdministracio) {
				logger.debug("Comunicació SIR --> actualitzar estat...");
				auditNotificacioHelper.updateNotificacioEnviada(notificacioEntity);
				registreHelper.enviamentUpdateDatat(
						arbResposta.getEstat(), 
						arbResposta.getRegistreData(), 
						arbResposta.getSirRecepecioData(),
						arbResposta.getSirRegistreDestiData(),
						arbResposta.getRegistreNumeroFormatat(), 
						enviament);
			}
			if (enviament != null) {
				auditEnviamentHelper.actualitzaRegistreEnviament(
						arbResposta,
						notificacioEntity,
						enviament,
						totsAdministracio,
						eventBulider,
						event);
			} else {
				for(NotificacioEnviamentEntity enviamentEntity: enviaments) {
					auditEnviamentHelper.actualitzaRegistreEnviament(
							arbResposta,
							notificacioEntity,
							enviamentEntity,
							totsAdministracio,
							eventBulider,
							event);
				}
			}
		}
		return notificacioEntity;
	}

	private boolean isArxiuEmprarSir() {
		String sir = getPropertyEmprarSir();
		return Boolean.valueOf(sir);
	}
	private String getPropertyEmprarSir() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.emprar.sir");
	}
	
	private static final Logger logger = LoggerFactory.getLogger(RegistreNotificaHelper.class);

}

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
import es.caib.notib.core.api.dto.NotificacioDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.NotificacioErrorTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.RegistreIdDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
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
	NotificacioEventRepository notificacioEventRepository;
	
	public void realitzarProcesRegistrarNotificar(
			NotificacioEntity notificacioEntity,
			List<NotificacioEnviamentDtoV2> enviaments) {
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
					for(NotificacioEnviamentEntity enviament : notificacioEntity.getEnviaments()) {
						//Només crea assentament registral
						try {
							AsientoRegistralBeanDto arb = pluginHelper.notificacioToAsientoRegistralBean(
									notificacioEntity, 
									enviament);
							RespostaConsultaRegistre arbResposta = pluginHelper.crearAsientoRegistral(notificacioEntity.getEntitat().getDir3Codi(), arb, 2L);
							//Registrar event
							if(arbResposta.getErrorDescripcio() != null) {
								updateEventWithError(
										arbResposta,
										null,
										notificacioEntity,
										enviament,
										notificacioEntity.getEnviaments());
							} else {
								updateEventWithoutError(
										arbResposta,
										null,
										notificacioEntity,
										enviament,
										null,
										false);
							}
						} catch (Exception ex) {
							logger.error(
									ex.getMessage(),
									ex);
						}
					}
				} else {
					try {
						//Crea assentament registral + Notific@
						AsientoRegistralBeanDto arb = pluginHelper.notificacioEnviamentsToAsientoRegistralBean(
								notificacioEntity, 
								notificacioEntity.getEnviaments());
						RespostaConsultaRegistre arbResposta = pluginHelper.crearAsientoRegistral(notificacioEntity.getEntitat().getDir3Codi(), arb, 1L);
						//Registrar event
						if(arbResposta.getErrorCodi() != null) {
							updateEventWithError(
									arbResposta,
									null,
									notificacioEntity,
									null,
									notificacioEntity.getEnviaments());
						} else {
							updateEventWithoutError(
									arbResposta,
									null,
									notificacioEntity,
									null,
									notificacioEntity.getEnviaments(),
									true);
						}
					} catch (Exception ex) {
						logger.error(
								ex.getMessage(),
								ex);
					}
				}
			} else {
				//Crea assentament registral + Notific@
				try {
					AsientoRegistralBeanDto arb = pluginHelper.notificacioEnviamentsToAsientoRegistralBean(
							notificacioEntity, 
							notificacioEntity.getEnviaments());
					RespostaConsultaRegistre arbResposta = pluginHelper.crearAsientoRegistral(notificacioEntity.getEntitat().getDir3Codi(), arb, 1L);
					//Registrar event
					if(arbResposta.getErrorCodi() != null) {
						updateEventWithError(
								arbResposta,
								null,
								notificacioEntity,
								null,
								notificacioEntity.getEnviaments());
					} else {
						updateEventWithoutError(
								arbResposta,
								null,
								notificacioEntity,
								null,
								notificacioEntity.getEnviaments(),
								true);
					}
				} catch (Exception ex) {
					logger.error(
							ex.getMessage(),
							ex);
				}
			}
		} else {
			//Crea registre sortida + Notific@
			RegistreIdDto registreIdDto = new RegistreIdDto();
			try {
				registreIdDto = pluginHelper.registreAnotacioSortida(
						conversioTipusHelper.convertir(
								notificacioEntity, 
								NotificacioDtoV2.class), 
						enviaments, 
						1L);
				//Registrar event
				if (registreIdDto.getDescripcioError() != null) {
					updateEventWithError(
							null,
							registreIdDto,
							notificacioEntity,
							null,
							notificacioEntity.getEnviaments());				
				} else {
					updateEventWithoutError(
							null,
							registreIdDto,
							notificacioEntity,
							null,
							notificacioEntity.getEnviaments(),
							true);
				}
			} catch (Exception ex) {
				logger.error(
						ex.getMessage(),
						ex);
			}
		}
	}
	
	private void updateEventWithError(
			RespostaConsultaRegistre arbResposta,
			RegistreIdDto registreIdDto,
			NotificacioEntity notificacioEntity,
			NotificacioEnviamentEntity enviament,
			Set<NotificacioEnviamentEntity> enviaments) {
		String errorDescripcio;
		
		if (arbResposta != null) {
			errorDescripcio = arbResposta.getErrorDescripcio();
		} else {
			errorDescripcio = registreIdDto.getDescripcioError();
		}
		
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
		} else {
			for (NotificacioEnviamentEntity enviamentEntity : enviaments) {
				enviamentEntity.updateNotificaError(true, event);
				eventBulider.enviament(enviamentEntity);
			}
		} 
		
		notificacioEntity.updateNotificaError(
				NotificacioErrorTipusEnumDto.ERROR_REGISTRE,
				event);
		notificacioEntity.updateEventAfegir(event);
		notificacioEventRepository.saveAndFlush(event);
		
	}
	
	private void updateEventWithoutError(
			RespostaConsultaRegistre arbResposta,
			RegistreIdDto registreIdDto,
			NotificacioEntity notificacioEntity,
			NotificacioEnviamentEntity enviament,
			Set<NotificacioEnviamentEntity> enviaments,
			boolean enviarNotificacio) {
		//Crea un nou event
		NotificacioEventEntity.Builder eventBulider = NotificacioEventEntity.getBuilder(
				NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE,
				notificacioEntity);
		
		if (notificacioEntity.getTipusUsuari() != TipusUsuariEnumDto.INTERFICIE_WEB)
			eventBulider.callbackInicialitza();
		
		NotificacioEventEntity event = eventBulider.build();
		
		if (enviament != null) {
			eventBulider.enviament(enviament);
		} else {
			for (NotificacioEnviamentEntity enviamentEntity : enviaments) {
				eventBulider.enviament(enviamentEntity);
			}
		}
		
		if (arbResposta != null) {
			notificacioEntity.updateRegistreNumero(Integer.parseInt(arbResposta.getRegistreNumero()));
			notificacioEntity.updateRegistreNumeroFormatat(arbResposta.getRegistreNumeroFormatat());
			notificacioEntity.updateRegistreData(arbResposta.getRegistreData());
			notificacioEntity.updateEstat(NotificacioEstatEnumDto.REGISTRADA);
			notificacioEntity.updateEventAfegir(event);
			notificacioEventRepository.saveAndFlush(event);
			
			if (enviarNotificacio) {
				notificaHelper.notificacioEnviar(notificacioEntity.getId());
			}
			if (enviament != null) {
				enviament.setRegistreNumeroFormatat(arbResposta.getRegistreNumeroFormatat());
				enviament.setRegistreData(arbResposta.getRegistreData());
				enviament.setRegistreEstat(arbResposta.getEstat());
			} else {
				for(NotificacioEnviamentEntity enviamentEntity: enviaments) {
					enviamentEntity.setRegistreNumeroFormatat(arbResposta.getRegistreNumeroFormatat());
					enviamentEntity.setRegistreData(arbResposta.getRegistreData());
					enviamentEntity.setRegistreEstat(arbResposta.getEstat());
				}
			}
		} else {
			notificacioEntity.updateRegistreNumero(registreIdDto.getNumero());
			notificacioEntity.updateRegistreNumeroFormatat(registreIdDto.getNumeroRegistreFormat());
			notificacioEntity.updateRegistreData(registreIdDto.getData());
			notificacioEntity.updateEstat(NotificacioEstatEnumDto.REGISTRADA);
			notificacioEntity.updateEventAfegir(event);
			notificacioEventRepository.saveAndFlush(event);
			notificaHelper.notificacioEnviar(notificacioEntity.getId());
			for(NotificacioEnviamentEntity enviamentEntity: notificacioEntity.getEnviaments()) {
				enviamentEntity.setRegistreNumeroFormatat(registreIdDto.getNumeroRegistreFormat());
				enviamentEntity.setRegistreData(registreIdDto.getData());
			}
		}
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

package es.caib.notib.logic.service;

import es.caib.notib.logic.helper.CallbackHelper;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.helper.PaginacioHelper;
import es.caib.notib.logic.helper.PropertiesConstants;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.callback.CallbackDto;
import es.caib.notib.logic.intf.dto.callback.CallbackFiltre;
import es.caib.notib.logic.intf.dto.callback.CallbackResposta;
import es.caib.notib.logic.intf.service.CallbackService;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.logic.threads.CallbackProcessarPendentsThread;
import es.caib.notib.logic.utils.NotibLogger;
import es.caib.notib.persist.entity.CallbackEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.repository.AplicacioRepository;
import es.caib.notib.persist.repository.CallbackRepository;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ScheduledMessage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jms.core.JmsMessageOperations;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Classe que implementa el servei de callback cap a les aplicacions clients de Notib
 * que estan configurades per rebre actualitzacions dels events de les notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class CallbackServiceImpl implements CallbackService {

 	@Autowired
	private CallbackRepository callbackRepository;
	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
    @Autowired
	private CallbackHelper callbackHelper;
    @Autowired
    private MetricsHelper metricsHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
    @Autowired
    private ConversioTipusHelper conversioTipusHelper;
    @Autowired
	private JmsTemplate jmsTemplate;
    @Autowired
    private AplicacioRepository aplicacioRepository;
    @Autowired
    private MessageHelper messageHelper;


    @Override
	@Transactional(readOnly=true, propagation = Propagation.REQUIRES_NEW)
	public void processarPendentsJms() {

		var noEnviats = callbackRepository.findEnviamentIdPendentsNoEnviats();
		for (var noEnviat : noEnviats) {
			jmsTemplate.convertAndSend(SmConstants.CUA_CALLBACKS, noEnviat,
					m -> {
						m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 1000L);
						return m;
					});
		}
	}

	@Override
	public void processarPendents() {

		var timer = metricsHelper.iniciMetrica();
		try {

			if (!isTasquesActivesProperty() || !isCallbackPendentsActiu()) {
				log.info("[Callback] Enviament periodic de callbacks deshabilitat. ");
				return;
			}
			log.info("[Callback] Cercant notificacions pendents d'enviar al client");
			var maxPendents = getEventsProcessarMaxProperty();
			var page = PageRequest.of(0, maxPendents);
			var pendents = callbackRepository.findEnviamentIdPendents(page);
			if (pendents.isEmpty()) {
				log.info("[Callback] No hi ha notificacions pendents d'enviar. ");
				return;
			}
			log.info("[Callback] Inici de les notificacions pendents cap a les aplicacions.");
			var errors = 0;
			var executorService = Executors.newFixedThreadPool(pendents.size());
			Map<Long, Future<Boolean>> futurs = new HashMap<>();
			CallbackProcessarPendentsThread thread;
			Future<Boolean> futur;
			var multiThread = Boolean.parseBoolean(configHelper.getConfig(PropertiesConstants.SCHEDULLED_MULTITHREAD));
			for (var id: pendents) {
				log.info("[Callback] >>> Enviant avís a aplicació client de canvi d'estat de l'event amb identificador: " + id);
				try {
					if (multiThread) {
						thread = new CallbackProcessarPendentsThread(id, callbackHelper);
						futur = executorService.submit(thread);
						futurs.put(id, futur);
						continue;
					}
					if(!callbackHelper.notifica(id, null)) {
						errors++;
					}
				} catch (Exception ex) {
					errors++;
					log.error("[Callback] L'enviament [Id: " + id + "] ha provocat la següent excepcio:", ex);
					callbackHelper.marcarEventNoProcessable(id, ex.getMessage(), ExceptionUtils.getStackTrace(ex));
				}
			}
			var keys = futurs.keySet();
			Boolean err;
			for (var key : keys) {
				try {
					err = futurs.get(key).get();
					errors = Boolean.TRUE.equals(err) ? errors + 1 : errors;
				} catch (Exception ex) {
					errors++;
					log.error("[Callback] L'enviament [Id: " + key + "] ha provocat la següent excepcio:", ex);
					callbackHelper.marcarEventNoProcessable(key, ex.getMessage(), ExceptionUtils.getStackTrace(ex));
				}
			}
			log.info("[Callback] Fi de les notificacions pendents cap a les aplicacions: " + pendents.size() + ", " + errors + " errors");
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public boolean reintentarCallback(Long notificacioId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.info("Notificant canvi al client...");
			// Recupera l'event
			var isError = false;
			var not = notificacioRepository.findById(notificacioId).orElseThrow();
			NotificacioEntity notificacio;
			for (var env : not.getEnviaments()) {
				try {
					notificacio = callbackHelper.notifica(env, null);
					isError = (notificacio != null && !notificacio.isErrorLastCallback());
				} catch (Exception e) {
					log.error(String.format("[Callback]L'enviament [Id: %d] ha provocat la següent excepcio:", env.getId()), e);
					isError = true;
				}
			}
			return isError;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public boolean findByNotificacio(Long notId) {
		return false;
	}

	@Override
	public PaginaDto<CallbackDto> findPendentsByEntitat(CallbackFiltre filtre, PaginacioParamsDto paginacioParams) {

		var pageable = getMappeigPropietats(paginacioParams);
		var map = callbackRepository.findIdAndAdjustedDate();
		filtre.setMaxReintents(configHelper.getConfigAsInteger("es.caib.notib.tasca.callback.pendents.notifica.events.intents.max"));
		var pendents = callbackRepository.findPendentsByEntitat(filtre, pageable);
		var dtos = paginacioHelper.toPaginaDto(pendents, CallbackDto.class);
		for (var pendent : dtos.getContingut()) {
			var properIntent = map.get(pendent.getId());
			pendent.setProperIntent(properIntent);
		}
		return dtos;
	}

    @Override
    public List<Long> findPendentsIdByEntitat(CallbackFiltre filtre) {

        try {
            filtre.setMaxReintents(configHelper.getConfigAsInteger("es.caib.notib.tasca.callback.pendents.notifica.events.intents.max"));
            return callbackRepository.findPendentsIdByEntitat(filtre);
        } catch (Exception ex){
            log.error("Error obtinguent els ids amb filtre de les callbacks", ex);
            return new ArrayList<>();
        }
    }

    @Override
	public CallbackResposta enviarCallback(Long callbackId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.info("Notificant canvi al client...");
			// Recupera l'event
			var callback = callbackRepository.findById(callbackId).orElseThrow();
			var enviament = notificacioEnviamentRepository.findById(callback.getEnviamentId()).orElseThrow();
            var aplicacio = aplicacioRepository.findByUsuariCodiAndEntitatId(callback.getUsuariCodi(), enviament.getNotificacio().getEntitat().getId());
            if (!aplicacio.isActiva()) {
                NotibLogger.getInstance().info("[CallbackServiceImpl] El callback no s'envia ja que l'aplicacio  amb id " + aplicacio.getId() + " no esta activa ", log, LoggingTipus.CALLBACK);
                return CallbackResposta.builder().ok(false).errorMsg(messageHelper.getMessage("callback.aplicacio.no.activa")).build();
            }
			try {
				var notificacio = callbackHelper.notifica(enviament, null);
				var isError = (notificacio != null && !notificacio.isErrorLastCallback());
				return CallbackResposta.builder().ok(isError).errorMsg(callback.getErrorDesc()).build();
			} catch (Exception e) {
				log.error(String.format("[Callback]L'enviament [Id: %d] ha provocat la següent excepcio:", enviament.getId()), e);
				return CallbackResposta.builder().ok(false).errorMsg(e.getMessage()).build();
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

//	@Override
//	public CallbackResposta enviarCallback(Set<Long> callbacks) {
//
//		var timer = metricsHelper.iniciMetrica();
//		try {
//			log.info("Enviant callbacks massius");
//			for (var callbackId : callbacks) {
//				new Thread(() -> {
//				var callback = callbackRepository.findById(callbackId).orElseThrow();
//                var enviament = notificacioEnviamentRepository.findById(callback.getEnviamentId()).orElseThrow();
//                var aplicacio = aplicacioRepository.findByUsuariCodiAndEntitatId(callback.getUsuariCodi(), enviament.getNotificacio().getEntitat().getId());
//                if (!aplicacio.isActiva()) {
//                    NotibLogger.getInstance().info("[CallbackServiceImpl] El callback no s'envia ja que l'aplicacio  amb id " + aplicacio.getId() + " no esta activa ", log, LoggingTipus.CALLBACK);
//                    return;
//                }
//				try {
//						callbackHelper.notifica(callback.getEnviamentId(), null);
//					} catch (Exception e) {
//						log.error(String.format("[Callback]L'enviament [Id: %d] ha provocat la següent excepcio:", callback.getEnviamentId()), e);
//					}
//				}).start();
//			}
//			return CallbackResposta.builder().ok(true).build();
//		} catch (Exception e) {
//			log.error("Error executant els callbacks massius " + e);
//			return CallbackResposta.builder().ok(false).errorMsg(e.getMessage()).build();
//		} finally {
//			metricsHelper.fiMetrica(timer);
//		}
//	}


    @Override
    public CallbackResposta enviarCallback(Set<Long> callbacks) {

        var timer = metricsHelper.iniciMetrica();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        try {
            log.info("Enviant callbacks massius");
            List<Future<Void>> futures = new ArrayList<>();

            for (var callbackId : callbacks) {
                futures.add(executorService.submit(() -> {
                    var callback = callbackRepository.findById(callbackId).orElseThrow();
                    var enviament = notificacioEnviamentRepository.findById(callback.getEnviamentId()).orElseThrow();
                    var aplicacio = aplicacioRepository.findByUsuariCodiAndEntitatId(callback.getUsuariCodi(), enviament.getNotificacio().getEntitat().getId());

                    if (!aplicacio.isActiva()) {
                        NotibLogger.getInstance().info("[CallbackServiceImpl] El callback no s'envia ja que l'aplicacio amb id " + aplicacio.getId() + " no esta activa ", log, LoggingTipus.CALLBACK);
                        return null; // Return null for inactive applications
                    }

                    try {
                        callbackHelper.notifica(callback.getEnviamentId(), null);
                    } catch (Exception e) {
                        log.error(String.format("[Callback]L'enviament [Id: %d] ha provocat la següent excepcio:", callback.getEnviamentId()), e);
                    }
                    return null;
                }));
            }
            for (Future<Void> future : futures) {
                try {
                    future.get();
                } catch (ExecutionException e) {
                    log.error("Error executant callback task", e.getCause());
                }
            }
            return CallbackResposta.builder().ok(true).build();
        } catch (Exception e) {
            log.error("Error executant els callbacks massius " + e);
            return CallbackResposta.builder().ok(false).errorMsg(e.getMessage()).build();
        } finally {
            executorService.shutdown(); // Properly shutdown the executor service
            metricsHelper.fiMetrica(timer);
        }
    }

	@Override
	@Transactional
	public boolean pausarCallback(Long callbackId, boolean pausat) {

		try {
			var callback = callbackRepository.findById(callbackId).orElseThrow();
			callback.setPausat(pausat);
			return true;
		} catch (Exception e) {
			log.error("Error pausant el callback amb id " + callbackId, e);
			return false;
		}
	}

	@Override
	@Transactional
	public CallbackResposta pausarCallback(Set<Long> callbacks, boolean pausat) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.info("Pausant callbacks massius pausat = " + pausat);
			CallbackEntity callback;
			NotificacioEnviamentEntity enviament;
			for (var callbackId : callbacks) {
				callback = callbackRepository.findById(callbackId).orElseThrow();
				callback.setPausat(pausat);
			}
			return CallbackResposta.builder().ok(true).build();
		} catch (Exception e) {
			return CallbackResposta.builder().ok(false).errorMsg(e.getMessage()).build();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

    @Override
    public boolean esborrarCallback(Long callbackId) {

        var timer = metricsHelper.iniciMetrica();
        try {
            log.info("Esborrant callback " + callbackId);
            callbackRepository.deleteById(callbackId);
            return true;
        } catch (Exception e) {
            log.error("Error pausant el callback amb id " + callbackId, e);log.error("Error pausant el callback amb id " + callbackId, e);
            return false;
        } finally {
            metricsHelper.fiMetrica(timer);
        }
    }

    @Override
    public CallbackResposta esborrarCallback(Set<Long> callbacks) {

        var timer = metricsHelper.iniciMetrica();
        try {
            log.info("Esborrant callbacks massius = " + callbacks);
            callbackRepository.deleteAllById(callbacks);
            return CallbackResposta.builder().ok(true).build();
        } catch (Exception e) {
            return CallbackResposta.builder().ok(false).errorMsg(e.getMessage()).build();
        } finally {
            metricsHelper.fiMetrica(timer);
        }
    }

    private Pageable getMappeigPropietats(PaginacioParamsDto paginacioParams) {

		Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<>();
		mapeigPropietatsOrdenacio.put("usuariCodi", new String[] {"usuariCodi"});
		mapeigPropietatsOrdenacio.put("endpoint", new String[] {"usuariCodi"});
		mapeigPropietatsOrdenacio.put("data", new String[] {"data"});

		return paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);
	}

	private boolean isTasquesActivesProperty() {
		return configHelper.getConfigAsBoolean("es.caib.notib.tasques.actives");
	}

	private boolean isCallbackPendentsActiu() {
		return configHelper.getConfigAsBoolean("es.caib.notib.tasca.callback.pendents.actiu");
	}

	private int getEventsProcessarMaxProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.callback.pendents.processar.max");
	}

}

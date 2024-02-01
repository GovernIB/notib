package es.caib.notib.logic.service;

import com.google.common.base.Strings;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.dto.stateMachine.StateMachineInfo;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioMassivaRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.stateMachine.StateMachineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Implementació del servei de gestió de enviaments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class EnviamentSmServiceImpl implements EnviamentSmService {

	@Resource
	private MetricsHelper metricsHelper;
	@Autowired
	private ConfigHelper configHelper;
	@PersistenceContext
	private EntityManager entityManager;

	private final NotificacioEnviamentRepository enviamentRepository;
	private final NotificacioRepository notificacioRepository;
	private final NotificacioMassivaRepository notificacioMassivaRepository;
	private final StateMachineRepository smRepository;
	private final StateMachineService<EnviamentSmEstat, EnviamentSmEvent> stateMachineService;

	@Override
	public EnviamentSmEstat getEstat(String enviamentUuid) {
		return stateMachineService.acquireStateMachine(enviamentUuid, true).getState().getId();
	}

	@Override
	@Transactional(readOnly = true)
	public boolean mostrarAfegirStateMachine(Long notificacioId) {

		var not = notificacioRepository.findById(notificacioId).orElseThrow();
		for (var env : not.getEnviaments()) {
			var estat = smRepository.findEstatByMachineId(env.getUuid());
			if (estat == null) {
				return true;
			}
		}
		return false;
	}

	@Override
	@Transactional(readOnly = true)
	public StateMachineInfo infoStateMachine(Long enviamentId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de la State Machine per l'enviament amb id" + enviamentId);
			var env = enviamentRepository.findById(enviamentId).orElseThrow();
			var info = new StateMachineInfo();
			var estat = stateMachineService.acquireStateMachine(env.getUuid()).getState().getId();
			if (estat == null) {
				return info;
			}
			info.setEstat(estat);
			return info;
		} catch (Exception ex) {
			log.error("Error canviant l'estat de la màquina per l'enviament " + enviamentId, ex);
			return new StateMachineInfo();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly=true, propagation = Propagation.REQUIRES_NEW)
	public void afegirNotificacions() {

		log.info("Afegint notificacions no existents a la màquina amb estat PENDENT, ENVIADA, REGISTRADA O ENVIADA_AMB_ERROR");
		var notificacions = notificacioRepository.findNotificacionsEnProgres("20/08/2023");
		var size = notificacions.size();
		for (var foo=0;foo<size;foo++) {
			try {
				afegirNotificacio(notificacions.get(foo));
			} catch (Exception ex) {
				log.error("Error afegint a la maquina d'estast la notifiacio " + foo);
			}
			if (foo % 50 == 0) {
				log.info("Processades 50 notificacions");
				// provar EntityManager.flush()
				entityManager.flush();
			}
		}
	}

	private boolean isPendentRegistre(NotificacioEnviamentEntity env) {
		return NotificacioEstatEnumDto.PENDENT.equals(env.getNotificacio().getEstat()) && env.getNotificacio().getRegistreEnviamentIntent() == 0 && !env.isNotificaError();
	}

	@Override
	@Transactional
	public boolean afegirNotificacio(Long notificacioId) {

		try {
			log.debug("Afegint a la màquina la notificacio amb id " + notificacioId);
			var not = notificacioRepository.findById(notificacioId).orElseThrow();
			var estat = not.getEstat();
			not.getEnviaments().forEach(e -> {
				// S'HA DE FICAR ELS INTENTS ACTUALS A LA MÀQUINA.
				if (!NotificacioEstatEnumDto.PENDENT.equals(estat) && !NotificacioEstatEnumDto.REGISTRADA.equals(estat) &&
						!NotificacioEstatEnumDto.ENVIADA.equals(estat) && !NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(estat)) {
					return;
				}
				EnviamentSmEvent eventSm = null;
				var intent = 0;
				if (e.getRegistreData() == null) { // Pendent
					// max reintents -> estat registre_error - posar intents màquina al màxim que és la propietat definida
					// algun reintent o nou -> registre_pendent - cal també afegir el numero d'intents a la màquina rg_enviar
					eventSm = not.getRegistreEnviamentIntent() < configHelper.getMaxReintentsRegistre() ? EnviamentSmEvent.RG_ENVIAR : EnviamentSmEvent.RG_ERROR;
					intent = not.getRegistreEnviamentIntent();
				} else if (!not.isComunicacioSir() && (e.isNotificaError() || Strings.isNullOrEmpty(e.getNotificaIdentificador()))) { // Registrada
					// si es notifica mateix cas que registre. Mirar intents i NT_ENVIAR o cap a NOTIFICA_ERROR
					eventSm = e.getNotificaIntentNum() < configHelper.getMaxReintentsNotifca() ? EnviamentSmEvent.NT_ENVIAR : EnviamentSmEvent.NT_ERROR;
					intent = e.getNotificaIntentNum();
	//			} else if (NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(estat) && e.isPerEmail()) { //Enviament registrat i pendent d'enviar per email
	//				eventSm = e.getNotificaIntentNum() < configHelper.getMaxReintentsConsultaNotifica() ? EnviamentSmEvent.CN_CONSULTAR : EnviamentSmEvent.NT_ERROR;
	//				intent = e.getNotificaIntentNum();
				} else {// if (NotificacioEstatEnumDto.ENVIADA.equals(estat)) {
					// Si es SIR mirar els reintents de consulta mirar el maxim de reintents. Si max reintents ? SR_CONSULTAR : SIR ERROR
					// Si es enviada a notifica mirar si enviament té els intents esgotats de consulta ? NOTIFICA_ERROR : NOTIFICA_SENT
					if (not.isComunicacioSir()) {
						eventSm = e.getSirConsultaIntent() < configHelper.getMaxReintentsConsultaSir() ? EnviamentSmEvent.SR_CONSULTAR : EnviamentSmEvent.SR_ERROR;
						intent = e.getSirConsultaIntent();
					} else if (!e.isNotificaEstatFinal()) { // No es processen les enviades correctament
						eventSm = e.getNotificaIntentNum() < configHelper.getMaxReintentsConsultaNotifica() ? EnviamentSmEvent.CN_CONSULTAR : EnviamentSmEvent.CN_ERROR;
						intent = e.getNotificaIntentNum();
					}
				}
				afegirEnviament(e, eventSm, intent);
			});
			return true;
		} catch (Exception ex) {
			log.error("Error afegint la notificacio " + notificacioId + " a la màquina d'estats");
			return false;
		}
	}

	private boolean afegirEnviament(NotificacioEnviamentEntity env, EnviamentSmEvent eventSm, int intent) {

		try {
			log.debug("Afegint a la màquina l'enviament amb id " + env.getUuid());
			var sm = stateMachineService.acquireStateMachine(env.getUuid());
			sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, intent);
			sendEvent(env.getUuid(), sm, eventSm);
			return true;
		} catch (Exception ex) {
			log.error("Error al afegir a la màquina l'enviament " + env.getUuid(), ex);
			return false;
		}
	}

	@Override
	@Transactional
	public boolean canviarEstat(Long enviamentId, String estat) {

		try {
			log.debug("Canviant a l'estat " + estat + " de la màquina per l'enviament amb id " + enviamentId);
			var uuId = enviamentRepository.getUuidById(enviamentId);
			var sm = stateMachineService.acquireStateMachine(uuId);
			sm.getStateMachineAccessor().doWithAllRegions(access -> access
					.resetStateMachine(new DefaultStateMachineContext<>(EnviamentSmEstat.valueOf(estat), null, null,null)));
			return true;
		} catch (Exception ex) {
			log.error("Error canviant l'estat de la màquina per l'enviament " + enviamentId, ex);
			return false;
		}
	}

	@Override
	public boolean enviarEvent(Long enviamentId, String event) {

		try {
			log.debug("Enviant l'event " + event + " a la màquina per l'enviament amb id " + enviamentId);
			var uuId = enviamentRepository.getUuidById(enviamentId);
			var sm = stateMachineService.acquireStateMachine(uuId);
			sendEvent(uuId, sm, EnviamentSmEvent.valueOf(event));
			return true;
		} catch (Exception ex) {
			log.error("Error enviament l'event a la màquina per l'enviament " + enviamentId, ex);
			return false;
		}
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> altaEnviament(String enviamentUuid, Long delay) {

		log.debug("[SM] Alta enviament " + enviamentUuid + " delay " + delay);
		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		var enviament = enviamentRepository.findByUuid(enviamentUuid).orElseThrow();
		var variables = sm.getExtendedState().getVariables();
		variables.put(SmConstants.ENVIAMENT_TIPUS, enviament.getNotificacio().getEnviamentTipus().name());
		variables.put(SmConstants.ENVIAMENT_SENSE_NIF, enviament.isPerEmail());
		variables.put(SmConstants.ENVIAMENT_DELAY, delay);
		// Enviam a registre
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.RG_ENVIAR);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> altaEnviament(String enviamentUuid) {

		log.debug("[SM] Alta enviament " + enviamentUuid);
		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		var enviament = enviamentRepository.findByUuid(enviamentUuid).orElseThrow();
		var variables = sm.getExtendedState().getVariables();
		variables.put(SmConstants.ENVIAMENT_TIPUS, enviament.getNotificacio().getEnviamentTipus().name());
		variables.put(SmConstants.ENVIAMENT_SENSE_NIF, enviament.isPerEmail());
		// Enviam a registre
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.RG_ENVIAR);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreEnviament(String enviamentUuid, boolean retry) {

		log.debug("[SM] Registre enviament " + enviamentUuid + " retry " + retry);
		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		var variables = sm.getExtendedState().getVariables();
		variables.put(SmConstants.RG_RETRY, retry);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.RG_ENVIAR);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreSuccess(String enviamentUuid) {

		log.debug("[SM] Registre succes enviament " + enviamentUuid);
		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, 0);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.RG_SUCCESS);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreFailed(String enviamentUuid) {

		log.debug("[SM] Registre failed enviament " + enviamentUuid);
		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		var reintents = (int)sm.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0);
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, reintents + 1);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.RG_ERROR);
		stateMachineService.releaseStateMachine(enviamentUuid);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreReset(String enviamentUuid) {

		log.debug("[SM] Registre reset enviament " + enviamentUuid);
		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, 0);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.RG_RETRY);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreRetry(String enviamentUuid) {

		log.debug("[SM] Registre retry enviament " + enviamentUuid);
		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.RG_RETRY);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreForward(String enviamentUuid) {

		log.debug("[SM] Registre forward enviament " + enviamentUuid);
		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, 0);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.RG_FORWARD);
		return sm;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean enviamentIsInRegistreErrorState(String enviamentUuid) {
		return EnviamentSmEstat.REGISTRE_ERROR.equals(getEstatEnviament(enviamentUuid));
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaEnviament(String enviamentUuid, boolean retry) {

		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sm.getExtendedState().getVariables().put(SmConstants.NT_RETRY, retry);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.NT_ENVIAR);
		return sm;
	}

	@Override
	@Transactional
	public void notificaFi(String enviamentUuid) {

		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.NT_FI);
		stateMachineService.releaseStateMachine(enviamentUuid);
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaSuccess(String enviamentUuid) {

		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, 0);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.NT_SUCCESS);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaFailed(String enviamentUuid) {

		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		var reintents = (int)sm.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0);
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, reintents + 1);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.NT_ERROR);
		stateMachineService.releaseStateMachine(enviamentUuid);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaRetry(String enviamentUuid) {

		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.NT_RETRY);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaReset(String enviamentUuid) {

		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, 0);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.NT_RETRY);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaForward(String enviamentUuid) {

		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, 0);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.NT_FORWARD);
		return sm;
	}

//	@Override
//	@Transactional
//	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> emailEnviament(String enviamentUuid) {
//		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
//		sendEvent(enviamentUuid, sm, EnviamentSmEvent.EM_ENVIAR);
//		return sm;
//	}
//
//	@Override
//	@Transactional
//	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> emailSuccess(String enviamentUuid) {
//		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
//		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, 0);
//		sendEvent(enviamentUuid, sm, EnviamentSmEvent.EM_SUCCESS);
//		return sm;
//	}
//
//	@Override
//	@Transactional
//	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> emailFailed(String enviamentUuid) {
//		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
//		sm.getExtendedState().getVariables().put(
//				SmConstants.ENVIAMENT_REINTENTS,
//				(int)sm.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0) + 1);
//		sendEvent(enviamentUuid, sm, EnviamentSmEvent.EM_RETRY);
//		return sm;
//	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> enviamentConsulta(String enviamentUuid) {

		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.CN_CONSULTAR);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaSuccess(String enviamentUuid) {

		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		var enviament = enviamentRepository.findByUuid(enviamentUuid).orElseThrow();
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_ESTAT_FINAL,enviament.isNotificaEstatFinal());
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, 0);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.CN_SUCCESS);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaFailed(String enviamentUuid) {

		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		var reintents = (int)sm.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0);
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, reintents + 1);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.CN_ERROR);
		stateMachineService.releaseStateMachine(enviamentUuid);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaReset(String enviamentUuid) {

		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, 0);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.CN_RETRY);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaRetry(String enviamentUuid) {

		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, 0);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.CN_RETRY);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaForward(String enviamentUuid) {

		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, 0);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.CN_FORWARD);
		stateMachineService.releaseStateMachine(enviamentUuid);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirConsulta(String enviamentUuid) {

		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sm.getExtendedState().getVariables().put(SmConstants.CONSULTA_POOLING, true);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.SR_CONSULTAR);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirSuccess(String enviamentUuid) {

		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		var enviament = enviamentRepository.findByUuid(enviamentUuid).orElseThrow();
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_ESTAT_FINAL,enviament.isRegistreEstatFinal());
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, 0);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.SR_SUCCESS);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirFailed(String enviamentUuid) {

		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
 		var reintents = (int)sm.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0);
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, reintents + 1);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.SR_ERROR);
		stateMachineService.releaseStateMachine(enviamentUuid);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirReset(String enviamentUuid) {

		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, 0);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.SR_RESET);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirRetry(String enviamentUuid) {

		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.SR_RETRY);
		return sm;
	}

    @Override
	@Transactional
    public StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirForward(String enviamentUuid) {

		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, 0);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.SR_FORWARD);
		stateMachineService.releaseStateMachine(enviamentUuid);
		return sm;
    }

    @Override
	@Transactional
	public void remove(String enviamentUuid) {
		stateMachineService.releaseStateMachine(enviamentUuid, true);
	}

	@Override
	@Transactional(readOnly = true)
	public EnviamentSmEstat getEstatEnviament(String enviamentUuid) {

		var sm = stateMachineService.acquireStateMachine(enviamentUuid);
		return sm.getState().getId();
	}

	private void sendEvent(String enviamentUuid, StateMachine<EnviamentSmEstat, EnviamentSmEvent> sm, EnviamentSmEvent event) {

		var msg = MessageBuilder.withPayload(event).setHeader(SmConstants.ENVIAMENT_UUID_HEADER, enviamentUuid).build();
		log.debug("[SM] Sent event " + msg.getPayload().name() + " enviament " + enviamentUuid);
		sm.sendEvent(msg);
	}
}

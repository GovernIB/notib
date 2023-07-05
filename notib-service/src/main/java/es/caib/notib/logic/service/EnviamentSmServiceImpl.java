package es.caib.notib.logic.service;

import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementació del servei de gestió de enviaments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class EnviamentSmServiceImpl implements EnviamentSmService {

	private final NotificacioEnviamentRepository notificacioEnviamentRepository;
//	private final NotificaHelper notificaHelper;
	private final StateMachineService<EnviamentSmEstat, EnviamentSmEvent> stateMachineService;
//	private final ConfigHelper configHelper;


	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> altaEnviament(String enviamentUuid) {
		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		var enviament = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElseThrow();
		var variables = sm.getExtendedState().getVariables();
		variables.put(SmConstants.ENVIAMENT_TIPUS, enviament.getNotificacio().getEnviamentTipus().name());
		variables.put(SmConstants.ENVIAMENT_SENSE_NIF, enviament.isPerEmail());

//		sendEvent(enviamentUuid, sm, EnviamentSmEvent.RG_ENVIAR);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreEnviament(String enviamentUuid) {
		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.RG_ENVIAR);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreSuccess(String enviamentUuid) {
		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, 0);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.RG_SUCCESS);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreFailed(String enviamentUuid) {
		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sm.getExtendedState().getVariables().put(
				SmConstants.ENVIAMENT_REINTENTS,
				(int)sm.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0) + 1);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.RG_ERROR);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaEnviament(String enviamentUuid) {
		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.NT_ENVIAR);
		return sm;
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
		sm.getExtendedState().getVariables().put(
				SmConstants.ENVIAMENT_REINTENTS,
				(int)sm.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0) + 1);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.NT_RETRY);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> emailEnviament(String enviamentUuid) {
		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.EM_ENVIAR);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> emailSuccess(String enviamentUuid) {
		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, 0);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.EM_SUCCESS);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> emailFailed(String enviamentUuid) {
		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sm.getExtendedState().getVariables().put(
				SmConstants.ENVIAMENT_REINTENTS,
				(int)sm.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0) + 1);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.EM_RETRY);
		return sm;
	}

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
		var enviament = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElseThrow();
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_ESTAT_FINAL,enviament.isNotificaEstatFinal());
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, 0);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.CN_SUCCESS);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaFailed(String enviamentUuid) {
		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sm.getExtendedState().getVariables().put(
				SmConstants.ENVIAMENT_REINTENTS,
				(int)sm.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0) + 1);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.CN_RETRY);
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
		var enviament = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElseThrow();
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_ESTAT_FINAL,enviament.isRegistreEstatFinal());
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, 0);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.SR_SUCCESS);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirFailed(String enviamentUuid) {
		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sm.getExtendedState().getVariables().put(
				SmConstants.ENVIAMENT_REINTENTS,
				(int)sm.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0) + 1);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.SR_RETRY);
		return sm;
	}

	@Override
	@Transactional
	public void remove(String enviamentUuid) {
		stateMachineService.releaseStateMachine(enviamentUuid, true);
	}

//	private int getMaxRegistreReintents() {
//		return configHelper.getConfigAsInteger("es.caib.notib.tasca.registre.enviaments.reintents.maxim", 3);
//	}
//
//	private int getMaxNotificaReintents() {
//		return configHelper.getConfigAsInteger("es.caib.notib.tasca.notifica.enviaments.reintents.maxim", 3);
//	}
//
//	private int getMaxEmailReintents() {
//		return configHelper.getConfigAsInteger("es.caib.notib.tasca.notifica.enviaments.reintents.maxim", 3);
//	}
//
//	private int getMaxConsultaReintents() {
//		return configHelper.getConfigAsInteger("es.caib.notib.tasca.enviament.actualitzacio.estat.reintents.maxim", 3);
//	}
//
//	private int getMaxConsultaSirReintents() {
//		return configHelper.getConfigAsInteger("es.caib.notib.tasca.enviament.actualitzacio.estat.registre.reintents.maxim", 3);
//	}

	private void sendEvent(String enviamentUuid, StateMachine<EnviamentSmEstat, EnviamentSmEvent> sm, EnviamentSmEvent event) {
		var msg = MessageBuilder.withPayload(event)
				.setHeader(SmConstants.ENVIAMENT_UUID_HEADER, enviamentUuid)
				.build();
		sm.sendEvent(msg);
	}


//	private StateMachine<EnviamentSmEstat, EnviamentSmEvent> smBuild(Long enviamentId) {
//		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
//
//		StateMachine<EnviamentSmEstat, EnviamentSmEvent> sm = stateMachineFactory.getStateMachine(Long.toString(enviament.getId()));
//
//		sm.stop();
//		sm.getStateMachineAccessor()
//				.doWithAllRegions(sma -> {
//					sma.resetStateMachine(new DefaultStateMachineContext<>(enviament.getSmEstat(), null, null, null));
//				});
//		sm.start();
//
//		return sm;
//	}

//	@PostConstruct
//	public void init() {
//		enviamentStateMachine = stateMachineFactory.getStateMachine();
//	}
}

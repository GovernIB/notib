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
	private final StateMachineService<EnviamentSmEstat, EnviamentSmEvent> stateMachineService;

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> altaEnviament(String enviamentUuid) {

		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		var enviament = notificacioEnviamentRepository.findByUuid(enviamentUuid).orElseThrow();
		var variables = sm.getExtendedState().getVariables();
		variables.put(SmConstants.ENVIAMENT_TIPUS, enviament.getNotificacio().getEnviamentTipus().name());
		variables.put(SmConstants.ENVIAMENT_SENSE_NIF, enviament.isPerEmail());
		// Enviam a registre
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.RG_ENVIAR);
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
		var reintents = (int)sm.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0);
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, reintents + 1);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.RG_ERROR);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreReset(String enviamentUuid) {

		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, 0);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.RG_RETRY);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreRetry(String enviamentUuid) {

		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.RG_RETRY);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreForward(String enviamentUuid) {

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
		var reintents = (int)sm.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0);
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, reintents + 1);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.NT_ERROR);
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
		var reintents = (int)sm.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0);
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, reintents + 1);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.CN_ERROR);
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
		var reintents = (int)sm.getExtendedState().getVariables().getOrDefault(SmConstants.ENVIAMENT_REINTENTS, 0);
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, reintents + 1);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.SR_ERROR);
		return sm;
	}

	@Override
	@Transactional
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirRetry(String enviamentUuid) {

		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, 0);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.SR_RETRY);
		return sm;
	}

    @Override
	@Transactional
    public StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirForward(String enviamentUuid) {

		var sm = stateMachineService.acquireStateMachine(enviamentUuid, true);
		sm.getExtendedState().getVariables().put(SmConstants.ENVIAMENT_REINTENTS, 0);
		sendEvent(enviamentUuid, sm, EnviamentSmEvent.SR_FORWARD);
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

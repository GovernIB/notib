/**
 * 
 */
package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.dto.stateMachine.StateMachineInfo;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;

/**
 * Declaració dels mètodes per a la consulta de notificacions i dels
 * destinataris i events associats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EnviamentSmService {


	@PreAuthorize("isAuthenticated()")
	EnviamentSmEstat getEstat(String enviamentUuid);

	@PreAuthorize("hasRole('NOT_SUPER')")
	boolean mostrarAfegirStateMachine(Long notificacioId);

	@PreAuthorize("hasRole('NOT_SUPER')")
	StateMachineInfo infoStateMachine(Long enviamentId);

	void afegirNotificacions();

	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN')")
	boolean afegirNotificacio(Long notificacioId);

	@PreAuthorize("isAuthenticated()")
	void acquireStateMachine(String uuid);

	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN')")
	boolean canviarEstat(Long enviamentId, String estat);

	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN')")
	boolean enviarEvent(Long enviamentId, String event);

	@PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> altaEnviament(String enviamentUuid);

	@PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> altaEnviament(String enviamentUuid, Long delay);

	@PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreEnviament(String enviamentUuid, boolean retry);

	@PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreSuccess(String enviamentUuid);

	@PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreFailed(String enviamentUuid);

	@PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreReset(String enviamentUuid, long delay);

	@PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreRetry(String enviamentUuid);

	@PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreForward(String enviamentUuid);

	@PreAuthorize("isAuthenticated()")
    boolean enviamentIsInRegistreErrorState(String enviamentUuid);

    @PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaEnviament(String enviamentUuid, boolean retry);

    @PreAuthorize("isAuthenticated()")
	void notificaFi(String notificaReferencia);

	@PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaSuccess(String enviamentUuid);

	@PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaFailed(String enviamentUuid);

	@PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaRetry(String enviamentUuid);

	@PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaReset(String enviamentUuid, long delay);

	@PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaForward(String enviamentUuid);

	@PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> enviamentConsulta(String enviamentUuid);

	@PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaSuccess(String enviamentUuid);

	@PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaFailed(String enviamentUuid);

	@PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaReset(String enviamentUuid);

	@PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaRetry(String enviamentUuid);

	@PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaForward(String enviamentUuid);

	@PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirConsulta(String enviamentUuid);

	@PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirSuccess(String enviamentUuid);

	@PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirFailed(String enviamentUuid);

	@PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirReset(String enviamentUuid);

	@PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirRetry(String enviamentUuid);

	@PreAuthorize("isAuthenticated()")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirForward(String enviamentUuid);

	//Us intern de la SM

	@PreAuthorize("isAuthenticated()")
	EnviamentSmEstat getEstatEnviament(String enviamentUuid);

	@PreAuthorize("isAuthenticated()")
	void remove(String enviamentUuid);
}

/**
 * 
 */
package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.statemachine.StateMachine;

/**
 * Declaració dels mètodes per a la consulta de notificacions i dels
 * destinataris i events associats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EnviamentSmService {

	@PreAuthorize("hasRole('tothom')")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> altaEnviament(String enviamentUuid);

	@PreAuthorize("hasRole('tothom')")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreEnviament(String enviamentUuid);

	@PreAuthorize("hasRole('tothom')")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreSuccess(String enviamentUuid);

	@PreAuthorize("hasRole('tothom')")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreFailed(String enviamentUuid);

	@PreAuthorize("hasRole('tothom')")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreReset(String enviamentUuid);

	@PreAuthorize("hasRole('tothom')")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreRetry(String enviamentUuid);

	@PreAuthorize("hasRole('tothom')")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreForward(String enviamentUuid);

	@PreAuthorize("hasRole('tothom')")
    boolean enviamentIsInRegistreErrorState(String enviamentUuid);

    @PreAuthorize("hasRole('tothom')")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaEnviament(String enviamentUuid);

	@PreAuthorize("hasRole('tothom')")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaSuccess(String enviamentUuid);

	@PreAuthorize("hasRole('tothom')")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaFailed(String enviamentUuid);

	@PreAuthorize("hasRole('tothom')")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaRetry(String enviamentUuid);

	@PreAuthorize("hasRole('tothom')")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaReset(String enviamentUuid);

	@PreAuthorize("hasRole('tothom')")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaForward(String enviamentUuid);

//	@PreAuthorize("hasRole('tothom')")
//	StateMachine<EnviamentSmEstat, EnviamentSmEvent> emailEnviament(String enviamentUuid);
//
//	@PreAuthorize("hasRole('tothom')")
//	StateMachine<EnviamentSmEstat, EnviamentSmEvent> emailSuccess(String enviamentUuid);
//
//	@PreAuthorize("hasRole('tothom')")
//	StateMachine<EnviamentSmEstat, EnviamentSmEvent> emailFailed(String enviamentUuid);

	@PreAuthorize("hasRole('tothom')")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> enviamentConsulta(String enviamentUuid);

	@PreAuthorize("hasRole('tothom')")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaSuccess(String enviamentUuid);

	@PreAuthorize("hasRole('tothom')")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaFailed(String enviamentUuid);

	@PreAuthorize("hasRole('tothom')")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaRetry(String enviamentUuid);

	@PreAuthorize("hasRole('tothom')")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaForward(String enviamentUuid);

	@PreAuthorize("hasRole('tothom')")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirConsulta(String enviamentUuid);

	@PreAuthorize("hasRole('tothom')")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirSuccess(String enviamentUuid);

	@PreAuthorize("hasRole('tothom')")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirFailed(String enviamentUuid);

	@PreAuthorize("hasRole('tothom')")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirRetry(String enviamentUuid);

	@PreAuthorize("hasRole('tothom')")
	StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirForward(String enviamentUuid);

	@PreAuthorize("hasRole('tothom')")
	EnviamentSmEstat getEstatEnviament(String enviamentUuid);

	@PreAuthorize("hasRole('tothom')")
	void remove(String enviamentUuid);
}

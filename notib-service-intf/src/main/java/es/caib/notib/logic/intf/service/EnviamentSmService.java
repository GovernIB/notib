/**
 *
 */
package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.dto.stateMachine.StateMachineInfo;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import es.caib.notib.logic.intf.statemachine.dto.ParametresSm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.statemachine.StateMachine;

/**
 * Declaració dels mètodes per a la consulta de notificacions i dels
 * destinataris i events associats.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EnviamentSmService {

	EnviamentSmEstat getEstat(String enviamentUuid);

	@PreAuthorize("hasRole('NOT_SUPER')")
	/**/boolean mostrarAfegirStateMachine(Long notificacioId);

	@PreAuthorize("hasRole('NOT_SUPER')")
	/**/StateMachineInfo infoStateMachine(Long enviamentId);

	void afegirNotificacions();

	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN')")
	/**/boolean afegirNotificacio(Long notificacioId);

	void acquireStateMachine(String uuid);

	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN')")
	/**/boolean canviarEstat(Long enviamentId, String estat);

	@PreAuthorize("hasRole('NOT_SUPER') or hasRole('NOT_ADMIN')")
	/**/boolean enviarEvent(Long enviamentId, String event);

	StateMachine<EnviamentSmEstat, EnviamentSmEvent> altaEnviament(String enviamentUuid);

	@PreAuthorize("isAuthenticated()")
	/**/StateMachine<EnviamentSmEstat, EnviamentSmEvent> altaEnviamentWeb(String enviamentUuid);



	StateMachine<EnviamentSmEstat, EnviamentSmEvent> altaEnviament(String enviamentUuid, Long delay);

	StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreEnviament(String enviamentUuid, boolean retry);

	StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreSuccess(String enviamentUuid);

	StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreFailed(String enviamentUuid);

	StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreReset(String enviamentUuid, long delay);

	StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreRetry(String enviamentUuid);

	StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreForward(String enviamentUuid);

    boolean enviamentIsInRegistreErrorState(String enviamentUuid);

	StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaEnviament(String enviamentUuid, boolean retry);

	void notificaFi(String notificaReferencia);

	StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaSuccess(String enviamentUuid);

	StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaFailed(String enviamentUuid);

	StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaRetry(String enviamentUuid);

	StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaReset(String enviamentUuid, long delay);

	StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaForward(String enviamentUuid);

	StateMachine<EnviamentSmEstat, EnviamentSmEvent> enviamentConsulta(String enviamentUuid);

	StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaSuccess(String enviamentUuid);

	StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaFailed(String enviamentUuid);

	StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaReset(ParametresSm parametres);

	StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaRetry(ParametresSm parametres);

	StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaForward(String enviamentUuid);

	StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirConsulta(String enviamentUuid);

	StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirSuccess(String enviamentUuid);

	StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirFailed(String enviamentUuid);

	StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirReset(ParametresSm parametres);

	StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirRetry(ParametresSm parametresSm);

	StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirForward(String enviamentUuid);

	EnviamentSmEstat getEstatEnviament(String enviamentUuid);

	void remove(String enviamentUuid);

}

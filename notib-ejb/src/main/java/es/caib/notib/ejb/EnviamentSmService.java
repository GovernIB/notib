/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.stateMachine.StateMachineInfo;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEvent;
import org.springframework.context.annotation.Primary;
import org.springframework.statemachine.StateMachine;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * Implementació de EnviamentService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class EnviamentSmService extends AbstractService<es.caib.notib.logic.intf.service.EnviamentSmService> implements es.caib.notib.logic.intf.service.EnviamentSmService {


	@Override
	@RolesAllowed("**")
	public EnviamentSmEstat getEstat(String enviamentUuid) {
		return getDelegateService().getEstat(enviamentUuid);
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public boolean mostrarAfegirStateMachine(Long notificacioId) {
		return getDelegateService().mostrarAfegirStateMachine(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public StateMachineInfo infoStateMachine(Long enviamentId) {
		return getDelegateService().infoStateMachine(enviamentId);
	}

	@Override
	public void afegirNotificacions() {
		getDelegateService().afegirNotificacions();
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public boolean afegirNotificacio(Long notificacioId) {
		return getDelegateService().afegirNotificacio(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public boolean canviarEstat(Long enviamentId, String estat) {
		return getDelegateService().canviarEstat(enviamentId, estat);
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public boolean enviarEvent(Long enviamentId, String event) {
		return getDelegateService().enviarEvent(enviamentId, event);
	}

	@Override
	@RolesAllowed("**")
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> altaEnviament(String enviamentUuid) {
		return getDelegateService().altaEnviament(enviamentUuid);
	}

	@Override
	@RolesAllowed("**")
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreEnviament(String enviamentUuid, boolean retry) {
		return getDelegateService().registreEnviament(enviamentUuid, retry);
	}

	@Override
	@RolesAllowed("**")
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreSuccess(String enviamentUuid) {
		return getDelegateService().registreSuccess(enviamentUuid);
	}

	@Override
	@RolesAllowed("**")
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreFailed(String enviamentUuid) {
		return getDelegateService().registreFailed(enviamentUuid);
	}

	@Override
	@RolesAllowed("**")
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreReset(String enviamentUuid) {
		return getDelegateService().registreReset(enviamentUuid);
	}

	@Override
	@RolesAllowed("**")
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreRetry(String enviamentUuid) {
		return getDelegateService().registreRetry(enviamentUuid);
	}

	@Override
	@RolesAllowed("**")
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreForward(String enviamentUuid) {
		return getDelegateService().registreForward(enviamentUuid);
	}

	@Override
	@RolesAllowed("**")
    public boolean enviamentIsInRegistreErrorState(String enviamentUuid) {
        return getDelegateService().enviamentIsInRegistreErrorState(enviamentUuid);
    }

    @Override
	@RolesAllowed("**")
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaEnviament(String enviamentUuid, boolean retry) {
		return getDelegateService().notificaEnviament(enviamentUuid, retry);
	}

	@Override
	@RolesAllowed("**")
	public void notificaFi(String notificaReferencia) {
		getDelegateService().notificaFi(notificaReferencia);
	}

	@Override
	@RolesAllowed("**")
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaSuccess(String enviamentUuid) {
		return getDelegateService().notificaSuccess(enviamentUuid);
	}

	@Override
	@RolesAllowed("**")
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaFailed(String enviamentUuid) {
		return getDelegateService().notificaFailed(enviamentUuid);
	}

	@Override
	@RolesAllowed("**")
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaRetry(String enviamentUuid) {
		return getDelegateService().notificaRetry(enviamentUuid);
	}

	@Override
	@RolesAllowed("**")
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaReset(String enviamentUuid) {
		return getDelegateService().notificaReset(enviamentUuid);
	}

	@Override
	@RolesAllowed("**")
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaForward(String enviamentUuid) {
		return getDelegateService().notificaForward(enviamentUuid);
	}

	@Override
	@RolesAllowed("**")
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> enviamentConsulta(String enviamentUuid) {
		return getDelegateService().enviamentConsulta(enviamentUuid);
	}

	@Override
	@RolesAllowed("**")
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaSuccess(String enviamentUuid) {
		return getDelegateService().consultaSuccess(enviamentUuid);
	}

	@Override
	@RolesAllowed("**")
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaFailed(String enviamentUuid) {
		return getDelegateService().consultaFailed(enviamentUuid);
	}

	@Override
	@RolesAllowed("**")
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaReset(String enviamentUuid) {
		return getDelegateService().consultaReset(enviamentUuid);
	}

	@Override
	@RolesAllowed("**")
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaRetry(String enviamentUuid) {
		return getDelegateService().consultaRetry(enviamentUuid);
	}

	@Override
	@RolesAllowed("**")
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaForward(String enviamentUuid) {
		return getDelegateService().consultaForward(enviamentUuid);
	}

	@Override
	@RolesAllowed("**")
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirConsulta(String enviamentUuid) {
		return getDelegateService().sirConsulta(enviamentUuid);
	}

	@Override
	@RolesAllowed("**")
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirSuccess(String enviamentUuid) {
		return getDelegateService().sirSuccess(enviamentUuid);
	}

	@Override
	@RolesAllowed("**")
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirFailed(String enviamentUuid) {
		return getDelegateService().sirFailed(enviamentUuid);
	}

	@Override
	@RolesAllowed("**")
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirReset(String enviamentUuid) {
		return getDelegateService().sirRetry(enviamentUuid);
	}

	@Override
	@RolesAllowed("**")
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirRetry(String enviamentUuid) {
		return getDelegateService().sirRetry(enviamentUuid);
	}

    @Override
	@RolesAllowed("**")
    public StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirForward(String enviamentUuid) {
        return getDelegateService().sirForward(enviamentUuid);
    }

    @Override
	@RolesAllowed("**")
	public EnviamentSmEstat getEstatEnviament(String enviamentUuid) {
		return getDelegateService().getEstatEnviament(enviamentUuid);
	}

	@Override
	public void remove(String enviamentUuid) {
		// empty
	}

}

/**
 * 
 */
package es.caib.notib.ejb;

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
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> altaEnviament(String enviamentUuid) {
		return getDelegateService().altaEnviament(enviamentUuid);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreEnviament(String enviamentUuid) {
		return getDelegateService().registreEnviament(enviamentUuid);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreSuccess(String enviamentUuid) {
		return getDelegateService().registreSuccess(enviamentUuid);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreFailed(String enviamentUuid) {
		return getDelegateService().registreFailed(enviamentUuid);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreReset(String enviamentUuid) {
		return getDelegateService().registreReset(enviamentUuid);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreRetry(String enviamentUuid) {
		return getDelegateService().registreRetry(enviamentUuid);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> registreForward(String enviamentUuid) {
		return getDelegateService().registreForward(enviamentUuid);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
    public boolean enviamentIsInRegistreErrorState(String enviamentUuid) {
        return getDelegateService().enviamentIsInRegistreErrorState(enviamentUuid);
    }

    @Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaEnviament(String enviamentUuid) {
		return getDelegateService().notificaEnviament(enviamentUuid);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaSuccess(String enviamentUuid) {
		return getDelegateService().notificaSuccess(enviamentUuid);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaFailed(String enviamentUuid) {
		return getDelegateService().notificaFailed(enviamentUuid);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaRetry(String enviamentUuid) {
		return getDelegateService().notificaRetry(enviamentUuid);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaReset(String enviamentUuid) {
		return getDelegateService().notificaReset(enviamentUuid);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> notificaForward(String enviamentUuid) {
		return getDelegateService().notificaForward(enviamentUuid);
	}

//	@Override
//	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
//	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> emailEnviament(String enviamentUuid) {
//		return getDelegateService().emailEnviament(enviamentUuid);
//	}
//
//	@Override
//	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
//	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> emailSuccess(String enviamentUuid) {
//		return getDelegateService().emailSuccess(enviamentUuid);
//	}
//
//	@Override
//	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
//	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> emailFailed(String enviamentUuid) {
//		return getDelegateService().emailFailed(enviamentUuid);
//	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> enviamentConsulta(String enviamentUuid) {
		return getDelegateService().enviamentConsulta(enviamentUuid);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaSuccess(String enviamentUuid) {
		return getDelegateService().consultaSuccess(enviamentUuid);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaFailed(String enviamentUuid) {
		return getDelegateService().consultaFailed(enviamentUuid);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaRetry(String enviamentUuid) {
		return getDelegateService().consultaRetry(enviamentUuid);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> consultaForward(String enviamentUuid) {
		return getDelegateService().consultaForward(enviamentUuid);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirConsulta(String enviamentUuid) {
		return getDelegateService().sirConsulta(enviamentUuid);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirSuccess(String enviamentUuid) {
		return getDelegateService().sirSuccess(enviamentUuid);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirFailed(String enviamentUuid) {
		return getDelegateService().sirFailed(enviamentUuid);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirRetry(String enviamentUuid) {
		return getDelegateService().sirRetry(enviamentUuid);
	}

    @Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
    public StateMachine<EnviamentSmEstat, EnviamentSmEvent> sirForward(String enviamentUuid) {
        return getDelegateService().sirForward(enviamentUuid);
    }

    @Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public EnviamentSmEstat getEstatEnviament(String enviamentUuid) {
		return getDelegateService().getEstatEnviament(enviamentUuid);
	}

	@Override
	public void remove(String enviamentUuid) {

	}

}

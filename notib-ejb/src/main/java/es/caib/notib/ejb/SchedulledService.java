/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * Implementació de Schedulled Service com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class SchedulledService extends AbstractService<es.caib.notib.logic.intf.service.SchedulledService> implements es.caib.notib.logic.intf.service.SchedulledService {

	@Autowired
	SchedulledService delegate;

//	@Override
//	@RolesAllowed({"NOT_SUPER"})
//	public void restartSchedulledTasks() {
//		delegate.restartSchedulledTasks();
//	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public void notificaEnviamentsRegistrats() {
		delegate.notificaEnviamentsRegistrats();
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public void registrarEnviamentsPendents() throws RegistreNotificaException {
		delegate.registrarEnviamentsPendents();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public void enviamentRefrescarEstatPendents() {
		delegate.enviamentRefrescarEstatPendents();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public void enviamentRefrescarEstatEnviatSir() {
		delegate.enviamentRefrescarEstatEnviatSir();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public void actualitzarProcediments() {
		delegate.actualitzarProcediments();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public void refrescarNotificacionsExpirades() {
		delegate.refrescarNotificacionsExpirades();
	}

	@Override
	public void enviamentRefrescarEstatDEH() {
		delegate.enviamentRefrescarEstatDEH();
	}

	@Override
	public void enviamentRefrescarEstatCIE() {
		delegate.enviamentRefrescarEstatCIE();
	}

	@Override
	public void eliminarDocumentsTemporals() {
		delegate.eliminarDocumentsTemporals();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public void actualitzarServeis() {
		delegate.actualitzarServeis();
	}

    @Override
    public void consultaCanvisOrganigrama() {
        delegate.consultaCanvisOrganigrama();
    }

}

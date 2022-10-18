/**
 * 
 */
package es.caib.notib.core.ejb;

import es.caib.notib.core.api.exception.RegistreNotificaException;
import es.caib.notib.core.api.service.SchedulledService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Implementació de Schedulled Service com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class SchedulledServiceBean implements SchedulledService {

	@Autowired
	SchedulledService delegate;

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public void restartSchedulledTasks() {
		delegate.restartSchedulledTasks();
	}

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

	@Override
	public void monitorIntegracionsEliminarAntics() {

	}

}

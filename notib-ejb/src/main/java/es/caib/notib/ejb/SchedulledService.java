/**
 * 
 */
package es.caib.notib.ejb;

import org.springframework.context.annotation.Primary;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * Implementació de Schedulled Service com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class SchedulledService extends AbstractService<es.caib.notib.logic.intf.service.SchedulledService> implements es.caib.notib.logic.intf.service.SchedulledService {

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public void actualitzarProcediments() {
		getDelegateService().actualitzarProcediments();
	}

	@Override
	@RolesAllowed("**")
	public void refrescarNotificacionsExpirades() {
		getDelegateService().refrescarNotificacionsExpirades();
	}

	@Override
	@PermitAll
	public void enviamentRefrescarEstatDEH() {
		getDelegateService().enviamentRefrescarEstatDEH();
	}

	@Override
	@PermitAll
	public void enviamentRefrescarEstatCIE() {
		getDelegateService().enviamentRefrescarEstatCIE();
	}

	@Override
	@PermitAll
	public void eliminarDocumentsTemporals() {
		getDelegateService().eliminarDocumentsTemporals();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public void actualitzarServeis() {
		getDelegateService().actualitzarServeis();
	}

	@Override
	@PermitAll
	public void consultaCanvisOrganigrama() {
		getDelegateService().consultaCanvisOrganigrama();
	}

	@Override
	@PermitAll
	public void monitorIntegracionsEliminarAntics() {
		getDelegateService().monitorIntegracionsEliminarAntics();
	}

	@Override
	@PermitAll
	public void actualitzarEstatOrgansEnviamentTable() {
		getDelegateService().actualitzarEstatOrgansEnviamentTable();
	}

}

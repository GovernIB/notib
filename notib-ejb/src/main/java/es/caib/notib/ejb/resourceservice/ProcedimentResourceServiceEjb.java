package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.ProcedimentResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa ProcedimentResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class ProcedimentResourceServiceEjb extends AbstractServiceEjb<ProcedimentResourceService> implements ProcedimentResourceService {

	@Delegate
	private ProcedimentResourceService delegateService = null;

	@Override
	protected void setDelegateService(ProcedimentResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

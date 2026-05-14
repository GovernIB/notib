package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.EntitatResourceService;
import es.caib.notib.logic.intf.resourceservice.EntregaCieResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa EntregaCieResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class EntregaCieResourceServiceEjb extends AbstractServiceEjb<EntregaCieResourceService> implements EntregaCieResourceService {

	@Delegate
	private EntregaCieResourceService delegateService = null;

	@Override
	protected void setDelegateService(EntregaCieResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

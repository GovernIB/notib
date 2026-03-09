package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.EntitatResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa EntitatResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class EntitatResourceServiceEjb extends AbstractServiceEjb<EntitatResourceService> implements EntitatResourceService {

	@Delegate
	private EntitatResourceService delegateService = null;

	@Override
	protected void setDelegateService(EntitatResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

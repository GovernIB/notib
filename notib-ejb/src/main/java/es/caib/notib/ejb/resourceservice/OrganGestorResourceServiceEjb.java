package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.OrganGestorResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa OrganGestorResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class OrganGestorResourceServiceEjb extends AbstractServiceEjb<OrganGestorResourceService> implements OrganGestorResourceService {

	@Delegate
	private OrganGestorResourceService delegateService = null;

	@Override
	protected void setDelegateService(OrganGestorResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

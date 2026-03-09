package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.AclEntryResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa AclEntryResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class AclEntryResourceServiceEjb extends AbstractServiceEjb<AclEntryResourceService> implements AclEntryResourceService {

	@Delegate
	private AclEntryResourceService delegateService = null;

	@Override
	protected void setDelegateService(AclEntryResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

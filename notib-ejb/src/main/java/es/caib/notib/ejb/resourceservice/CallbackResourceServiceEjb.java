package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.CallbackResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa CallbackResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class CallbackResourceServiceEjb extends AbstractServiceEjb<CallbackResourceService> implements CallbackResourceService {

	@Delegate
	private CallbackResourceService delegateService = null;

	@Override
	protected void setDelegateService(CallbackResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

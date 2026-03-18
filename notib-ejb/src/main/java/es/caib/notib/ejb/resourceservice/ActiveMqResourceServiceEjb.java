package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.ActiveMqResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa ActiveMqResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class ActiveMqResourceServiceEjb extends AbstractServiceEjb<ActiveMqResourceService> implements ActiveMqResourceService {

	@Delegate
	private ActiveMqResourceService delegateService = null;

	@Override
	protected void setDelegateService(ActiveMqResourceService delegateService) {
		this.delegateService = delegateService;
	}
}

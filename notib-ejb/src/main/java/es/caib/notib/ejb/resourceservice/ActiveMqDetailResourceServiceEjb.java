package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.ActiveMqDetailResourceService;
import es.caib.notib.logic.intf.resourceservice.ActiveMqResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa ActiveMqDetailResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class ActiveMqDetailResourceServiceEjb extends AbstractServiceEjb<ActiveMqDetailResourceService> implements ActiveMqDetailResourceService {

	@Delegate
	private ActiveMqDetailResourceService delegateService = null;

	@Override
	protected void setDelegateService(ActiveMqDetailResourceService delegateService) {
		this.delegateService = delegateService;
	}
}

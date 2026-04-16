package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.EventResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa EventResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class EventResourceServiceEjb extends AbstractServiceEjb<EventResourceService> implements EventResourceService {

	@Delegate
	private EventResourceService delegateService = null;

	@Override
	protected void setDelegateService(EventResourceService delegateService) {
		this.delegateService = delegateService;
	}
}

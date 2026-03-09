package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.SseEventService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa SseEventService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class SseEventServiceEjb extends AbstractServiceEjb<SseEventService> implements SseEventService {

	@Delegate
	private SseEventService delegateService = null;

	@Override
	protected void setDelegateService(SseEventService delegateService) {
		this.delegateService = delegateService;
	}

}

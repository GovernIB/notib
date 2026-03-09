package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.NotificacioTableResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa NotificacioTableResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class NotificacioTableResourceServiceEjb extends AbstractServiceEjb<NotificacioTableResourceService> implements NotificacioTableResourceService {

	@Delegate
	private NotificacioTableResourceService delegateService = null;

	@Override
	protected void setDelegateService(NotificacioTableResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

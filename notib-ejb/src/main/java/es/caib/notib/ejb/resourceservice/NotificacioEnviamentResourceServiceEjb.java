package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.NotificacioEnviamentResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa NotificacioEnviamentResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class NotificacioEnviamentResourceServiceEjb extends AbstractServiceEjb<NotificacioEnviamentResourceService> implements NotificacioEnviamentResourceService {

	@Delegate
	private NotificacioEnviamentResourceService delegateService = null;

	@Override
	protected void setDelegateService(NotificacioEnviamentResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

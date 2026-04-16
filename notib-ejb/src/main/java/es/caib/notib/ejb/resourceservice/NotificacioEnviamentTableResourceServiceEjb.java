package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.NotificacioEnviamentTableResourceService;
import es.caib.notib.logic.intf.resourceservice.NotificacioTableResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa NotificacioEnviamentTableResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class NotificacioEnviamentTableResourceServiceEjb extends AbstractServiceEjb<NotificacioEnviamentTableResourceService> implements NotificacioEnviamentTableResourceService {

	@Delegate
	private NotificacioEnviamentTableResourceService delegateService = null;

	@Override
	protected void setDelegateService(NotificacioEnviamentTableResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

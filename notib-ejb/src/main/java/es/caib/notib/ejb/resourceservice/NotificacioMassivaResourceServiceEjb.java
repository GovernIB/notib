package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.NotificacioMassivaResourceService;
import es.caib.notib.logic.intf.resourceservice.NotificacioResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa NotificacioMassivaResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class NotificacioMassivaResourceServiceEjb extends AbstractServiceEjb<NotificacioMassivaResourceService> implements NotificacioMassivaResourceService {

	@Delegate
	private NotificacioMassivaResourceService delegateService = null;

	@Override
	protected void setDelegateService(NotificacioMassivaResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

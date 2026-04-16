package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.NotificacioAuditResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa NotificacioAuditResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class NotificacioAuditResourceServiceEjb extends AbstractServiceEjb<NotificacioAuditResourceService> implements NotificacioAuditResourceService {

	@Delegate
	private NotificacioAuditResourceService delegateService = null;

	@Override
	protected void setDelegateService(NotificacioAuditResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

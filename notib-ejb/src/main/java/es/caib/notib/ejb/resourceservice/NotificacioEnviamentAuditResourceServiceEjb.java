package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.NotificacioEnviamentAuditResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa NotificacioEnviamentAuditResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class NotificacioEnviamentAuditResourceServiceEjb extends AbstractServiceEjb<NotificacioEnviamentAuditResourceService> implements NotificacioEnviamentAuditResourceService {

	@Delegate
	private NotificacioEnviamentAuditResourceService delegateService = null;

	@Override
	protected void setDelegateService(NotificacioEnviamentAuditResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

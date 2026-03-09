package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.MonitorIntegracioResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa MonitorIntegracioResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class MonitorIntegracioResourceServiceEjb extends AbstractServiceEjb<MonitorIntegracioResourceService> implements MonitorIntegracioResourceService {

	@Delegate
	private MonitorIntegracioResourceService delegateService = null;

	@Override
	protected void setDelegateService(MonitorIntegracioResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

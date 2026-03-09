package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.MonitorIntegracioParamResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa MonitorIntegracioParamResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class MonitorIntegracioParamResourceServiceEjb extends AbstractServiceEjb<MonitorIntegracioParamResourceService> implements MonitorIntegracioParamResourceService {

	@Delegate
	private MonitorIntegracioParamResourceService delegateService = null;

	@Override
	protected void setDelegateService(MonitorIntegracioParamResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

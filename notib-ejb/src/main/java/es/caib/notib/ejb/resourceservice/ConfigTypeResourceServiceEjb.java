package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.ConfigTypeResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa ConfigTypeResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class ConfigTypeResourceServiceEjb extends AbstractServiceEjb<ConfigTypeResourceService> implements ConfigTypeResourceService {

	@Delegate
	private ConfigTypeResourceService delegateService = null;

	@Override
	protected void setDelegateService(ConfigTypeResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

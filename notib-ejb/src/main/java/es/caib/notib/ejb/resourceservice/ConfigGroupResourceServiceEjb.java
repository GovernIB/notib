package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.ConfigGroupResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa ConfigGroupResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class ConfigGroupResourceServiceEjb extends AbstractServiceEjb<ConfigGroupResourceService> implements ConfigGroupResourceService {

	@Delegate
	private ConfigGroupResourceService delegateService = null;

	@Override
	protected void setDelegateService(ConfigGroupResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

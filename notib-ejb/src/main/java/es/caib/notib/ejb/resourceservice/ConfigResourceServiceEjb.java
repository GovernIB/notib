package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.ConfigResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa ConfigResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class ConfigResourceServiceEjb extends AbstractServiceEjb<ConfigResourceService> implements ConfigResourceService {

	@Delegate
	private ConfigResourceService delegateService = null;

	@Override
	protected void setDelegateService(ConfigResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

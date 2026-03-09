package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.AplicacioResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa AplicacioResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class AplicacioResourceServiceEjb extends AbstractServiceEjb<AplicacioResourceService> implements AplicacioResourceService {

	@Delegate
	private AplicacioResourceService delegateService = null;

	@Override
	protected void setDelegateService(AplicacioResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

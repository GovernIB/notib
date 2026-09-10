package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.EntregaCieResourceService;
import es.caib.notib.logic.intf.resourceservice.EntregaPostalResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa EntregaCieResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class EntregaPostalResourceServiceEjb extends AbstractServiceEjb<EntregaPostalResourceService> implements EntregaPostalResourceService {

	@Delegate
	private EntregaPostalResourceService delegateService = null;

	@Override
	protected void setDelegateService(EntregaPostalResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.PagadorCieResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa PagadorCieResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class PagadorCieResourceServiceEjb extends AbstractServiceEjb<PagadorCieResourceService> implements PagadorCieResourceService {

	@Delegate
	private PagadorCieResourceService delegateService = null;

	@Override
	protected void setDelegateService(PagadorCieResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

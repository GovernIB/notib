package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.PagadorPostalResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa PagadorPostalResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class PagadorPostalResourceServiceEjb extends AbstractServiceEjb<PagadorPostalResourceService> implements PagadorPostalResourceService {

	@Delegate
	private PagadorPostalResourceService delegateService = null;

	@Override
	protected void setDelegateService(PagadorPostalResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

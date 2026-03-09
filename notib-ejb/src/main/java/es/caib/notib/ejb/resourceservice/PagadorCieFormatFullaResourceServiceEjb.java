package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.PagadorCieFormatFullaResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa PagadorCieFormatFullaResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class PagadorCieFormatFullaResourceServiceEjb extends AbstractServiceEjb<PagadorCieFormatFullaResourceService> implements PagadorCieFormatFullaResourceService {

	@Delegate
	private PagadorCieFormatFullaResourceService delegateService = null;

	@Override
	protected void setDelegateService(PagadorCieFormatFullaResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

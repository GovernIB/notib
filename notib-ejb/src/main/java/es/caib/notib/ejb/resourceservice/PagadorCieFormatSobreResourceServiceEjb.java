package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.PagadorCieFormatSobreResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa PagadorCieFormatSobreResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class PagadorCieFormatSobreResourceServiceEjb extends AbstractServiceEjb<PagadorCieFormatSobreResourceService> implements PagadorCieFormatSobreResourceService {

	@Delegate
	private PagadorCieFormatSobreResourceService delegateService = null;

	@Override
	protected void setDelegateService(PagadorCieFormatSobreResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

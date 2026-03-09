package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.DocumentResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa DocumentResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class DocumentResourceServiceEjb extends AbstractServiceEjb<DocumentResourceService> implements DocumentResourceService {

	@Delegate
	private DocumentResourceService delegateService = null;

	@Override
	protected void setDelegateService(DocumentResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

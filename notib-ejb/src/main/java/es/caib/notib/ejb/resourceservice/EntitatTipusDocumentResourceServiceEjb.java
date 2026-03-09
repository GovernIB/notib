package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.EntitatTipusDocumentResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa EntitatTipusDocumentResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class EntitatTipusDocumentResourceServiceEjb extends AbstractServiceEjb<EntitatTipusDocumentResourceService> implements EntitatTipusDocumentResourceService {

	@Delegate
	private EntitatTipusDocumentResourceService delegateService = null;

	@Override
	protected void setDelegateService(EntitatTipusDocumentResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

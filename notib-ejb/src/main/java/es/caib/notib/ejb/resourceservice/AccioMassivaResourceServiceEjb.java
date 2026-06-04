package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.AccioMassivaResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa AccioMassivaResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class AccioMassivaResourceServiceEjb extends AbstractServiceEjb<AccioMassivaResourceService> implements AccioMassivaResourceService {

	@Delegate
	private AccioMassivaResourceService delegateService = null;

	@Override
	protected void setDelegateService(AccioMassivaResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

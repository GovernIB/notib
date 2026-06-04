package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.AccioMassivaElementResourceService;
import es.caib.notib.logic.intf.resourceservice.AccioMassivaResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa AccioMassivaElementResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class AccioMassivaElementResourceServiceEjb extends AbstractServiceEjb<AccioMassivaElementResourceService> implements AccioMassivaElementResourceService {

	@Delegate
	private AccioMassivaElementResourceService delegateService = null;

	@Override
	protected void setDelegateService(AccioMassivaElementResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

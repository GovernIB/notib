package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.UsuariResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa UsuariResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class UsuariResourceServiceEjb extends AbstractServiceEjb<UsuariResourceService> implements UsuariResourceService {

	@Delegate
	private UsuariResourceService delegateService = null;

	@Override
	protected void setDelegateService(UsuariResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

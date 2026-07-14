package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.ActiveMqResourceService;
import es.caib.notib.logic.intf.resourceservice.UsuariPermisResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa ActiveMqResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class UsuariPermisResourceServiceEjb extends AbstractServiceEjb<UsuariPermisResourceService> implements UsuariPermisResourceService {

	@Delegate
	private UsuariPermisResourceService delegateService = null;

	@Override
	protected void setDelegateService(UsuariPermisResourceService delegateService) {
		this.delegateService = delegateService;
	}
}

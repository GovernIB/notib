package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.PersonaResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa PersonaResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class PersonaResourceServiceEjb extends AbstractServiceEjb<PersonaResourceService> implements PersonaResourceService {

	@Delegate
	private PersonaResourceService delegateService = null;

	@Override
	protected void setDelegateService(PersonaResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

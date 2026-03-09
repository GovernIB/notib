package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.GrupResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa GrupResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class GrupResourceServiceEjb extends AbstractServiceEjb<GrupResourceService> implements GrupResourceService {

	@Delegate
	private GrupResourceService delegateService = null;

	@Override
	protected void setDelegateService(GrupResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

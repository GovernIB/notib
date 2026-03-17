package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.CacheResourceService;
import es.caib.notib.logic.intf.resourceservice.Dir3ResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa CacheResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class CacheResourceServiceEjb extends AbstractServiceEjb<CacheResourceService> implements CacheResourceService {

	@Delegate
	private CacheResourceService delegateService = null;

	@Override
	protected void setDelegateService(CacheResourceService delegateService) {
		this.delegateService = delegateService;
	}
}

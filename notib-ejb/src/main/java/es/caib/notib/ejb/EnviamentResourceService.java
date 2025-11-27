package es.caib.notib.ejb;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
@RolesAllowed("**")
public class EnviamentResourceService
		extends AbstractServiceEjb<es.caib.notib.logic.intf.resourceservice.EnviamentResourceService>
		implements es.caib.notib.logic.intf.resourceservice.EnviamentResourceService {

	@Delegate
	private es.caib.notib.logic.intf.resourceservice.EnviamentResourceService delegateService;

	protected void setDelegateService(es.caib.notib.logic.intf.resourceservice.EnviamentResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

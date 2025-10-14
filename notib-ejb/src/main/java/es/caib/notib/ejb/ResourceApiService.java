package es.caib.notib.ejb;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

@Stateless
public class ResourceApiService
		extends AbstractServiceEjb<es.caib.notib.logic.intf.base.service.ResourceApiService>
		implements es.caib.notib.logic.intf.base.service.ResourceApiService {

	@Delegate
	private es.caib.notib.logic.intf.base.service.ResourceApiService delegateService;

	@Override
	protected void setDelegateService(es.caib.notib.logic.intf.base.service.ResourceApiService delegateService) {
		this.delegateService = delegateService;
	}

}

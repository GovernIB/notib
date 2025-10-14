package es.caib.notib.ejb;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

@Stateless
public class PermissionEvaluatorService
		extends AbstractServiceEjb<es.caib.notib.logic.intf.base.service.PermissionEvaluatorService>
		implements es.caib.notib.logic.intf.base.service.PermissionEvaluatorService {

	@Delegate
	private es.caib.notib.logic.intf.base.service.PermissionEvaluatorService delegateService;

	@Override
	protected void setDelegateService(es.caib.notib.logic.intf.base.service.PermissionEvaluatorService delegateService) {
		this.delegateService = delegateService;
	}

}

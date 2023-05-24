package es.caib.notib.ejb;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;

/**
 * Implementació de CallbackService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.

 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Stateless
public class CallbackService extends AbstractService<es.caib.notib.logic.intf.service.CallbackService> implements es.caib.notib.logic.intf.service.CallbackService {
	
	@Override
	@PermitAll
	public void processarPendents() {
		getDelegateService().processarPendents();
	}

	@Override
	@PermitAll
	public boolean reintentarCallback(Long notId) {
		return false;
	}

	@Override
	@PermitAll
	public boolean findByNotificacio(Long notId) {
		return false;
	}

}

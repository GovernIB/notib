package es.caib.notib.ejb;

import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementació de CallbackService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.

 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Stateless
public class CallbackService extends AbstractService<es.caib.notib.logic.intf.service.CallbackService> implements es.caib.notib.logic.intf.service.CallbackService {
	
	@Autowired
	CallbackService delegate;

	@Override
	public void processarPendents() {
		delegate.processarPendents();
	}

}

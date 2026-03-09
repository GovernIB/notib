package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.Dir3ResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa Dir3ResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class Dir3ResourceServiceEjb extends AbstractServiceEjb<Dir3ResourceService> implements Dir3ResourceService {

	@Delegate
	private Dir3ResourceService delegateService = null;

	@Override
	protected void setDelegateService(Dir3ResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

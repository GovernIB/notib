package es.caib.notib.ejb.resourceservice;

import es.caib.notib.ejb.config.AbstractServiceEjb;
import es.caib.notib.logic.intf.resourceservice.AvisResourceService;
import es.caib.notib.logic.intf.resourceservice.ExcepcioLogResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa AvisResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Limit Tecnologies
 */
@Stateless
public class ExcepcioLogResourceServiceEjb extends AbstractServiceEjb<ExcepcioLogResourceService> implements ExcepcioLogResourceService {

	@Delegate
	private ExcepcioLogResourceService delegateService = null;

	@Override
	protected void setDelegateService(ExcepcioLogResourceService delegateService) {
		this.delegateService = delegateService;
	}

}

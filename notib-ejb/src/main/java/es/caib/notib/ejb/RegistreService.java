package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.RegistreAnotacioDto;
import es.caib.notib.logic.intf.statemachine.dto.ConsultaSirDto;
import es.caib.notib.logic.intf.statemachine.events.EnviamentRegistreRequest;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * Implementació de RegistreService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class RegistreService extends AbstractService<es.caib.notib.logic.intf.service.RegistreService> implements es.caib.notib.logic.intf.service.RegistreService {

	@Override
	@RolesAllowed("**")
	public void registrarSortida(RegistreAnotacioDto registreAnotacio) {
		getDelegateService().registrarSortida(registreAnotacio);
	}

	@Override
	@RolesAllowed("**")
	public void enviarRegistre(EnviamentRegistreRequest enviamentRegistreRequest) {
		getDelegateService().enviarRegistre(enviamentRegistreRequest);
	}

	@Override
	public void consultaSir(ConsultaSirDto enviament) {
		getDelegateService().consultaSir(enviament);
	}

}

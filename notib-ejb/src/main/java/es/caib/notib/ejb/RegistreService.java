package es.caib.notib.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.notib.logic.intf.dto.RegistreAnotacioDto;

/**
 * Implementació de RegistreService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class RegistreService extends AbstractService<es.caib.notib.logic.intf.service.RegistreService> implements es.caib.notib.logic.intf.service.RegistreService {

	@Autowired
	RegistreService delegate;

	@Override
	@RolesAllowed({"NOT_SUPER", "tothom", "NOT_ADMIN", "NOT_APL"})
	public void registrarSortida(RegistreAnotacioDto registreAnotacio) {
		delegate.registrarSortida(registreAnotacio);
	}
	
}

package es.caib.notib.core.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.notib.core.api.dto.RegistreAnotacioDto;
import es.caib.notib.core.api.service.RegistreService;

/**
 * Implementació de RegistreService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class RegistreServiceBean implements RegistreService {

	@Autowired
	RegistreService delegate;

	@Override
	@RolesAllowed({"NOT_SUPER", "NOT_USER", "NOT_ADMIN", "NOT_APL"})
	public void registrarSortida(RegistreAnotacioDto registreAnotacio) {
		delegate.registrarSortida(registreAnotacio);
	}
	
}

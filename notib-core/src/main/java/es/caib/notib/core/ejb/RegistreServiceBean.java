package es.caib.notib.core.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.notib.core.api.dto.RegistreAnotacioDto;
import es.caib.notib.core.api.service.RegistreService;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class RegistreServiceBean implements RegistreService{

	@Autowired
	RegistreService delegate;

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})
	public void registrarSortida(RegistreAnotacioDto registreAnotacio) {
		delegate.registrarSortida(registreAnotacio);
	}
	
}

/**
 * 
 */
package es.caib.notib.core.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.notib.core.api.dto.AplicacioDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.UsuariAplicacioService;

/**
 * Implementació de EntitatService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class UsuariAplicacioServiceBean implements UsuariAplicacioService {

	@Autowired
	UsuariAplicacioService delegate;

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public AplicacioDto create(
			AplicacioDto aplicacio) {
		return delegate.create(aplicacio);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public AplicacioDto update(
			AplicacioDto aplicacio) throws NotFoundException {
		return delegate.update(aplicacio);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public AplicacioDto delete(
			Long id) throws NotFoundException {
		return delegate.delete(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public AplicacioDto findById(
			Long aplicacioId) {
		return delegate.findById(aplicacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public AplicacioDto findByUsuariCodi(
			String usuariCodi) {
		return delegate.findByUsuariCodi(usuariCodi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public PaginaDto<AplicacioDto> findPaginat(
			PaginacioParamsDto paginacioParams) {
		return delegate.findPaginat(paginacioParams);
	}

}

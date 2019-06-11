package es.caib.notib.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.notib.core.api.dto.PagadorCieFormatFullaDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.PagadorCieFormatFullaService;

/**
 * Implementació de PagadorCieFormatFullaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class PagadorCieFormatFullaServiceBean implements PagadorCieFormatFullaService  {

	@Autowired
	PagadorCieFormatFullaService delegate;
	
	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public PagadorCieFormatFullaDto create(Long pagadorCieId, PagadorCieFormatFullaDto formatSobre) {
		return delegate.create(
				pagadorCieId, 
				formatSobre);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public PagadorCieFormatFullaDto update(PagadorCieFormatFullaDto formatSobre) throws NotFoundException {
		return delegate.update(formatSobre);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public PagadorCieFormatFullaDto delete(Long id) throws NotFoundException {
		return delegate.delete(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER", "NOT_APL"})
	public PagadorCieFormatFullaDto findById(Long id) {
		return delegate.findById(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER", "NOT_APL"})
	public List<PagadorCieFormatFullaDto> findAll() {
		return delegate.findAll();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER", "NOT_APL"})
	public PaginaDto<PagadorCieFormatFullaDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		return delegate.findAllPaginat(paginacioParams);
	}

}

package es.caib.notib.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.notib.core.api.dto.cie.CieFormatSobreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.PagadorCieFormatSobreService;

/**
 * Implementació de PagadorCieFormatSobreService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class PagadorCieFormatSobreServiceBean implements PagadorCieFormatSobreService {

	@Autowired
	PagadorCieFormatSobreService delegate;
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public CieFormatSobreDto create(Long entitatId, CieFormatSobreDto formatSobre) {
		return delegate.create(
				entitatId, 
				formatSobre);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public CieFormatSobreDto update(CieFormatSobreDto formatSobre) throws NotFoundException {
		return delegate.update(formatSobre);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public CieFormatSobreDto delete(Long id) throws NotFoundException {
		return delegate.delete(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public CieFormatSobreDto findById(Long id) {
		return delegate.findById(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public List<CieFormatSobreDto> findAll() {
		return delegate.findAll();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public List<CieFormatSobreDto> findFormatSobreByPagadorCie(Long pagadorCieId) {
		return delegate.findFormatSobreByPagadorCie(pagadorCieId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public PaginaDto<CieFormatSobreDto> findAllPaginat(
			Long pagadorCieId, 
			PaginacioParamsDto paginacioParams) {
		return delegate.findAllPaginat(
				pagadorCieId,
				paginacioParams);
	}

}

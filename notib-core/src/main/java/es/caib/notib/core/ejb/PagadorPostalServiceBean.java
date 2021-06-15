/**
 * 
 */
package es.caib.notib.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.PagadorPostalDto;
import es.caib.notib.core.api.dto.PagadorPostalFiltreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.PagadorPostalService;

/**
 * Implementació de PagadorPostalService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class PagadorPostalServiceBean implements PagadorPostalService {

	@Autowired
	PagadorPostalService delegate;

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public PagadorPostalDto create(
			Long entitatId, 
			PagadorPostalDto postal) {
		return delegate.create(
				entitatId, 
				postal);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public PagadorPostalDto update(PagadorPostalDto postal) throws NotFoundException {
		return delegate.update(postal);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public PagadorPostalDto delete(Long id) throws NotFoundException {
		return delegate.delete(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public PagadorPostalDto findById(Long id) {
		return delegate.findById(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public PaginaDto<PagadorPostalDto> findAmbFiltrePaginat(
			Long entitatId, 
			PagadorPostalFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltrePaginat(
				entitatId, 
				filtre, 
				paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public List<PagadorPostalDto> findAll() {
		return delegate.findAll();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public PaginaDto<PagadorPostalDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		return delegate.findAllPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public List<PagadorPostalDto> findByEntitat(Long entitatId) {
		return delegate.findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public Object findByEntitatAndOrganGestor(EntitatDto entitat, OrganGestorDto organGestor) {
		return delegate.findByEntitatAndOrganGestor(entitat, organGestor);
	}

}

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
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.EntitatService;

/**
 * Implementació de EntitatService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class EntitatServiceBean implements EntitatService {

	@Autowired
	EntitatService delegate;

	@Override
	@RolesAllowed("NOT_ADMIN")
	public EntitatDto create(EntitatDto entitat) {
		return delegate.create(entitat);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public EntitatDto update(
			EntitatDto entitat) {
		return delegate.update(entitat);
	}

	@Override
	@RolesAllowed("NOT_ADMIN")
	public EntitatDto updateActiva(
			Long id,
			boolean activa) {
		return delegate.updateActiva(id, activa);
	}

	@Override
	@RolesAllowed("NOT_ADMIN")
	public EntitatDto delete(
			Long id) {
		return delegate.delete(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public EntitatDto findById(Long id) {
		return delegate.findById(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public EntitatDto findByCodi(String codi) {
		return delegate.findByCodi(codi);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public EntitatDto findByDir3codi(String dir3Codi) {
		return delegate.findByDir3codi(dir3Codi);
	}

	@Override
	@RolesAllowed("NOT_ADMIN")
	public List<EntitatDto> findAll() {
		return delegate.findAll();
	}

	@Override
	@RolesAllowed("NOT_ADMIN")
	public PaginaDto<EntitatDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		return delegate.findAllPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed("NOT_ADMIN")
	public List<EntitatDto> findAccessiblesUsuariActual() {
		return delegate.findAccessiblesUsuariActual();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public List<PermisDto> permisFindByEntitatId(Long id) throws NotFoundException {
		return delegate.permisFindByEntitatId(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public void permisUpdate(Long entitatId, PermisDto permis) throws NotFoundException {
		delegate.permisUpdate(entitatId, permis);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public void permisDelete(Long entitatId, Long permisId) throws NotFoundException {
		delegate.permisDelete(entitatId, permisId);
	}

}

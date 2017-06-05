/**
 * 
 */
package es.caib.notib.core.ejb;

import java.util.List;
import java.util.Map;

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
	@RolesAllowed("NOT_REP")
	public List<EntitatDto> findByEntitatId(Long entitatId) {
		return delegate.findByEntitatId(entitatId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public List<PermisDto> findPermis(Long id) throws NotFoundException {
		return delegate.findPermis(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public void updatePermis(Long id, PermisDto permis) throws NotFoundException {
		delegate.updatePermis(id, permis);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public void deletePermis(Long id, Long permisId) throws NotFoundException {
		delegate.deletePermis(id, permisId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP", "NOT_APL"})
	public Map<Long, List<PermisDto>> findPermisos(List<Long> entitatIds) {
		return delegate.findPermisos(entitatIds);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public EntitatDto findByCif(String cif) {
		return delegate.findByCif(cif);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public EntitatDto findByDir3(String dir3) {
		return delegate.findByDir3(dir3);
	}

}

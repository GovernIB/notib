package es.caib.notib.core.ejb;

import es.caib.notib.core.api.dto.AvisDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.service.AvisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;

/**
 * Implementació de AvisService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class AvisServiceBean implements AvisService {

	@Autowired
	AvisService delegate;

	@Override
	@RolesAllowed("NOT_SUPER")
	public AvisDto create(AvisDto avis) {
		return delegate.create(avis);
	}

	@Override
	@RolesAllowed("NOT_SUPER")
	public AvisDto update(AvisDto avis) {
		return delegate.update(avis);
	}

	@Override
	@RolesAllowed("NOT_SUPER")
	public AvisDto updateActiva(Long id, boolean activa) {
		return delegate.updateActiva(id, activa);
	}

	@Override
	@RolesAllowed("NOT_SUPER")
	public AvisDto delete(Long id) {
		return delegate.delete(id);
	}

	@Override
	@RolesAllowed("NOT_SUPER")
	public AvisDto findById(Long id) {
		return delegate.findById(id);
	}

	@Override
	@RolesAllowed("NOT_SUPER")
	public PaginaDto<AvisDto> findPaginat(PaginacioParamsDto paginacioParams) {
		return delegate.findPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed({"tothom", "NOT_SUPER"})
	public List<AvisDto> findActive() {
		return delegate.findActive();
	}

}

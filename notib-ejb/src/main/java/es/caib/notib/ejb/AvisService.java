package es.caib.notib.ejb;

import es.caib.notib.ejb.AbstractService;
import es.caib.notib.logic.intf.dto.AvisDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de AvisService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class AvisService extends AbstractService<es.caib.notib.logic.intf.service.AvisService> implements es.caib.notib.logic.intf.service.AvisService {

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

	@Override
	@RolesAllowed("tothom")
	public List<AvisDto> findActiveAdmin(Long entitatId) {
		return delegate.findActiveAdmin(entitatId);
	}

}

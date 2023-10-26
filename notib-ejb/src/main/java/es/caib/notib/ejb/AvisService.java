package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.AvisDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de AvisService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class AvisService extends AbstractService<es.caib.notib.logic.intf.service.AvisService> implements es.caib.notib.logic.intf.service.AvisService {

	@Override
	@RolesAllowed("NOT_SUPER")
	public AvisDto create(AvisDto avis) {
		return getDelegateService().create(avis);
	}

	@Override
	@RolesAllowed("NOT_SUPER")
	public AvisDto update(AvisDto avis) {
		return getDelegateService().update(avis);
	}

	@Override
	@RolesAllowed("NOT_SUPER")
	public AvisDto updateActiva(Long id, boolean activa) {
		return getDelegateService().updateActiva(id, activa);
	}

	@Override
	@RolesAllowed("NOT_SUPER")
	public AvisDto delete(Long id) {
		return getDelegateService().delete(id);
	}

	@Override
	@RolesAllowed("NOT_SUPER")
	public AvisDto findById(Long id) {
		return getDelegateService().findById(id);
	}

	@Override
	@RolesAllowed("NOT_SUPER")
	public PaginaDto<AvisDto> findPaginat(PaginacioParamsDto paginacioParams) {
		return getDelegateService().findPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<AvisDto> findActive() {
		return getDelegateService().findActive();
	}

	@Override
	@RolesAllowed("**")
	public List<AvisDto> findActiveAdmin(Long entitatId) {
		return getDelegateService().findActiveAdmin(entitatId);
	}

}

/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.IdentificadorTextDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.cie.CieDataDto;
import es.caib.notib.logic.intf.dto.cie.CieDto;
import es.caib.notib.logic.intf.dto.cie.CieFiltreDto;
import es.caib.notib.logic.intf.dto.cie.CieTableItemDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de PagadorCieService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class PagadorCieService extends AbstractService<es.caib.notib.logic.intf.service.PagadorCieService> implements es.caib.notib.logic.intf.service.PagadorCieService {

	@Override
	@RolesAllowed("**")
	public CieDto upsert(Long entitatId, CieDataDto cie) {
		return getDelegateService().upsert(entitatId, cie);
	}

	@Override
	@RolesAllowed("**")
	public CieDto delete(Long id) throws NotFoundException {
		return getDelegateService().delete(id);
	}

	@Override
	@RolesAllowed("**")
	public CieDto findById(Long id) {
		return getDelegateService().findById(id);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<CieTableItemDto> findAmbFiltrePaginat(
			Long entitatId,
			CieFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return getDelegateService().findAmbFiltrePaginat(
				entitatId,
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<CieDto> findAll() {
		return getDelegateService().findAll();
	}

	@Override
	@RolesAllowed("**")
	public List<IdentificadorTextDto> findAllIdentificadorText() {
		return getDelegateService().findAllIdentificadorText();
	}

	@Override
	@PermitAll
	public List<IdentificadorTextDto> findPagadorsByEntitat(EntitatDto entitat) {
		return getDelegateService().findPagadorsByEntitat(entitat);
	}

	@Override
	@RolesAllowed("**")
	public List<IdentificadorTextDto> findNoCaducatsByEntitat(EntitatDto entitat) {
		return getDelegateService().findNoCaducatsByEntitat(entitat);
	}

	@Override
	@RolesAllowed("**")
	public List<IdentificadorTextDto> findNoCaducatsByEntitatAndOrgan(EntitatDto entitat, String organCodi, boolean isAdminOrgan) {
		return getDelegateService().findNoCaducatsByEntitatAndOrgan(entitat, organCodi, isAdminOrgan);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<CieDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		return getDelegateService().findAllPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<CieDto> findByEntitat(Long entitatId) {
		return getDelegateService().findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public Object findByEntitatAndOrganGestor(EntitatDto entitat, OrganGestorDto organGestor) {
		return getDelegateService().findByEntitatAndOrganGestor(entitat, organGestor);
	}
}

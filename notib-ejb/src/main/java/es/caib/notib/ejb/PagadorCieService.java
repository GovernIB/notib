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
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de PagadorCieService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class PagadorCieService extends AbstractService<es.caib.notib.logic.intf.service.PagadorCieService> implements es.caib.notib.logic.intf.service.PagadorCieService {

	@Autowired
	PagadorCieService delegate;

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public CieDto create(Long entitatId, CieDataDto cie) {
		return delegate.create(entitatId, cie);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public CieDto update(CieDataDto cie) throws NotFoundException {
		return delegate.update(cie);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public CieDto delete(Long id) throws NotFoundException {
		return delegate.delete(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public CieDto findById(Long id) {
		return delegate.findById(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public PaginaDto<CieTableItemDto> findAmbFiltrePaginat(
			Long entitatId, 
			CieFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltrePaginat(
				entitatId, 
				filtre, 
				paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public List<CieDto> findAll() {
		return delegate.findAll();
	}
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public List<IdentificadorTextDto> findAllIdentificadorText() {
		return delegate.findAllIdentificadorText();
	}
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public PaginaDto<CieDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		return delegate.findAllPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public List<CieDto> findByEntitat(Long entitatId) {
		return delegate.findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public Object findByEntitatAndOrganGestor(EntitatDto entitat, OrganGestorDto organGestor) {
		return delegate.findByEntitatAndOrganGestor(entitat, organGestor);
	}
}

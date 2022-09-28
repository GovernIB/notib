/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.IdentificadorTextDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalDataDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalFiltreDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalTableItemDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de PagadorPostalService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class OperadorPostalService extends AbstractService<es.caib.notib.logic.intf.service.OperadorPostalService> implements es.caib.notib.logic.intf.service.OperadorPostalService {

	@Autowired
	OperadorPostalService delegate;

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public OperadorPostalDto create(
			Long entitatId,
			OperadorPostalDataDto postal) {
		return delegate.create(
				entitatId, 
				postal);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public OperadorPostalDto update(OperadorPostalDataDto postal) throws NotFoundException {
		return delegate.update(postal);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public OperadorPostalDto delete(Long id) throws NotFoundException {
		return delegate.delete(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public OperadorPostalDto findById(Long id) {
		return delegate.findById(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public PaginaDto<OperadorPostalTableItemDto> findAmbFiltrePaginat(
			Long entitatId, 
			OperadorPostalFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltrePaginat(
				entitatId, 
				filtre, 
				paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public List<OperadorPostalDto> findAll() {
		return delegate.findAll();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public List<IdentificadorTextDto> findAllIdentificadorText() {
		return delegate.findAllIdentificadorText();
	}
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public PaginaDto<OperadorPostalDto> findAllPaginat(PaginacioParamsDto paginacioParams) {
		return delegate.findAllPaginat(paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public List<OperadorPostalDto> findByEntitat(Long entitatId) {
		return delegate.findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public Object findByEntitatAndOrganGestor(EntitatDto entitat, OrganGestorDto organGestor) {
		return delegate.findByEntitatAndOrganGestor(entitat, organGestor);
	}

}

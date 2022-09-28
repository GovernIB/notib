package es.caib.notib.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.notib.logic.intf.dto.cie.CieFormatSobreDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.exception.NotFoundException;

/**
 * Implementació de PagadorCieFormatSobreService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class PagadorCieFormatSobreService extends AbstractService<es.caib.notib.logic.intf.service.PagadorCieFormatSobreService> implements es.caib.notib.logic.intf.service.PagadorCieFormatSobreService {

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

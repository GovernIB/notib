package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.cie.CieFormatSobreDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de PagadorCieFormatSobreService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class PagadorCieFormatSobreService extends AbstractService<es.caib.notib.logic.intf.service.PagadorCieFormatSobreService> implements es.caib.notib.logic.intf.service.PagadorCieFormatSobreService {

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public CieFormatSobreDto create(Long entitatId, CieFormatSobreDto formatSobre) {
		return getDelegateService().create(entitatId, formatSobre);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public CieFormatSobreDto update(CieFormatSobreDto formatSobre) throws NotFoundException {
		return getDelegateService().update(formatSobre);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "tothom"})
	public CieFormatSobreDto delete(Long id) throws NotFoundException {
		return getDelegateService().delete(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public CieFormatSobreDto findById(Long id) {
		return getDelegateService().findById(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public List<CieFormatSobreDto> findAll() {
		return getDelegateService().findAll();
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public List<CieFormatSobreDto> findFormatSobreByPagadorCie(Long pagadorCieId) {
		return getDelegateService().findFormatSobreByPagadorCie(pagadorCieId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
	public PaginaDto<CieFormatSobreDto> findAllPaginat(Long pagadorCieId, PaginacioParamsDto paginacioParams) {
		return getDelegateService().findAllPaginat(pagadorCieId, paginacioParams);
	}

}

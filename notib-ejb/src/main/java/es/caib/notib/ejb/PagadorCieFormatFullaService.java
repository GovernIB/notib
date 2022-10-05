package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.cie.CieFormatFullaDto;
import es.caib.notib.logic.intf.exception.NotFoundException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de PagadorCieFormatFullaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class PagadorCieFormatFullaService extends AbstractService<es.caib.notib.logic.intf.service.PagadorCieFormatFullaService> implements es.caib.notib.logic.intf.service.PagadorCieFormatFullaService {

    @Override
    @RolesAllowed({"NOT_ADMIN", "tothom",})
    public CieFormatFullaDto create(Long pagadorCieId, CieFormatFullaDto formatSobre) {
        return getDelegateService().create(
                pagadorCieId,
                formatSobre);
    }

    @Override
    @RolesAllowed({"NOT_ADMIN", "tothom",})
    public CieFormatFullaDto update(CieFormatFullaDto formatSobre) throws NotFoundException {
        return getDelegateService().update(formatSobre);
    }

    @Override
    @RolesAllowed({"NOT_ADMIN", "tothom",})
    public CieFormatFullaDto delete(Long id) throws NotFoundException {
        return getDelegateService().delete(id);
    }

    @Override
    @RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
    public CieFormatFullaDto findById(Long id) {
        return getDelegateService().findById(id);
    }

    @Override
    @RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
    public List<CieFormatFullaDto> findAll() {
        return getDelegateService().findAll();
    }

    @Override
    @RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
    public PaginaDto<CieFormatFullaDto> findAllPaginat(
            Long pagadorCieId,
            PaginacioParamsDto paginacioParams) {
        return getDelegateService().findAllPaginat(
                pagadorCieId,
                paginacioParams);
    }

    @Override
    @RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
    public List<CieFormatFullaDto> findFormatFullaByPagadorCie(Long pagadorCieId) {
        return getDelegateService().findFormatFullaByPagadorCie(pagadorCieId);
    }
}

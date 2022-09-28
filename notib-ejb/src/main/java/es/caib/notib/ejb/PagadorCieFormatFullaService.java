package es.caib.notib.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.notib.logic.intf.dto.cie.CieFormatFullaDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.exception.NotFoundException;

/**
 * Implementació de PagadorCieFormatFullaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class PagadorCieFormatFullaService extends AbstractService<es.caib.notib.logic.intf.service.PagadorCieFormatFullaService> implements es.caib.notib.logic.intf.service.PagadorCieFormatFullaService {

    @Autowired
    PagadorCieFormatFullaService delegate;

    @Override
    @RolesAllowed({"NOT_ADMIN", "tothom",})
    public CieFormatFullaDto create(Long pagadorCieId, CieFormatFullaDto formatSobre) {
        return delegate.create(
                pagadorCieId,
                formatSobre);
    }

    @Override
    @RolesAllowed({"NOT_ADMIN", "tothom",})
    public CieFormatFullaDto update(CieFormatFullaDto formatSobre) throws NotFoundException {
        return delegate.update(formatSobre);
    }

    @Override
    @RolesAllowed({"NOT_ADMIN", "tothom",})
    public CieFormatFullaDto delete(Long id) throws NotFoundException {
        return delegate.delete(id);
    }

    @Override
    @RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
    public CieFormatFullaDto findById(Long id) {
        return delegate.findById(id);
    }

    @Override
    @RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
    public List<CieFormatFullaDto> findAll() {
        return delegate.findAll();
    }

    @Override
    @RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
    public PaginaDto<CieFormatFullaDto> findAllPaginat(
            Long pagadorCieId,
            PaginacioParamsDto paginacioParams) {
        return delegate.findAllPaginat(
                pagadorCieId,
                paginacioParams);
    }

    @Override
    @RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_APL"})
    public List<CieFormatFullaDto> findFormatFullaByPagadorCie(Long pagadorCieId) {
        return delegate.findFormatFullaByPagadorCie(pagadorCieId);
    }
}

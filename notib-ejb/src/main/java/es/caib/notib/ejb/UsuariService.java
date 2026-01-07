package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.intf.dto.permis.PermisosUsuari;
import es.caib.notib.logic.intf.dto.permis.PermisosUsuarisFiltre;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Primary
@Stateless
public class UsuariService extends AbstractService<es.caib.notib.logic.intf.service.UsuariService> implements es.caib.notib.logic.intf.service.UsuariService {

    @RolesAllowed({"NOT_SUPER"})
    @Override
    public UsuariDto findByCodi(String codi) {
        return getDelegateService().findByCodi(codi);
    }

    @Override
    @RolesAllowed({"NOT_SUPER"})
    @TransactionTimeout(value = 1200)
    public Long updateUsuariCodi(String codiAntic, String codiNou) {
        return getDelegateService().updateUsuariCodi(codiAntic, codiNou);
    }

    @Override
    @RolesAllowed({"NOT_ADMIN"})
    public PaginaDto<UsuariDto> findAmbFiltre(PermisosUsuarisFiltre filtre, PaginacioParamsDto paginacioParams) {
        return getDelegateService().findAmbFiltre(filtre, paginacioParams);
    }

    @Override
    @RolesAllowed({"NOT_ADMIN"})
    public PermisosUsuari getPermisosUsuari(EntitatDto entitat, String usuariCodi) {
        return getDelegateService().getPermisosUsuari(entitat, usuariCodi);
    }
}

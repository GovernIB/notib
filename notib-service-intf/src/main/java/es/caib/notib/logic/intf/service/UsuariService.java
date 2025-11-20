package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.permis.PermisosUsuari;
import es.caib.notib.logic.intf.dto.permis.PermisosUsuarisFiltre;
import org.springframework.security.access.prepost.PreAuthorize;

public interface UsuariService {

    @PreAuthorize("hasRole('NOT_SUPER')")
    UsuariDto findByCodi(String codi);

    @PreAuthorize("hasRole('NOT_SUPER') OR hasRole('NOT_SUPER')")
    Long updateUsuariCodi(String codiAntic, String codiNou);

    @PreAuthorize("hasRole('NOT_ADMIN')")
    PaginaDto<UsuariDto> findAmbFiltre(PermisosUsuarisFiltre filtre, PaginacioParamsDto paginacioParams);

    @PreAuthorize("hasRole('NOT_ADMIN')")
    PermisosUsuari getPermisosUsuari(EntitatDto entitat, String usuariCodi, OrganGestorDto organAdmin);
}

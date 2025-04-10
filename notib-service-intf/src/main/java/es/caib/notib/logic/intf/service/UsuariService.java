package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.dto.UsuariDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface UsuariService {

    @PreAuthorize("hasRole('NOT_SUPER')")
    UsuariDto findByCodi(String codi);

    @PreAuthorize("hasRole('NOT_SUPER')")
    void updateUsuariCodi(String codiAntic, String codiNou);
}

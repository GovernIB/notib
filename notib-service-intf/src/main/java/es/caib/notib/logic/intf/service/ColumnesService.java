package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.dto.Taula;
import es.caib.notib.logic.intf.dto.notenviament.ColumnesDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ColumnesService {

    /**
     * Crea les columnes s'han de mostrar
     *
     * @param columnes Attribut amb les columnes a visualitzar.
     * @return columnes que s'han de visualitzar.
     */
    @PreAuthorize("isAuthenticated()")
    void columnesCreate(String codiUsuari, Long entitatId, ColumnesDto columnes);

    /**
     * Actualitza les columnes s'han de mostrar
     *
     * @param columnes Attribut amb les columnes a visualitzar.
     * @return columnes que s'han de visualitzar.
     */
    @PreAuthorize("isAuthenticated()")
    void columnesUpdate(Long entitatId, ColumnesDto columnes);

    /**
     * Obté les columnes visibles per un usuari i entitat
     * @return columnes que s'han de visualitzar.
     */
    @PreAuthorize("isAuthenticated()")
    ColumnesDto getColumnesUsuari(Long entitatId, String codiUsuari, Taula taula);
}

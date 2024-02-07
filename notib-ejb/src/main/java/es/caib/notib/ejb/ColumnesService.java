package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.Taula;
import es.caib.notib.logic.intf.dto.notenviament.ColumnesDto;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Primary
@Stateless
public class ColumnesService extends AbstractService<es.caib.notib.logic.intf.service.ColumnesService> implements es.caib.notib.logic.intf.service.ColumnesService {


    @Override
    @RolesAllowed("**")
    public void columnesCreate(String codiUsuari, Long entitatId, ColumnesDto columnes) {
        getDelegateService().columnesCreate(codiUsuari, entitatId, columnes);
    }

    @Override
    @RolesAllowed("**")
    public void columnesUpdate(Long entitatId, ColumnesDto columnes) {
        getDelegateService().columnesUpdate(entitatId, columnes);
    }

    @Override
    @RolesAllowed("**")
    public ColumnesDto getColumnesUsuari(Long entitatId, String codiUsuari, Taula taula) {
        return getDelegateService().getColumnesUsuari(entitatId, codiUsuari, taula);
    }
}

package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.Taula;
import es.caib.notib.logic.intf.dto.notenviament.ColumnesDto;
import es.caib.notib.logic.intf.dto.notificacio.ColumnesRemeses;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Primary
@Stateless
public class ColumnesService extends AbstractService<es.caib.notib.logic.intf.service.ColumnesService> implements es.caib.notib.logic.intf.service.ColumnesService {


    @Override
    @RolesAllowed("**")
    public void createColumnesRemeses(String codiUsuari, Long entitatId, ColumnesRemeses columnes) {
        getDelegateService().createColumnesRemeses(codiUsuari, entitatId, columnes);
    }

    @Override
    @RolesAllowed("**")
    public void updateColumnesRemeses(Long entitatId, ColumnesRemeses columnes) {
        getDelegateService().updateColumnesRemeses(entitatId, columnes);
    }

    @Override
    @RolesAllowed("**")
    public ColumnesRemeses getColumnesRemeses(Long entitatId, String codiUsuari) {
        return getDelegateService().getColumnesRemeses(entitatId, codiUsuari);
    }

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
    public ColumnesDto getColumnesUsuari(Long entitatId, String codiUsuari) {
        return getDelegateService().getColumnesUsuari(entitatId, codiUsuari);
    }
}

package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.AdviserResponseDto;
import es.caib.notib.logic.intf.dto.adviser.EnviamentAdviser;

import javax.ejb.Stateless;

@Stateless
public class AdviserService extends AbstractService<es.caib.notib.logic.intf.service.AdviserService> implements es.caib.notib.logic.intf.service.AdviserService {

    @Override
    public AdviserResponseDto sincronitzarEnviament(EnviamentAdviser env) {
        return getDelegateService().sincronitzarEnviament(env);
    }
}
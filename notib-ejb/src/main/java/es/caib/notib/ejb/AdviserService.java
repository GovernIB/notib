package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.adviser.EnviamentAdviser;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ejb.Stateless;

@Stateless
public class AdviserService extends AbstractService<es.caib.notib.logic.intf.service.AdviserService> implements es.caib.notib.logic.intf.service.AdviserService {

    @Override
    public void sincronitzarEnviament(EnviamentAdviser env) {
        getDelegateService().sincronitzarEnviament(env);
    }
}
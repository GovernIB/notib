package es.caib.notib.ejb;

import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.ResultadoSincronizarEnvio;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.SincronizarEnvio;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;

@Primary
@Stateless
public class AdviserService extends AbstractService<es.caib.notib.logic.intf.service.AdviserService> implements es.caib.notib.logic.intf.service.AdviserService {

    @PermitAll
    public ResultadoSincronizarEnvio sincronizarEnvio(SincronizarEnvio sincronizarEnvio) {
        return getDelegateService().sincronizarEnvio(sincronizarEnvio);
    }

}
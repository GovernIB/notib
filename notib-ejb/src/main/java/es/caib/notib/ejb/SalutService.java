package es.caib.notib.ejb;

import es.caib.comanda.model.server.monitoring.ContextInfo;
import es.caib.comanda.model.server.monitoring.IntegracioInfo;
import es.caib.comanda.model.server.monitoring.SalutInfo;
import es.caib.comanda.model.server.monitoring.SubsistemaInfo;
import org.springframework.boot.actuate.health.Health;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

@Primary
@Stateless
public class SalutService extends AbstractService<es.caib.notib.logic.intf.service.SalutService> implements es.caib.notib.logic.intf.service.SalutService {

    @Override
	@RolesAllowed({"NOT_COM"})
    public List<IntegracioInfo> getIntegracions() {
        return getDelegateService().getIntegracions();
    }

    @Override
	@RolesAllowed({"NOT_COM"})
    public List<SubsistemaInfo> getSubsistemes() {
        return getDelegateService().getSubsistemes();
    }

    @Override
	@RolesAllowed({"NOT_COM"})
    public List<ContextInfo> getContexts(String baseUrl) {
        return getDelegateService().getContexts(baseUrl);
    }

    @Override
	@PermitAll
    public SalutInfo checkSalut(String versio, Long latenciaHttpMs) {
        return getDelegateService().checkSalut(versio, latenciaHttpMs);
    }

    @Override
    public Health checkHealthIndicator() {
        return getDelegateService().checkHealthIndicator();
    }

}

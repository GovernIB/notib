package es.caib.notib.ejb;

import es.caib.comanda.ms.salut.model.*;
import org.springframework.boot.actuate.health.Health;
import org.springframework.context.annotation.Primary;

import javax.ejb.Stateless;
import java.util.List;

@Primary
@Stateless
public class SalutService extends AbstractService<es.caib.notib.logic.intf.service.SalutService> implements es.caib.notib.logic.intf.service.SalutService {

    @Override
    public List<IntegracioInfo> getIntegracions() {
        return getDelegateService().getIntegracions();
    }

    @Override
    public List<SubsistemaInfo> getSubsistemes() {
        return getDelegateService().getSubsistemes();
    }

    @Override
    public List<ContextInfo> getContexts(String baseUrl) {
        return getDelegateService().getContexts(baseUrl);
    }

    @Override
    public SalutInfo checkSalut(String versio, String performanceUrl) {
        return getDelegateService().checkSalut(versio, performanceUrl);
    }

    @Override
    public Health checkHealthIndicator() {
        return getDelegateService().checkHealthIndicator();
    }

}

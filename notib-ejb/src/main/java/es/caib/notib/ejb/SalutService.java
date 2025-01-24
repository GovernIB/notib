package es.caib.notib.ejb;

import es.caib.comanda.salut.model.AppInfo;
import es.caib.comanda.salut.model.IntegracioInfo;
import es.caib.comanda.salut.model.SalutInfo;
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
    public List<AppInfo> getSubsistemes() {
        return getDelegateService().getSubsistemes();
    }

    @Override
    public SalutInfo checkSalut(String versio, String performanceUrl) {
        return getDelegateService().checkSalut(versio, performanceUrl);
    }

}

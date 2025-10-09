package es.caib.notib.logic.intf.service;


import es.caib.comanda.ms.salut.model.ContextInfo;
import es.caib.comanda.ms.salut.model.IntegracioInfo;
import es.caib.comanda.ms.salut.model.SalutInfo;
import es.caib.comanda.ms.salut.model.SubsistemaInfo;
import org.springframework.boot.actuate.health.Health;

import java.util.List;

public interface SalutService {

    public List<IntegracioInfo> getIntegracions();
    public List<SubsistemaInfo> getSubsistemes();
    public List<ContextInfo> getContexts(String baseUrl);
    public SalutInfo checkSalut(String versio, String performanceUrl);
    Health checkHealthIndicator();

}

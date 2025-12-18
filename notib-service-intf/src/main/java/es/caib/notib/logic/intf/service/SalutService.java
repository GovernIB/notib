package es.caib.notib.logic.intf.service;


import es.caib.comanda.model.v1.salut.ContextInfo;
import es.caib.comanda.model.v1.salut.IntegracioInfo;
import es.caib.comanda.model.v1.salut.SalutInfo;
import es.caib.comanda.model.v1.salut.SubsistemaInfo;
import org.springframework.boot.actuate.health.Health;

import java.util.List;

public interface SalutService {

    public List<IntegracioInfo> getIntegracions();
    public List<SubsistemaInfo> getSubsistemes();
    public List<ContextInfo> getContexts(String baseUrl);
    public SalutInfo checkSalut(String versio, String performanceUrl);
    Health checkHealthIndicator();

}

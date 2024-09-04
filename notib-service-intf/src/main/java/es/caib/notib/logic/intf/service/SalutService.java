package es.caib.notib.logic.intf.service;

import es.caib.comanda.salut.model.AppInfo;
import es.caib.comanda.salut.model.EstatSalut;
import es.caib.comanda.salut.model.IntegracioInfo;
import es.caib.comanda.salut.model.SalutInfo;

import java.util.List;

public interface SalutService {

    public List<IntegracioInfo> getIntegracions();
    public List<AppInfo> getSubsistemes();
    public SalutInfo checkSalut(String versio, String performanceUrl);

    public EstatSalut executePerformanceTest();
}

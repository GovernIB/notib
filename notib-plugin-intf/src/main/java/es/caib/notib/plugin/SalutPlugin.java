package es.caib.notib.plugin;

import es.caib.comanda.model.server.monitoring.EstatSalut;
import es.caib.comanda.model.server.monitoring.IntegracioPeticions;
import io.micrometer.core.instrument.MeterRegistry;

public interface SalutPlugin {

    void init(MeterRegistry registry, String codiPlugin, String codiEntitat);
    boolean teConfiguracioEspecifica();

    EstatSalut getEstatPlugin();
    IntegracioPeticions getPeticionsPlugin();

}

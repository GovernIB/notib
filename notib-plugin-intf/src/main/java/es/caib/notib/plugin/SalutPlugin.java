package es.caib.notib.plugin;

import es.caib.comanda.salut.model.EstatSalut;
import es.caib.comanda.salut.model.IntegracioPeticions;

public interface SalutPlugin {

    boolean teConfiguracioEspecifica();

    EstatSalut getEstatPlugin();
    IntegracioPeticions getPeticionsPlugin();

}

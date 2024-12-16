package es.caib.notib.plugin.valsig;

import es.caib.comanda.salut.model.EstatSalut;
import es.caib.comanda.salut.model.EstatSalutEnum;
import es.caib.comanda.salut.model.IntegracioPeticions;
import es.caib.notib.plugin.validatesignature.ValidateSignaturePlugin;
import lombok.Synchronized;

import java.time.Duration;
import java.time.Instant;
import java.util.Properties;

public class AfirmaCxfValidateSignaturePlugin extends org.fundaciobit.plugins.validatesignature.afirmacxf.AfirmaCxfValidateSignaturePlugin implements ValidateSignaturePlugin {

    public AfirmaCxfValidateSignaturePlugin(String propertyKeyBase, Properties properties, boolean configuracioEspecifica) {
        super(propertyKeyBase, properties);
        this.configuracioEspecifica = configuracioEspecifica;
    }

    // Mètodes de SALUT
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private boolean configuracioEspecifica = false;
    private int operacionsOk = 0;
    private int operacionsError = 0;

    @Synchronized
    private void incrementarOperacioOk() {
        operacionsOk++;
    }

    @Synchronized
    private void incrementarOperacioError() {
        operacionsError++;
    }

    @Synchronized
    private void resetComptadors() {
        operacionsOk = 0;
        operacionsError = 0;
    }

    @Override
    public boolean teConfiguracioEspecifica() {
        return this.configuracioEspecifica;
    }

    @Override
    public EstatSalut getEstatPlugin() {
        try {
            Instant start = Instant.now();
            consultaUsuaris(getLdapFiltreCodi(), "fakeUser");
            return EstatSalut.builder()
                    .latencia((int) Duration.between(start, Instant.now()).toMillis())
                    .estat(EstatSalutEnum.UP)
                    .build();
        } catch (Exception ex) {
            return EstatSalut.builder().estat(EstatSalutEnum.DOWN).build();
        }
    }

    @Override
    public IntegracioPeticions getPeticionsPlugin() {
        IntegracioPeticions integracioPeticions = IntegracioPeticions.builder()
                .totalOk(operacionsOk)
                .totalError(operacionsError)
                .build();
        resetComptadors();
        return integracioPeticions;
    }

}

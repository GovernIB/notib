package es.caib.notib.plugin.valsig;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.notib.plugin.AbstractSalutPlugin;
import es.caib.notib.plugin.validatesignature.ValidateSignaturePlugin;
import io.micrometer.core.instrument.MeterRegistry;
import org.fundaciobit.plugins.validatesignature.api.ValidateSignatureRequest;
import org.fundaciobit.plugins.validatesignature.api.ValidateSignatureResponse;

import java.util.Properties;

public class AfirmaCxfValidateSignaturePlugin extends org.fundaciobit.plugins.validatesignature.afirmacxf.AfirmaCxfValidateSignaturePlugin implements ValidateSignaturePlugin {

    public AfirmaCxfValidateSignaturePlugin(String propertyKeyBase, Properties properties, boolean configuracioEspecifica) {
        super(propertyKeyBase, properties);
        salutPluginComponent.setConfiguracioEspecifica(configuracioEspecifica);
        salutPluginComponent.setUrlPlugin(properties.getProperty("es.caib.notib.plugins.validatesignature.afirmacxf.endpoint"));

    }

    @Override
    public ValidateSignatureResponse validateSignature(ValidateSignatureRequest validationRequest) throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            var result = super.validateSignature(validationRequest);
            salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - startTime);
            return result;
        } catch (Exception ex) {
            if ("El valor de la signatura és null.".equals(ex.getMessage()) ||
                    (ex.getMessage() != null && ex.getMessage().startsWith("Informació de l'error no disponible")) ||
                    // Document no firmat
                    ex.getMessage().contains("El formato de la firma no es valido(urn:oasis:names:tc:dss:1.0:resultmajor:RequesterError)") ||
                    ex.getMessage().contains("El formato de la firma no es válido(urn:oasis:names:tc:dss:1.0:resultmajor:RequesterError)") ||
                    ex.getMessage().contains("El documento OOXML no está firmado(urn:oasis:names:tc:dss:1.0:resultmajor:ResponderError)")) {
                salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - startTime);
            } else {
                salutPluginComponent.incrementarOperacioError();
            }
            throw ex;
        }
    }

    // Mètodes de SALUT
    // /////////////////////////////////////////////////////////////////////////////////////////////
    private AbstractSalutPlugin salutPluginComponent = new AbstractSalutPlugin();
    public void init(MeterRegistry registry, String codiPlugin, String codiEntiat) {
        salutPluginComponent.init(registry, codiPlugin, codiEntiat);

    }

    @Override
    public boolean teConfiguracioEspecifica() {
        return salutPluginComponent.teConfiguracioEspecifica();
    }

    @Override
    public EstatSalut getEstatPlugin() {
        return salutPluginComponent.getEstatPlugin();
    }

    @Override
    public IntegracioPeticions getPeticionsPlugin() {
        return salutPluginComponent.getPeticionsPlugin();
    }

}

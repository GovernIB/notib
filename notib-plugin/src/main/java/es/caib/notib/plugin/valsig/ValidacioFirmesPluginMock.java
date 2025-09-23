package es.caib.notib.plugin.valsig;


import com.google.common.base.Strings;
import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.notib.plugin.AbstractSalutPlugin;
import es.caib.notib.plugin.validatesignature.api.IValidateSignaturePlugin;
import es.caib.notib.plugin.validatesignature.api.SignatureRequestedInformation;
import es.caib.notib.plugin.validatesignature.api.ValidateSignatureRequest;
import es.caib.notib.plugin.validatesignature.api.ValidateSignatureResponse;
import es.caib.notib.plugin.validatesignature.api.ValidationStatus;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.Properties;
import java.util.Random;

public class ValidacioFirmesPluginMock extends AbstractSalutPlugin implements IValidateSignaturePlugin {

    private Properties properties;
    private String propertyKeyBase;
    private boolean configuracioEspcifica;

    public ValidacioFirmesPluginMock(String propertyKeyBase, Properties properties) {

        super();
        this.propertyKeyBase = propertyKeyBase;
        this.properties = properties;
    }

    public ValidacioFirmesPluginMock(String propertyKeyBase, Properties properties, boolean configuracioEspecifica) {

        this.configuracioEspecifica = configuracioEspecifica;
        salutPluginComponent.setCodiEntitat(codiEntitat);
//        var entitat = "";
//        if (configuracioEspecifica && !Strings.isNullOrEmpty(codiEntitat)) {
//            entitat = codiEntitat;
//        }
        salutPluginComponent.setUrlPlugin(properties.getProperty("es.caib.notib.plugins.validatesignature.afirmacxf.endpoint"));

    }

    public ValidacioFirmesPluginMock(Properties properties) {

        super();
        this.properties = properties;
    }
    public ValidacioFirmesPluginMock() {
        super();
    }

    @Override
    public String filter(ValidateSignatureRequest validateSignatureRequest) {
        return null;
    }

    @Override
    public SignatureRequestedInformation getSupportedSignatureRequestedInformation() {
        return new SignatureRequestedInformation();
    }

    @Override
    public SignatureRequestedInformation getSupportedSignatureRequestedInformationBySignatureType(String s) {
        return new SignatureRequestedInformation();
    }

    @Override
    public ValidateSignatureResponse validateSignature(ValidateSignatureRequest validateSignatureRequest) throws Exception {

        ValidateSignatureResponse validateSignatureResponse = new ValidateSignatureResponse();
        ValidationStatus validationStatus = new ValidationStatus();
        validationStatus.setStatus(ValidationStatus.SIGNATURE_VALID); // Validacio VALID
//        validationStatus.setStatus(ValidationStatus.SIGNATURE_INVALID); // Validacio INVALID
        validateSignatureResponse.setValidationStatus(validationStatus);
        salutPluginComponent.incrementarOperacioOk(new Random().nextLong());
        return validateSignatureResponse;
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

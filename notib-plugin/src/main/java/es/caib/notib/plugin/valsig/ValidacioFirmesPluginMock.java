package es.caib.notib.plugin.valsig;


import com.google.common.base.Strings;
import es.caib.notib.plugin.AbstractSalutPlugin;
import es.caib.notib.plugin.validatesignature.api.IValidateSignaturePlugin;
import es.caib.notib.plugin.validatesignature.api.SignatureRequestedInformation;
import es.caib.notib.plugin.validatesignature.api.ValidateSignatureRequest;
import es.caib.notib.plugin.validatesignature.api.ValidateSignatureResponse;
import es.caib.notib.plugin.validatesignature.api.ValidationStatus;

import java.util.Properties;

public class ValidacioFirmesPluginMock extends AbstractSalutPlugin implements IValidateSignaturePlugin {

    private Properties properties;
    private String propertyKeyBase;
    private boolean configuracioEspcifica;

    public ValidacioFirmesPluginMock(String propertyKeyBase, Properties properties) {

        super();
        this.propertyKeyBase = propertyKeyBase;
        this.properties = properties;
    }

    public ValidacioFirmesPluginMock(String propertyKeyBase, Properties properties, boolean configuracioEspcifica, String codiEntitat) {

        super();
        this.propertyKeyBase = propertyKeyBase;
        this.properties = properties;
        this.configuracioEspcifica = configuracioEspcifica;
        this.codiEntitat = codiEntitat;
        var entitat = "";
        if (configuracioEspecifica && !Strings.isNullOrEmpty(codiEntitat)) {
            entitat = codiEntitat;
        }
        urlPlugin = properties.getProperty("es.caib.notib.plugins.validatesignature.afirmacxf.endpoint");
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
        return validateSignatureResponse;
    }

}

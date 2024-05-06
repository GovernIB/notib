package es.caib.notib.plugin.valsig;



import es.caib.notib.plugin.validatesignature.api.IValidateSignaturePlugin;
import es.caib.notib.plugin.validatesignature.api.SignatureRequestedInformation;
import es.caib.notib.plugin.validatesignature.api.ValidateSignatureRequest;
import es.caib.notib.plugin.validatesignature.api.ValidateSignatureResponse;
import es.caib.notib.plugin.validatesignature.api.ValidationStatus;

import java.util.Properties;

public class ValidacioFirmesPluginMock implements IValidateSignaturePlugin {

    private Properties properties;
    private String propertyKeyBase;

    public ValidacioFirmesPluginMock(String propertyKeyBase, Properties properties) {
        super();
        this.propertyKeyBase = propertyKeyBase;
        this.properties = properties;
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

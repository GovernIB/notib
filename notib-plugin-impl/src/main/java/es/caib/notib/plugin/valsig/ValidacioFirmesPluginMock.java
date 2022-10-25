package es.caib.notib.plugin.valsig;

import org.fundaciobit.plugins.validatesignature.api.IValidateSignaturePlugin;
import org.fundaciobit.plugins.validatesignature.api.SignatureRequestedInformation;
import org.fundaciobit.plugins.validatesignature.api.ValidateSignatureRequest;
import org.fundaciobit.plugins.validatesignature.api.ValidateSignatureResponse;
import org.fundaciobit.plugins.validatesignature.api.ValidationStatus;

public class ValidacioFirmesPluginMock implements IValidateSignaturePlugin {

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
//        validationStatus.setStatus(ValidationStatus.SIGNATURE_VALID); // Validacio VALID
        validationStatus.setStatus(ValidationStatus.SIGNATURE_INVALID); // Validacio INVALID
        validateSignatureResponse.setValidationStatus(validationStatus);
        return validateSignatureResponse;
    }
}

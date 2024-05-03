package es.caib.notib.plugin.validatesignature.api;

import es.caib.notib.plugin.certificate.InformacioCertificat;
import org.fundaciobit.plugins.validatesignature.api.ValidationStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @author anadal
 *
 */
public interface IValidateSignaturePlugin extends org.fundaciobit.plugins.validatesignature.api.IValidateSignaturePlugin {
  
  
  public static final String VALIDATE_SIGNATURE_BASE_PROPERTY = IPLUGIN_BASE_PROPERTIES + "validatesignature.";
  
  /**
   * 
   * @param validationRequest
   * @return null si tot ha anat bé. Sinó el missatge de l'error.
   */
  public String filter(ValidateSignatureRequest validationRequest);

  @Override
  default String filter(org.fundaciobit.plugins.validatesignature.api.ValidateSignatureRequest validateSignatureRequest) {
    return filter(toNtValidateSignatureRequest(validateSignatureRequest));
  }

  private static ValidateSignatureRequest toNtValidateSignatureRequest(org.fundaciobit.plugins.validatesignature.api.ValidateSignatureRequest validateSignatureRequest) {
    ValidateSignatureRequest validationRequest = new ValidateSignatureRequest();
    validationRequest.setSignatureData(validateSignatureRequest.getSignatureData());
    validationRequest.setSignedDocumentData(validateSignatureRequest.getSignedDocumentData());
    validationRequest.setLanguage(validateSignatureRequest.getLanguage());
    SignatureRequestedInformation signatureRequestedInformation = null;
    if (validateSignatureRequest.getSignatureRequestedInformation() != null) {
      signatureRequestedInformation = new SignatureRequestedInformation();
      signatureRequestedInformation.setReturnSignatureTypeFormatProfile(validateSignatureRequest.getSignatureRequestedInformation().getReturnSignatureTypeFormatProfile());
      signatureRequestedInformation.setValidateCertificateRevocation(validateSignatureRequest.getSignatureRequestedInformation().getValidateCertificateRevocation());
      signatureRequestedInformation.setReturnCertificateInfo(validateSignatureRequest.getSignatureRequestedInformation().getReturnCertificateInfo());
      signatureRequestedInformation.setReturnValidationChecks(validateSignatureRequest.getSignatureRequestedInformation().getReturnValidationChecks());
      signatureRequestedInformation.setReturnCertificates(validateSignatureRequest.getSignatureRequestedInformation().getReturnCertificates());
      signatureRequestedInformation.setReturnTimeStampInfo(validateSignatureRequest.getSignatureRequestedInformation().getReturnTimeStampInfo());
    }
    validationRequest.setSignatureRequestedInformation(signatureRequestedInformation);
    return validationRequest;
  }

  /**
   * El valors que retorni null, significa que per alguns tipus de firma retorna
   * la informació i per altres tipus no.
   * @see IValidateSignaturePlugin#getSupportedSignatureRequestedInformationBySignatureType(String signType)
   * @return
   */
  public SignatureRequestedInformation getSupportedSignatureRequestedInformation();
  
  
  public SignatureRequestedInformation getSupportedSignatureRequestedInformationBySignatureType(String signType);
  
  
  public ValidateSignatureResponse validateSignature(ValidateSignatureRequest validationRequest) throws Exception;

  @Override
  default org.fundaciobit.plugins.validatesignature.api.ValidateSignatureResponse validateSignature(org.fundaciobit.plugins.validatesignature.api.ValidateSignatureRequest validateSignatureRequest) throws Exception {
    ValidateSignatureResponse validateSignatureResponse = validateSignature(toNtValidateSignatureRequest(validateSignatureRequest));
    return toFbValidateSignatureResponse(validateSignatureResponse);
  }

  private static org.fundaciobit.plugins.validatesignature.api.ValidateSignatureResponse toFbValidateSignatureResponse(ValidateSignatureResponse validateSignatureResponse) {
    org.fundaciobit.plugins.validatesignature.api.ValidateSignatureResponse validateResponse = new org.fundaciobit.plugins.validatesignature.api.ValidateSignatureResponse();
    validateResponse.setSignType(validateSignatureResponse.getSignType());
    validateResponse.setSignFormat(validateSignatureResponse.getSignFormat());
    validateResponse.setSignProfile(validateSignatureResponse.getSignProfile());
    ValidationStatus validationStatus = null;
    if (validateSignatureResponse.getValidationStatus() != null) {
      validationStatus = new ValidationStatus();
      validationStatus.setStatus(validateSignatureResponse.getValidationStatus().getStatus());
      validationStatus.setErrorMsg(validateSignatureResponse.getValidationStatus().getErrorMsg());
      validationStatus.setErrorException(validateSignatureResponse.getValidationStatus().getErrorException());
    }
    validateResponse.setValidationStatus(validationStatus);
    if (validateSignatureResponse.getSignatureDetailInfo() != null) {
      validateResponse.setSignatureDetailInfo(Arrays.stream(validateSignatureResponse.getSignatureDetailInfo())
              .map(IValidateSignaturePlugin::toFbSignatureDetailInfo)
              .toArray(org.fundaciobit.plugins.validatesignature.api.SignatureDetailInfo[]::new));
    }

    return validateResponse;
  }

  private static org.fundaciobit.plugins.validatesignature.api.SignatureDetailInfo toFbSignatureDetailInfo(SignatureDetailInfo signatureDetailInfo) {
    org.fundaciobit.plugins.validatesignature.api.SignatureDetailInfo signatureInfo = null;
    if (signatureDetailInfo != null) {
      signatureInfo = new org.fundaciobit.plugins.validatesignature.api.SignatureDetailInfo();
      signatureInfo.setAlgorithm(signatureDetailInfo.getAlgorithm());
      signatureInfo.setDigestValue(signatureDetailInfo.getDigestValue());
      signatureInfo.setSignDate(signatureDetailInfo.getSignDate());
      signatureInfo.setValidChecks(toFbSignatureCheck(signatureDetailInfo.getValidChecks()));
      signatureInfo.setInvalidChecks(toFbSignatureCheck(signatureDetailInfo.getInvalidChecks()));
      signatureInfo.setIndeterminateChecks(toFbSignatureCheck(signatureDetailInfo.getIndeterminateChecks()));
      signatureInfo.setPolicyIdentifier(signatureDetailInfo.getPolicyIdentifier());
      signatureInfo.setCertificateInfo(toFbInformacioCertificat(signatureDetailInfo.getCertificateInfo()));
      signatureInfo.setCertificateChain(signatureDetailInfo.getCertificateChain());
      signatureInfo.setTimeStampInfo(toFbTimeStampInfo(signatureDetailInfo.getTimeStampInfo()));
    }
      return signatureInfo;
  }

  private static List<org.fundaciobit.plugins.validatesignature.api.SignatureCheck> toFbSignatureCheck(List<SignatureCheck> signatureChecks) {
    if (signatureChecks == null)
      return null;

    return signatureChecks.stream()
              .map(sc -> new org.fundaciobit.plugins.validatesignature.api.SignatureCheck(sc.getName(), sc.getType()))
              .collect(Collectors.toList());
  }

  private static org.fundaciobit.pluginsib.validatecertificate.InformacioCertificat toFbInformacioCertificat(InformacioCertificat informacioCertificat) {
    if (informacioCertificat == null)
      return null;

    org.fundaciobit.pluginsib.validatecertificate.InformacioCertificat infoCertificat = new org.fundaciobit.pluginsib.validatecertificate.InformacioCertificat();
    infoCertificat.setLlinatgesResponsable(informacioCertificat.getLlinatgesResponsable());
    infoCertificat.setIdEuropeu(informacioCertificat.getIdEuropeu());
    infoCertificat.setEntitatSubscriptoraNom(informacioCertificat.getEntitatSubscriptoraNom());
    infoCertificat.setEntitatSubscriptoraNif(informacioCertificat.getEntitatSubscriptoraNif());
    infoCertificat.setClassificacioEidas(informacioCertificat.getClassificacioEidas());
    infoCertificat.setCertificatQualificat(informacioCertificat.getCertificatQualificat());
    infoCertificat.setLlocDeFeina(informacioCertificat.getLlocDeFeina());
    infoCertificat.setCreatAmbUnDispositiuSegur(informacioCertificat.getCreatAmbUnDispositiuSegur());
    infoCertificat.setOrganitzacio(informacioCertificat.getOrganitzacio());
    infoCertificat.setCarrec(informacioCertificat.getCarrec());
    infoCertificat.setOiEuropeu(informacioCertificat.getOiEuropeu());
    infoCertificat.setAltresValors(informacioCertificat.getAltresValors());
    infoCertificat.setQcCompliance(informacioCertificat.getQcCompliance());
    infoCertificat.setQcSSCD(informacioCertificat.getQcSSCD());
    infoCertificat.setIdlogOn(informacioCertificat.getIdlogOn());
    infoCertificat.setPseudonim(informacioCertificat.getPseudonim());
    infoCertificat.setNumeroIdentificacionPersonal(informacioCertificat.getNumeroIdentificacionPersonal());
    return infoCertificat;
  }

  private static org.fundaciobit.plugins.validatesignature.api.TimeStampInfo toFbTimeStampInfo(TimeStampInfo timeStampInfo) {
    if (timeStampInfo == null)
      return null;

    org.fundaciobit.plugins.validatesignature.api.TimeStampInfo timeStamp = new org.fundaciobit.plugins.validatesignature.api.TimeStampInfo();
    timeStamp.setCreationTime(timeStampInfo.getCreationTime());
    timeStamp.setCertificateIssuer(timeStampInfo.getCertificateIssuer());
    timeStamp.setCertificateSubject(timeStampInfo.getCertificateSubject());
    timeStamp.setCertificate(timeStampInfo.getCertificate());
    timeStamp.setAlgorithm(timeStampInfo.getAlgorithm());
    return timeStamp;
  }
}

package es.caib.notib.plugin.validatesignature.api;

/**
 * 
 * @author anadal
 *
 */
public class ValidateSignatureResponse implements ValidateSignatureConstants {


  /** CADES, XADES, PADES, ... */
  private String signType;

  /** Attached-implicit, detached-explicit, enveloped, enveloping, ..., */
  private String signFormat;

  /** Perfil: BES, EPES, A, XL, .. */
  private String signProfile; //

  private ValidationStatus validationStatus = new ValidationStatus();

  private SignatureDetailInfo[] signatureDetailInfo = null;

  public ValidationStatus getValidationStatus() {
    return validationStatus;
  }

  public SignatureDetailInfo[] getSignatureDetailInfo() {
    return signatureDetailInfo;
  }

  public void setSignatureDetailInfo(SignatureDetailInfo[] signatureDetailInfo) {
    this.signatureDetailInfo = signatureDetailInfo;
  }

  public void setValidationStatus(ValidationStatus validationStatus) {
    this.validationStatus = validationStatus;
  }

  public String getSignType() {
    return signType;
  }

  public void setSignType(String signType) {
    this.signType = signType;
  }

  public String getSignFormat() {
    return signFormat;
  }

  public void setSignFormat(String signFormat) {
    this.signFormat = signFormat;
  }

  public String getSignProfile() {
    return signProfile;
  }

  public void setSignProfile(String signProfile) {
    this.signProfile = signProfile;
  }

}

package es.caib.notib.plugin.validatesignature.api;

/**
 * 
 * @author anadal
 *
 */
public class ValidateSignatureRequest {

  protected String language;

  protected byte[] signatureData;

  protected byte[] signedDocumentData;

  protected SignatureRequestedInformation signatureRequestedInformation;

  public ValidateSignatureRequest() {
  }



  public byte[] getSignatureData() {
    return signatureData;
  }

  public void setSignatureData(byte[] signatureData) {
    this.signatureData = signatureData;
  }

  public byte[] getSignedDocumentData() {
    return signedDocumentData;
  }

  public void setSignedDocumentData(byte[] signedDocumentData) {
    this.signedDocumentData = signedDocumentData;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }



  public SignatureRequestedInformation getSignatureRequestedInformation() {
    return signatureRequestedInformation;
  }



  public void setSignatureRequestedInformation(
      SignatureRequestedInformation signatureRequestedInformation) {
    this.signatureRequestedInformation = signatureRequestedInformation;
  }



}

package es.caib.notib.plugin.validatesignature.api;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import es.caib.notib.plugin.validatesignature.api.xml.ThrowableAdapter;

/**
 * 
 * @author anadal
 *
 */
public class ValidationStatus {


  public static final int SIGNATURE_VALID = 1;

  public static final int SIGNATURE_ERROR = -1;
  
  public static final int SIGNATURE_INVALID = -2;
  

  protected int status = SIGNATURE_ERROR;

  protected String errorMsg = null;

  protected Throwable errorException;
  
  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getErrorMsg() {
    return errorMsg;
  }

  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }
  
  @XmlJavaTypeAdapter(ThrowableAdapter.class)
  public Throwable getErrorException() {
    return errorException;
  }

  public void setErrorException(Throwable errorException) {
    this.errorException = errorException;
  }

  
}

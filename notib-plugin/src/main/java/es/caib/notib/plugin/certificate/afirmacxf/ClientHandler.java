package es.caib.notib.plugin.certificate.afirmacxf;

import es.caib.notib.plugin.certificate.afirmacxf.validarcertificadoapi.Validacion;

/**
 * 
 * @author anadal
 *
 */
public abstract class ClientHandler {

  

  public abstract void addSecureHeader(Validacion api);
  
}

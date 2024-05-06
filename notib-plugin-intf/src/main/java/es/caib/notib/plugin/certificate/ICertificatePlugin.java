package es.caib.notib.plugin.certificate;

import org.fundaciobit.pluginsib.core.IPlugin;

import java.security.cert.X509Certificate;

/**
 * Interface que mostrar els mètodes que hauria de tenir el 
 * plugin de verificació de certificats.
 * 
 * @author anadal
 *
 */
public interface ICertificatePlugin extends IPlugin {
  
  public static final String CERTIFICATE_BASE_PROPERTY = IPLUGIN_BASE_PROPERTIES + "certificate.";


  /**
   * Verifica un certificat.
   * @param certificat
   * @return null significa que tot esta bé. Cadena buida significa que no s'ha validat.
   *         Qualsevol altra cosa, conté el missatge de l'error.
   * @throws Exception Si es produeix algun error inesperat.
   */
  public String checkCertificate(X509Certificate certificat) throws Exception;
  
  /**
   * Valida i retorna informacio d'un certificat
   * @param certificat
   * @return
   * @throws Exception
   */
  public ResultatValidacio getInfoCertificate(X509Certificate certificat) throws Exception;
  
}

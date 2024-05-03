package es.caib.notib.plugin.certificate.afirmacxf;

import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.handler.WSHandlerConstants;
import es.caib.notib.plugin.certificate.afirmacxf.validarcertificadoapi.Validacion;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 
 * @author anadal
 * 
 */
public class ClientHandlerCertificate extends ClientHandler {

  /**
   * 
   */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 3883745902598584073L;

  private String keystoreLocation;
  private String keystoreType;
  private String keystorePassword;
  private String keystoreCertAlias;
  private String keystoreCertPassword;

  public ClientHandlerCertificate(String keystoreLocation, String keystoreType,
      String keystorePassword, String keystoreCertAlias, String keystoreCertPassword) {
    this.keystoreLocation = keystoreLocation;
    this.keystoreType = keystoreType;
    this.keystorePassword = keystorePassword;
    this.keystoreCertAlias = keystoreCertAlias;
    this.keystoreCertPassword = keystoreCertPassword;
  }

  @Override
  public void addSecureHeader(Validacion api) {

    org.apache.cxf.endpoint.Client client = ClientProxy.getClient(api);
    org.apache.cxf.endpoint.Endpoint cxfEndpoint = client.getEndpoint();

    Map<String, Object> outProps = new HashMap<String, Object>();


    outProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.SIGNATURE);

    outProps.put(WSHandlerConstants.USER, keystoreCertAlias);

    Properties cryptoProperties = new Properties();
    cryptoProperties.put("org.apache.ws.security.crypto.provider",
        "org.apache.ws.security.components.crypto.Merlin");
    cryptoProperties.put("org.apache.ws.security.crypto.merlin.keystore.password",
        keystorePassword);
    cryptoProperties.put("org.apache.ws.security.crypto.merlin.file", keystoreLocation);
    cryptoProperties.put("org.apache.ws.security.crypto.merlin.file", keystoreLocation);
    cryptoProperties.put("org.apache.ws.security.crypto.merlin.keystore.type", keystoreType); // "pkcs12"

    cryptoProperties.put("org.apache.ws.security.crypto.merlin.keystore.alias",
        keystoreCertAlias);

    outProps.put("cryptoProperties", cryptoProperties); // ?????

    outProps.put(WSHandlerConstants.SIG_PROP_REF_ID, "cryptoProperties");

    outProps.put(WSHandlerConstants.PW_CALLBACK_REF, new ClientCallbackSSL(
        keystoreCertPassword));

    outProps.put("signatureKeyIdentifier", "DirectReference");

    WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(outProps);

    cxfEndpoint.getOutInterceptors().add(wssOut);

  }

  public static class ClientCallbackSSL implements CallbackHandler {

    final String password;

    /**
     * @param password
     */
    public ClientCallbackSSL(String password) {
      super();
      this.password = password;
    }

    public void handle(Callback[] callbacks) throws IOException {
      for (int i = 0; i < callbacks.length; i++) {
        WSPasswordCallback pwcb = (WSPasswordCallback) callbacks[i];
        int usage = pwcb.getUsage();
        if (usage == WSPasswordCallback.DECRYPT || usage == WSPasswordCallback.SIGNATURE) {
          pwcb.setPassword(password);
        }
      }
    }
  }

}

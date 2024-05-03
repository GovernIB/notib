package es.caib.notib.plugin.certificate.afirmacxf;

import es.caib.notib.plugin.certificate.InformacioCertificat;
import es.caib.notib.plugin.certificate.ResultatValidacio;
import es.caib.notib.plugin.certificate.afirmacxf.validarcertificadoapi.InfoCertificadoInfo;
import es.caib.notib.plugin.certificate.afirmacxf.validarcertificadoapi.MensajeSalida;
import es.caib.notib.plugin.certificate.afirmacxf.validarcertificadoapi.ResultadoValidacionInfo;
import es.caib.notib.plugin.certificate.afirmacxf.validarcertificadoapi.ValidacionService;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.log4j.Logger;
import es.caib.notib.plugin.certificate.afirmacxf.validarcertificadoapi.Validacion;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.ws.BindingProvider;
import java.io.StringReader;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 
 * @author anadal
 * 
 */
@SuppressWarnings("restriction")
public class ValidaCertificat {

  public static final int MODE_VALIDACIO_SIMPLE = 0;
  public static final int MODE_VALIDACIO_AMB_REVOCACIO = 1;
  public static final int MODE_VALIDACIO_CADENA = 2;



  protected final Logger log = Logger.getLogger(getClass());

  private final String endPoint;
  private final String aplicacioId;

  private final int modeValidacio;
  
  private final boolean debug;

  private final ClientHandler clientHandler;

  private ValidaCertificat(String endPoint, String aplicacioId, int modeValidacio,
      ClientHandler clientHandler, boolean debug) {
    this.endPoint = endPoint;
    this.aplicacioId = aplicacioId;
    this.modeValidacio = modeValidacio;
    this.clientHandler = clientHandler;
    this.debug = debug;
  }

  public ValidaCertificat(String endPoint, String application_id, int modeValidacio,
      String username, String password, boolean debug) {
    this(endPoint, application_id, modeValidacio, new ClientHandlerUsernamePassword(username,
        password), debug);
  }

  public ValidaCertificat(String endPoint, String application_id, int modeValidacio,
      String keystoreLocation, String keystoreType, String keystorePassword,
      String keystoreCertAlias, String keystoreCertPassword, boolean debug) {

    this(endPoint, application_id, modeValidacio, new ClientHandlerCertificate(
        keystoreLocation, keystoreType, keystorePassword, keystoreCertAlias,
        keystoreCertPassword), debug);
  }

  public int getModeValidacio() {
    return modeValidacio;
  }

  public String getEndPoint() {
    return endPoint;
  }

  public String getAplicacioId() {
    return aplicacioId;
  }

  public ResultatValidacio validar(X509Certificate certificate, boolean obtenirDadesCertificat)
      throws Exception {
    return validar(certificate.getEncoded(), obtenirDadesCertificat);
  }

  private MensajeSalida getMensajeSalidaFromXml(String xml) throws Exception {
    JAXBContext context = JAXBContext.newInstance(MensajeSalida.class);
    Unmarshaller unMarshaller = context.createUnmarshaller();
    MensajeSalida ms = (MensajeSalida) unMarshaller.unmarshal(new StringReader(xml));
    return ms;
  }

  public ResultatValidacio validar(byte[] data, boolean obtenirDadesCertificat)
      throws Exception {

    String certificatBase64 = Base64.getEncoder().encodeToString(data);

    String respostaXml = cridarValidarCertificado(certificatBase64, obtenirDadesCertificat,
        modeValidacio);

    if (debug) {
      log.info(respostaXml);
    }

    MensajeSalida ms = getMensajeSalidaFromXml(respostaXml);

    
    MensajeSalida.Respuesta.Excepcion ex = ms.getRespuesta().getExcepcion();
    
    if (ex != null) {
      log.error("Exception = " + ex);
    }


    if (ex == null) {
      
      ResultadoValidacionInfo rvi;
      
      
      rvi = ms.getRespuesta().getResultadoProcesamiento().getResultadoValidacion();

      ResultatValidacio resultatValidacio = new ResultatValidacio();

      resultatValidacio.setResultatValidacioCodi(Integer.parseInt(rvi.getResultado()));
      resultatValidacio.setResultatValidacioDescripcio(rvi.getDescripcion());

      if (obtenirDadesCertificat) {
        InfoCertificadoInfo infoCert = ms.getRespuesta().getResultadoProcesamiento()
            .getInfoCertificado();
        resultatValidacio.setInformacioCertificat(getDadesCertificat(infoCert));
      }
      return resultatValidacio;

    } else {

      

      StringBuffer str = new StringBuffer();

      String codigoError = ex.getCodigoError();
      if (codigoError != null) {
        str.append("codigoError: " + codigoError);
      }

      String descripcionError = ex.getDescripcion();
      if (descripcionError != null) {
        if (str.length() != 0) {
          str.append("\n");
        }
        str.append("descripcionError: " + descripcionError);
      }

      String excepcionAsociada = ex.getExcepcionAsociada();
      if (excepcionAsociada != null) {
        if (str.length() != 0) {
          str.append("\n");
        }
        str.append("excepcionAsociada: " + excepcionAsociada);
      }

      throw new Exception(str.toString());
    }

  }

  @SuppressWarnings("unchecked")
  private InformacioCertificat getDadesCertificat(InfoCertificadoInfo infoCert) {
    if (infoCert == null) {
      return null;
    }

    List<InfoCertificadoInfo.Campo> camps = infoCert.getCampo();
    
    Map<String, Object> map = new HashMap<String, Object>();
    
    for (InfoCertificadoInfo.Campo campo : camps) {
      map.put(campo.getIdCampo(), campo.getValorCampo());
    }

    return InfoCertificatUtils.processInfoCertificate(map);
  }

 
  
  // Cache
  protected Validacion api = null;
  
  protected long lastConnection = 0;

  private String cridarValidarCertificado(String certificatBase64,
      boolean obtenirDadesCertificat, int modeValidacio) throws Exception {

    // Cada 10 minuts refem la comunicació
    long now = System.currentTimeMillis();
    if (lastConnection + 10 * 60 * 1000L < now) {
      lastConnection = now;
      api = null;
    }
    
    if (api == null) {

      ValidacionService service = new ValidacionService(new java.net.URL(getEndPoint() + "?wsdl"));
      api = service.getValidarCertificado();
      
      // @firma no suporta. Veure https://github.com/GovernIB/pluginsib/issues/3
      Client client =  ClientProxy.getClient(api); 
      {
          HTTPConduit conduit = (HTTPConduit) client.getConduit();
          HTTPClientPolicy policy = new HTTPClientPolicy();
          policy.setAllowChunking(false);
          conduit.setClient(policy);
      }        
      
    }

    Map<String, Object> reqContext = ((BindingProvider) api).getRequestContext();
    reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, getEndPoint());

    String xmlPeticio = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<mensajeEntrada xmlns=\"http://afirmaws/ws/validacion\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:SchemaLocation=\"https://localhost/afirmaws/xsd/mvalidacion/ws.xsd\">"
        + "<peticion>ValidarCertificado</peticion>" + "<versionMsg>1.0</versionMsg>"
        + "<parametros>" + "<certificado><![CDATA[" + certificatBase64 + "]]></certificado>"
        + "<idAplicacion>" + aplicacioId + "</idAplicacion>" + "<modoValidacion>"
        + modeValidacio + "</modoValidacion>" + "<obtenerInfo>" + obtenirDadesCertificat
        + "</obtenerInfo>" + "</parametros>" + "</mensajeEntrada>";
    log.debug(xmlPeticio);

    this.clientHandler.addSecureHeader(api);

    String xmlResposta = api.validarCertificado(xmlPeticio);
    log.debug(xmlResposta);
    return xmlResposta;

  }

}

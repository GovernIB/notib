package es.caib.notib.plugin.registre;

import es.caib.notib.plugin.PropertiesHelper;
import es.caib.notib.plugin.utils.SOAPLoggingHandler;
import es.caib.regweb3.ws.api.v3.*;
import org.apache.commons.io.IOUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

//import org.fundaciobit.genapp.common.utils.Utils;
/**
 * 
 * @author anadal
 * 
 */
public abstract class RegWeb3Utils {

	public static final String HELLO_WORLD = "RegWebHelloWorld";
	public static final String HELLO_WORLD_WITH_SECURITY = "RegWebHelloWorldWithSecurity";

	public static final String REGWEB3_PERSONAS = "RegWebPersonas";
	public static final String REGWEB3_REGISTRO_ENTRADA = "RegWebRegistroEntrada";
	public static final String REGWEB3_REGISTRO_SALIDA = "RegWebRegistroSalida";
	public static final String REGWEB3_INFO = "RegWebInfo";
	public static final String REGWEB3_ASIENTO_REGISTRAL = "RegWebAsientoRegistral";


	public static String getEndPoint(String api) {
		String url = PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.registre.url");
		if (!url.endsWith("/")) {
			url = url + "/";
		}
		return url + api;
	}

	public static String getAppUserName() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.registre.usuari");
	}

	public static String getAppPassword() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.plugin.registre.password");
	}

	public static void configAddressUserPassword(String usr, String pwd, String endpoint, Object api) {

		BindingProvider bp = (BindingProvider) api;
		Map<String, Object> reqContext = bp.getRequestContext();
		reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
		reqContext.put(BindingProvider.USERNAME_PROPERTY, usr);
		reqContext.put(BindingProvider.PASSWORD_PROPERTY, pwd);
		List<Handler> handlerChain = new ArrayList<>();
		handlerChain.add(new SOAPLoggingHandler());
		bp.getBinding().setHandlerChain(handlerChain);
	}

	public static RegWebHelloWorldWs getHelloWorldApi() throws Exception {

		final String endpoint = getEndPoint(HELLO_WORLD);
		final URL wsdl = new URL(endpoint + "?wsdl");


		RegWebHelloWorldWsService helloService = new RegWebHelloWorldWsService(wsdl);
		RegWebHelloWorldWs helloApi = helloService.getRegWebHelloWorldWs();

		// Adreça servidor
		Map<String, Object> reqContext = ((BindingProvider) helloApi).getRequestContext();
		reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

		return helloApi;

	}

	public static RegWebHelloWorldWithSecurityWs getHelloWorldWithSecurityApi() throws Exception {

		final String endpoint = getEndPoint(HELLO_WORLD_WITH_SECURITY);
		final URL wsdl = new URL(endpoint + "?wsdl");
		
		RegWebHelloWorldWithSecurityWsService service = new RegWebHelloWorldWithSecurityWsService(wsdl);
		RegWebHelloWorldWithSecurityWs api = service.getRegWebHelloWorldWithSecurityWs();

		configAddressUserPassword(getAppUserName(), getAppPassword(), endpoint, api);
		
		return api;
	}

	public static RegWebPersonasWs getPersonasApi() throws Exception  {
		
		final String endpoint = getEndPoint(REGWEB3_PERSONAS);
		final URL wsdl = new URL(endpoint + "?wsdl");
		
		RegWebPersonasWsService service = new RegWebPersonasWsService(wsdl);
		RegWebPersonasWs api = service.getRegWebPersonasWs();

		configAddressUserPassword(getAppUserName(), getAppPassword(), endpoint, api);
		
		return api;
	}

	public static RegWebRegistroEntradaWs getRegistroEntradaApi() throws Exception {
		final String endpoint = getEndPoint(REGWEB3_REGISTRO_ENTRADA);

		final URL wsdl = new URL(endpoint + "?wsdl");
		RegWebRegistroEntradaWsService service = new RegWebRegistroEntradaWsService(wsdl);

		RegWebRegistroEntradaWs api = service.getRegWebRegistroEntradaWs();

		configAddressUserPassword(getAppUserName(), getAppPassword(), endpoint, api);

		return api;
	}

	public static RegWebRegistroSalidaWs getRegistroSalidaApi() throws Exception {
		
		final String endpoint = getEndPoint(REGWEB3_REGISTRO_SALIDA);
		final URL wsdl = new URL(endpoint + "?wsdl");
		
		RegWebRegistroSalidaWsService service = new RegWebRegistroSalidaWsService(wsdl);
		RegWebRegistroSalidaWs api = service.getRegWebRegistroSalidaWs();

		configAddressUserPassword(getAppUserName(), getAppPassword(), endpoint, api);

		return api;
	}
	
	public static RegWebAsientoRegistralWs getAsientoRegistralApi() throws Exception {
		
		final String endpoint = getEndPoint(REGWEB3_ASIENTO_REGISTRAL);
		final URL wsdl = new URL(endpoint + "?wsdl");
		
		RegWebAsientoRegistralWsService service = new RegWebAsientoRegistralWsService(wsdl);
		RegWebAsientoRegistralWs api = service.getRegWebAsientoRegistralWs();

		configAddressUserPassword(getAppUserName(), getAppPassword(), endpoint, api);

		return api;
	}

	public static RegWebInfoWs getInfoApi() throws Exception  {
		
		final String endpoint = getEndPoint(REGWEB3_INFO);
		final URL wsdl = new URL(endpoint + "?wsdl");
		
		RegWebInfoWsService service = new RegWebInfoWsService(wsdl);
		RegWebInfoWs api = service.getRegWebInfoWs();

		configAddressUserPassword(getAppUserName(), getAppPassword(), endpoint, api);

		return api;
	}

	public static byte[] constructFitxerFromResource(String name) throws Exception  {
		String filename;
		if (name.startsWith("/")) {
			filename = name.substring(1);
		} else {
			filename = '/' + name; 
		}
		InputStream is = RegWeb3Utils.class.getResourceAsStream(filename);
		if (is == null) {
			return null;
		}
		try {
			return IOUtils.toByteArray(is);
		} finally {
			try {
				is.close();
			} catch (Exception e) {
			}
		}

	}
	
	protected Date toDate(XMLGregorianCalendar calendar) throws DatatypeConfigurationException {
		if (calendar == null) {
			return null;
		}
		return calendar.toGregorianCalendar().getTime();
	}
	
	protected XMLGregorianCalendar toXmlGregorianCalendar(Date date) throws DatatypeConfigurationException {
		if (date == null) {
			return null;
		}
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
	}

	//    protected List<AnexoWs> getAnexos() throws Exception {
	//
	//      List<AnexoWs> anexos = new ArrayList<AnexoWs>();
	//
	//      // Anexo sin firma
	//      {
	//        AnexoWs anexoSinFirma = new AnexoWs();
	//        final String fichero = "pdf_sin_firma.pdf";
	//        anexoSinFirma.setTitulo("Anexo Sin Firma");
	//        String copia = CODIGO_SICRES_BY_TIPOVALIDEZDOCUMENTO.get(TIPOVALIDEZDOCUMENTO_COPIA);
	//        anexoSinFirma.setValidezDocumento(copia);
	//        anexoSinFirma.setTipoDocumental(getTestAnexoTipoDocumental());
	//        String formulario = CODIGO_SICRES_BY_TIPO_DOCUMENTO.get(TIPO_DOCUMENTO_FORMULARIO);
	//        anexoSinFirma.setTipoDocumento(formulario);
	//        anexoSinFirma.setOrigenCiudadanoAdmin(ANEXO_ORIGEN_CIUDADANO.intValue());
	//        anexoSinFirma.setObservaciones("Observaciones de Marilen");
	//
	//        anexoSinFirma.setModoFirma(MODO_FIRMA_ANEXO_SINFIRMA); // == 0
	//        anexoSinFirma.setFechaCaptura(new Timestamp(new Date().getTime()));
	//
	//        // Fichero
	//        anexoSinFirma.setFicheroAnexado(RegWebTestUtils.constructFitxerFromResource(fichero));
	//        anexoSinFirma.setNombreFicheroAnexado(fichero);
	//        anexoSinFirma.setTipoMIMEFicheroAnexado(Utils.getMimeType(fichero));
	//
	//        anexos.add(anexoSinFirma);
	//      }
	//
	//      // Anexo con firma attached
	//
	//      {
	//        AnexoWs anexoConFirmaAttached = new AnexoWs();
	//
	//        final String fichero = "pdf_con_firma.pdf";
	//        anexoConFirmaAttached.setTitulo("Anexo Con Firma Attached");
	//        String copia = CODIGO_SICRES_BY_TIPOVALIDEZDOCUMENTO.get(TIPOVALIDEZDOCUMENTO_COPIA);
	//        anexoConFirmaAttached.setValidezDocumento(copia);
	//        anexoConFirmaAttached.setTipoDocumental(getTestAnexoTipoDocumental());
	//        String formulario = CODIGO_SICRES_BY_TIPO_DOCUMENTO.get(TIPO_DOCUMENTO_FORMULARIO);
	//        anexoConFirmaAttached.setTipoDocumento(formulario);
	//        anexoConFirmaAttached.setOrigenCiudadanoAdmin(ANEXO_ORIGEN_CIUDADANO.intValue());
	//        anexoConFirmaAttached.setObservaciones("Observaciones de Marilen");
	//
	//        anexoConFirmaAttached.setModoFirma(MODO_FIRMA_ANEXO_ATTACHED); // == 1
	//        anexoConFirmaAttached.setFechaCaptura(new Timestamp(new Date().getTime()));
	//
	//        // Fichero con firma
	//        anexoConFirmaAttached.setFicheroAnexado(RegWebTestUtils
	//            .constructFitxerFromResource(fichero));
	//        anexoConFirmaAttached.setNombreFicheroAnexado(fichero);
	//        anexoConFirmaAttached.setTipoMIMEFicheroAnexado(Utils.getMimeType(fichero));
	//
	//        anexos.add(anexoConFirmaAttached);
	//      }
	//
	//      // Anexo con firma detached
	//      {
	//        AnexoWs anexoConFirmaDetached = new AnexoWs();
	//
	//        anexoConFirmaDetached.setTitulo("Anexo Con Firma Detached");
	//        String copia = CODIGO_SICRES_BY_TIPOVALIDEZDOCUMENTO.get(TIPOVALIDEZDOCUMENTO_COPIA);
	//        anexoConFirmaDetached.setValidezDocumento(copia);
	//        anexoConFirmaDetached.setTipoDocumental(getTestAnexoTipoDocumental());
	//        String formulario = CODIGO_SICRES_BY_TIPO_DOCUMENTO.get(TIPO_DOCUMENTO_FORMULARIO);
	//        anexoConFirmaDetached.setTipoDocumento(formulario);
	//        anexoConFirmaDetached.setOrigenCiudadanoAdmin(ANEXO_ORIGEN_CIUDADANO.intValue());
	//        anexoConFirmaDetached.setObservaciones("Observaciones de Marilen");
	//
	//        anexoConFirmaDetached.setModoFirma(MODO_FIRMA_ANEXO_DETACHED); // == 2
	//        anexoConFirmaDetached.setFechaCaptura(new Timestamp(new Date().getTime()));
	//
	//        // Fichero
	//        final String fichero = "xades_doc.txt";
	//        anexoConFirmaDetached.setFicheroAnexado(RegWebTestUtils
	//            .constructFitxerFromResource(fichero));
	//        anexoConFirmaDetached.setNombreFicheroAnexado(fichero);
	//        anexoConFirmaDetached.setTipoMIMEFicheroAnexado(Utils.getMimeType(fichero));
	//
	//        // Firma
	//        final String firma = "xades_firma.xml";
	//        anexoConFirmaDetached
	//            .setFirmaAnexada(RegWebTestUtils.constructFitxerFromResource(firma));
	//        anexoConFirmaDetached.setNombreFirmaAnexada(firma);
	//        anexoConFirmaDetached.setTipoMIMEFirmaAnexada("application/xml");
	//
	//        anexos.add(anexoConFirmaDetached);
	//
	//      }
	//
	//      return anexos;
	//    }


}

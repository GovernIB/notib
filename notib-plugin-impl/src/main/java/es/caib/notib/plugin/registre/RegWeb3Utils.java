package es.caib.notib.plugin.registre;

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
import java.util.Properties;

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

	protected final Properties properties;

	protected RegWeb3Utils(Properties properties) {
		this.properties = properties;
	}

	protected String getEndPoint(String api) {
		String url = properties.getProperty("es.caib.notib.plugin.registre.url");
		if (!url.endsWith("/")) {
			url = url + "/";
		}
		return url + api;
	}

	protected String getAppUserName() {
		return properties.getProperty("es.caib.notib.plugin.registre.usuari");
	}

	protected String getAppPassword() {
		return properties.getProperty("es.caib.notib.plugin.registre.password");
	}

	protected void configAddressUserPassword(String usr, String pwd, String endpoint, Object api) {

		BindingProvider bp = (BindingProvider) api;
		Map<String, Object> reqContext = bp.getRequestContext();
		reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
		reqContext.put(BindingProvider.USERNAME_PROPERTY, usr);
		reqContext.put(BindingProvider.PASSWORD_PROPERTY, pwd);
		List<Handler> handlerChain = new ArrayList<>();
		handlerChain.add(new SOAPLoggingHandler());
		bp.getBinding().setHandlerChain(handlerChain);
	}

	protected RegWebHelloWorldWs getHelloWorldApi() throws Exception {

		final String endpoint = getEndPoint(HELLO_WORLD);
		final URL wsdl = new URL(endpoint + "?wsdl");


		RegWebHelloWorldWsService helloService = new RegWebHelloWorldWsService(wsdl);
		RegWebHelloWorldWs helloApi = helloService.getRegWebHelloWorldWs();

		// Adreça servidor
		Map<String, Object> reqContext = ((BindingProvider) helloApi).getRequestContext();
		reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

		return helloApi;

	}

	protected RegWebHelloWorldWithSecurityWs getHelloWorldWithSecurityApi() throws Exception {

		final String endpoint = getEndPoint(HELLO_WORLD_WITH_SECURITY);
		final URL wsdl = new URL(endpoint + "?wsdl");
		
		RegWebHelloWorldWithSecurityWsService service = new RegWebHelloWorldWithSecurityWsService(wsdl);
		RegWebHelloWorldWithSecurityWs api = service.getRegWebHelloWorldWithSecurityWs();

		configAddressUserPassword(getAppUserName(), getAppPassword(), endpoint, api);
		
		return api;
	}

	protected RegWebPersonasWs getPersonasApi() throws Exception  {
		
		final String endpoint = getEndPoint(REGWEB3_PERSONAS);
		final URL wsdl = new URL(endpoint + "?wsdl");
		
		RegWebPersonasWsService service = new RegWebPersonasWsService(wsdl);
		RegWebPersonasWs api = service.getRegWebPersonasWs();

		configAddressUserPassword(getAppUserName(), getAppPassword(), endpoint, api);
		
		return api;
	}

	protected RegWebRegistroEntradaWs getRegistroEntradaApi() throws Exception {
		final String endpoint = getEndPoint(REGWEB3_REGISTRO_ENTRADA);

		final URL wsdl = new URL(endpoint + "?wsdl");
		RegWebRegistroEntradaWsService service = new RegWebRegistroEntradaWsService(wsdl);

		RegWebRegistroEntradaWs api = service.getRegWebRegistroEntradaWs();

		configAddressUserPassword(getAppUserName(), getAppPassword(), endpoint, api);

		return api;
	}

	protected RegWebRegistroSalidaWs getRegistroSalidaApi() throws Exception {
		
		final String endpoint = getEndPoint(REGWEB3_REGISTRO_SALIDA);
		final URL wsdl = new URL(endpoint + "?wsdl");
		
		RegWebRegistroSalidaWsService service = new RegWebRegistroSalidaWsService(wsdl);
		RegWebRegistroSalidaWs api = service.getRegWebRegistroSalidaWs();

		configAddressUserPassword(getAppUserName(), getAppPassword(), endpoint, api);

		return api;
	}

	protected RegWebAsientoRegistralWs getAsientoRegistralApi() throws Exception {
		
		final String endpoint = getEndPoint(REGWEB3_ASIENTO_REGISTRAL);
		final URL wsdl = new URL(endpoint + "?wsdl");
		
		RegWebAsientoRegistralWsService service = new RegWebAsientoRegistralWsService(wsdl);
		RegWebAsientoRegistralWs api = service.getRegWebAsientoRegistralWs();

		configAddressUserPassword(getAppUserName(), getAppPassword(), endpoint, api);

		return api;
	}

	protected RegWebInfoWs getInfoApi() throws Exception  {
		
		final String endpoint = getEndPoint(REGWEB3_INFO);
		final URL wsdl = new URL(endpoint + "?wsdl");
		
		RegWebInfoWsService service = new RegWebInfoWsService(wsdl);
		RegWebInfoWs api = service.getRegWebInfoWs();

		configAddressUserPassword(getAppUserName(), getAppPassword(), endpoint, api);

		return api;
	}

	protected byte[] constructFitxerFromResource(String name) throws Exception  {
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

}

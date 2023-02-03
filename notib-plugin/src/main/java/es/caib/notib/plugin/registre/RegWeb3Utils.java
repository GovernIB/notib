package es.caib.notib.plugin.registre;

import es.caib.notib.plugin.utils.SOAPLoggingHandler;
import es.caib.regweb3.ws.api.v3.*;
import org.apache.commons.io.IOUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
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

		var url = properties.getProperty("es.caib.notib.plugin.registre.url");
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

		var bp = (BindingProvider) api;
		var reqContext = bp.getRequestContext();
		reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
		reqContext.put(BindingProvider.USERNAME_PROPERTY, usr);
		reqContext.put(BindingProvider.PASSWORD_PROPERTY, pwd);
		List<Handler> handlerChain = new ArrayList<>();
		handlerChain.add(new SOAPLoggingHandler());
		bp.getBinding().setHandlerChain(handlerChain);
	}

	protected RegWebHelloWorldWs getHelloWorldApi() throws Exception {

		final var endpoint = getEndPoint(HELLO_WORLD);
		final var wsdl = new URL(endpoint + "?wsdl");
		var helloService = new RegWebHelloWorldWsService(wsdl);
		var helloApi = helloService.getRegWebHelloWorldWs();
		// Adreça servidor
		var reqContext = ((BindingProvider) helloApi).getRequestContext();
		reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
		return helloApi;
	}

	protected RegWebHelloWorldWithSecurityWs getHelloWorldWithSecurityApi() throws Exception {

		final var endpoint = getEndPoint(HELLO_WORLD_WITH_SECURITY);
		final var wsdl = new URL(endpoint + "?wsdl");
		var service = new RegWebHelloWorldWithSecurityWsService(wsdl);
		var api = service.getRegWebHelloWorldWithSecurityWs();
		configAddressUserPassword(getAppUserName(), getAppPassword(), endpoint, api);
		return api;
	}

	protected RegWebPersonasWs getPersonasApi() throws Exception  {
		
		final String endpoint = getEndPoint(REGWEB3_PERSONAS);
		final URL wsdl = new URL(endpoint + "?wsdl");
		var service = new RegWebPersonasWsService(wsdl);
		var api = service.getRegWebPersonasWs();
		configAddressUserPassword(getAppUserName(), getAppPassword(), endpoint, api);
		return api;
	}

	protected RegWebRegistroEntradaWs getRegistroEntradaApi() throws Exception {

		final var endpoint = getEndPoint(REGWEB3_REGISTRO_ENTRADA);
		final var wsdl = new URL(endpoint + "?wsdl");
		var service = new RegWebRegistroEntradaWsService(wsdl);
		var api = service.getRegWebRegistroEntradaWs();
		configAddressUserPassword(getAppUserName(), getAppPassword(), endpoint, api);
		return api;
	}

	protected RegWebRegistroSalidaWs getRegistroSalidaApi() throws Exception {
		
		final String endpoint = getEndPoint(REGWEB3_REGISTRO_SALIDA);
		final URL wsdl = new URL(endpoint + "?wsdl");
		var service = new RegWebRegistroSalidaWsService(wsdl);
		var api = service.getRegWebRegistroSalidaWs();
		configAddressUserPassword(getAppUserName(), getAppPassword(), endpoint, api);
		return api;
	}

	protected RegWebAsientoRegistralWs getAsientoRegistralApi() throws Exception {
		
		final var endpoint = getEndPoint(REGWEB3_ASIENTO_REGISTRAL);
		final var wsdl = new URL(endpoint + "?wsdl");
		var service = new RegWebAsientoRegistralWsService(wsdl);
		var api = service.getRegWebAsientoRegistralWs();
		configAddressUserPassword(getAppUserName(), getAppPassword(), endpoint, api);
		return api;
	}

	protected RegWebInfoWs getInfoApi() throws Exception  {
		
		final var endpoint = getEndPoint(REGWEB3_INFO);
		final var wsdl = new URL(endpoint + "?wsdl");
		var service = new RegWebInfoWsService(wsdl);
		var api = service.getRegWebInfoWs();
		configAddressUserPassword(getAppUserName(), getAppPassword(), endpoint, api);
		return api;
	}

	protected byte[] constructFitxerFromResource(String name) throws Exception  {

		var filename = name.startsWith("/") ? name.substring(1) : '/' + name;
		var is = RegWeb3Utils.class.getResourceAsStream(filename);
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
		return calendar != null ? calendar.toGregorianCalendar().getTime() : null;
	}
	
	protected XMLGregorianCalendar toXmlGregorianCalendar(Date date) throws DatatypeConfigurationException {

		if (date == null) {
			return null;
		}
		var gc = new GregorianCalendar();
		gc.setTime(date);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
	}

}

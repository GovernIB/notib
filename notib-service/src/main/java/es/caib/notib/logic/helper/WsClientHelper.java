/**
 * 
 */
package es.caib.notib.logic.helper;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.CreateException;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;
import javax.xml.namespace.QName;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilitat per a instanciar clients per al servei d'enviament
 * de contingut a bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class WsClientHelper<T> {

	public T generarClientWs(URL wsdlResourceUrl, String endpoint, QName qname, String username, String password, String soapAction, boolean logMissatgesActiu,
							boolean disableCxfChunking, Class<T> clazz, Handler<?>... handlers)
							throws MalformedURLException, InstanceNotFoundException, MalformedObjectNameException, RemoteException, NamingException, CreateException {

		var url = wsdlResourceUrl;
		if (url == null) {
			url = !endpoint.endsWith("?wsdl") ? new URL(endpoint + "?wsdl") : new URL(endpoint);
		}
		var service = Service.create(url, qname);
		T servicePort = service.getPort(clazz);
		var bindingProvider = (BindingProvider)servicePort;
		// Configura l'adreça del servei
		var endpointAddress = !endpoint.endsWith("?wsdl") ? endpoint : endpoint.substring(0, endpoint.length() - "?wsdl".length());
		bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
		// Configura l'autenticació si és necessària
		if (username != null && !username.isEmpty()) {
			bindingProvider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
			bindingProvider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
		}
		// Configura el log de les peticions
		@SuppressWarnings("rawtypes")
		List<Handler> handlerChain = new ArrayList<>();
		if (logMissatgesActiu) {
			System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
			System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
			System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
			System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
			// handlerChain.add(new SOAPLoggingHandler(clazz));
		}
		// Configura handlers addicionals
		for (var i = 0; i < handlers.length; i++) {
			if (handlers[i] != null) {
				handlerChain.add(handlers[i]);
			}
		}
		bindingProvider.getBinding().setHandlerChain(handlerChain);
		if (soapAction != null) {
			bindingProvider.getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, true);
			bindingProvider.getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, soapAction);
		}
		if (disableCxfChunking) {
			try {
				// Verifica si CXF es troba disponible
				Class.forName("org.apache.cxf.endpoint.Client");
				var client = ClientProxy.getClient(servicePort);
				var http = (HTTPConduit) client.getConduit();
				var httpClientPolicy = new HTTPClientPolicy();
				httpClientPolicy.setAllowChunking(false);
				http.setClient(httpClientPolicy);
			} catch( ClassNotFoundException e ) {
				// Si CXF no es troba disponible no fa res
			}
		}
		return servicePort;
	}

	public T generarClientWs(URL wsdlResourceUrl, String endpoint, QName qname, String userName, String password, boolean disableCxfChunking, Class<T> clazz, Handler<?>... handlers)
							throws MalformedURLException, InstanceNotFoundException, MalformedObjectNameException, RemoteException, NamingException, CreateException {
		return this.generarClientWs(wsdlResourceUrl, endpoint, qname, userName, password, null, false, disableCxfChunking, clazz, handlers);
	}

	public T generarClientWs(URL wsdlResourceUrl, String endpoint, QName qname, String userName, String password, Class<T> clazz, Handler<?>... handlers)
							throws MalformedURLException, InstanceNotFoundException, MalformedObjectNameException, RemoteException, NamingException, CreateException {
		return this.generarClientWs(wsdlResourceUrl, endpoint, qname, userName, password, null, false, false, clazz, handlers);
	}

	public T generarClientWs(String endpoint, QName qname, String userName, String password, Class<T> clazz, Handler<?>... handlers)
							throws MalformedURLException, InstanceNotFoundException, MalformedObjectNameException, RemoteException, NamingException, CreateException {
		return this.generarClientWs(null, endpoint, qname, userName, password, null, false, false, clazz, handlers);
	}

	public T generarClientWs(String endpoint, QName qname, Class<T> clazz, Handler<?>... handlers)
							throws MalformedURLException, InstanceNotFoundException, MalformedObjectNameException, RemoteException, NamingException, CreateException {
		return this.generarClientWs(null, endpoint, qname, null, null, null, false, false, clazz, handlers);
	}

	public static class SOAPLoggingHandler implements SOAPHandler<SOAPMessageContext> {
		private final Logger LOGGER;
		public SOAPLoggingHandler(Class<?> loggerClass) {
			super();
			LOGGER = LoggerFactory.getLogger(loggerClass);
		}
		public Set<QName> getHeaders() {
			return null;
		}
		public boolean handleMessage(SOAPMessageContext smc) {
			logXml(smc);
			return true;
		}
		public boolean handleFault(SOAPMessageContext smc) {
			logXml(smc);
			return true;
		}
		public void close(MessageContext messageContext) {
		}
		private void logXml(SOAPMessageContext messageContext) {

			var sb = new StringBuilder();
			var outboundProperty = (Boolean)messageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			sb.append(outboundProperty.booleanValue() ? "Missarge sortint: " : "Missarge entrant: ");
			/*@SuppressWarnings("unchecked")
			Map<String, List<String>> requestHeaders = (Map<String, List<String>>)context.get(MessageContext.HTTP_REQUEST_HEADERS);
			if (requestHeaders == null) {
                requestHeaders = new HashMap<String, List<String>>();
                context.put(MessageContext.HTTP_REQUEST_HEADERS, requestHeaders);
            }*/
			var message = messageContext.getMessage();
			var baos = new ByteArrayOutputStream();
			try {
				message.writeTo(baos);
				sb.append(baos.toString());
			} catch (Exception ex) {
				sb.append("Error al imprimir el missatge XML: " + ex.getMessage());
			}
			LOGGER.debug(sb.toString());
		}
	}
}

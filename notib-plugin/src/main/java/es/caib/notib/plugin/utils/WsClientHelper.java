/**
 * 
 */
package es.caib.notib.plugin.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;

import javax.ejb.CreateException;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Utilitat per a instanciar clients per al servei d'enviament
 * de contingut a bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class WsClientHelper<T> {

	public T generarClientWs(URL wsdlResourceUrl, String endpoint, QName qname, String username, String password, String soapAction, boolean logMissatgesActiu,
							boolean disableCxfChunking, Class<T> clazz, Handler<?>... handlers)
							throws MalformedURLException, InstanceNotFoundException, MalformedObjectNameException, RemoteException, NamingException, CreateException {

		var url = wsdlResourceUrl;
		var wsdl = "?wsdl";
		if (url == null) {
			url = !endpoint.endsWith(wsdl) ? new URL(endpoint + wsdl) : new URL(endpoint);
		}
		var service = Service.create(url, qname);
		T servicePort = service.getPort(clazz);
		var bindingProvider = (BindingProvider)servicePort;
		// Configura l'adreça del servei
		var endpointAddress = !endpoint.endsWith(wsdl) ? endpoint : endpoint.substring(0, endpoint.length() - wsdl.length());
		bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointAddress);

		// Verifica si CXF es troba disponible
		boolean cxfDisponible = true;
		try {
			Class.forName("org.apache.cxf.endpoint.Client");
		} catch( ClassNotFoundException e ) {
			cxfDisponible = false;
		}

		if (cxfDisponible) {
			var client = ClientProxy.getClient(servicePort);
			if (username != null && !username.isEmpty()) {
				// Create a Map for the security properties
				var props = new HashMap<String, Object>();
				props.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
				// Specify a CallbackHandler to handle username and password information
				//				props.put(WSHandlerConstants.PW_CALLBACK_REF, new UserPasswordCallbackHandler());
				props.put(WSHandlerConstants.PW_CALLBACK_REF, new CallbackHandler() {
					@Override
					public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
						Arrays.stream(callbacks).filter(WSPasswordCallback.class::isInstance)
								.map(WSPasswordCallback.class::cast)
								.forEach(callback -> callback.setPassword(password));
					}
				});
				props.put(WSHandlerConstants.USER, username);
				// Specify a password type
				props.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_DIGEST);
				// Add nonce and timestamp
				props.put(WSHandlerConstants.ADD_USERNAMETOKEN_NONCE, "true");
				props.put(WSHandlerConstants.ADD_USERNAMETOKEN_CREATED, "true");
				// Add a WSS4J interceptor to the client's outgoing interceptor chain
				WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(props);
				client.getOutInterceptors().add(wssOut);
			}
			if (disableCxfChunking) {
				var http = (HTTPConduit) client.getConduit();
				var httpClientPolicy = new HTTPClientPolicy();
				httpClientPolicy.setAllowChunking(false);
				http.setClient(httpClientPolicy);
			}
		} else {
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
		}
		// Configura handlers addicionals
		for (var handler : handlers) {
			if (handler != null) {
				handlerChain.add(handler);
			}
			if (logMissatgesActiu) {
				handlerChain.add(new SOAPLoggingHandler());
			}
		}
		bindingProvider.getBinding().setHandlerChain(handlerChain);
		if (soapAction != null) {
			bindingProvider.getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, true);
			bindingProvider.getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, soapAction);
		}
		return servicePort;
	}

	public T generarClientWs(URL wsdlResourceUrl, String endpoint, QName qname, String userName, String password, boolean logMissatgeActiu, boolean disableCxfChunking, Class<T> clazz, Handler<?>... handlers)
							throws MalformedURLException, InstanceNotFoundException, MalformedObjectNameException, RemoteException, NamingException, CreateException {
		return this.generarClientWs(wsdlResourceUrl, endpoint, qname, userName, password, null, logMissatgeActiu, disableCxfChunking, clazz, handlers);
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

		public SOAPLoggingHandler() {
			super();
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
			sb.append(Boolean.TRUE.equals(outboundProperty) ? "Missarge sortint: " : "Missarge entrant: ");
			var message = messageContext.getMessage();
			var baos = new ByteArrayOutputStream();
			try {
				message.writeTo(baos);
				sb.append(baos);
			} catch (Exception ex) {
				sb.append("Error al imprimir el missatge XML: ").append(ex.getMessage());
			}
			log.info(sb.toString());
		}
	}

}

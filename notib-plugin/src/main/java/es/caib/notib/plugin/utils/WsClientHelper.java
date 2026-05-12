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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.ejb.CreateException;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utilitat per a instanciar clients per al servei d'enviament
 * de contingut a bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class WsClientHelper<T> {

	private List<Path> tmpFile = new ArrayList<>();

	private Document fetchDocument(URL url) throws Exception {

		var conn = (HttpURLConnection) url.openConnection();
		conn.setInstanceFollowRedirects(true);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/wsdl+xml, application/xml, */*");
		conn.setRequestProperty("User-Agent", "Java/" + System.getProperty("java.version"));
		conn.connect();
		try (var in = conn.getInputStream()) {
			var dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			return dbf.newDocumentBuilder().parse(in);
		}
	}

	private Path writeDocumentToTemp(Document doc, String suffix) throws Exception {

		var tmp = Files.createTempFile("nexeaWsdl-", suffix);
		try (var os = Files.newOutputStream(tmp)) {
			var tf = TransformerFactory.newInstance();
			var t = tf.newTransformer();
			t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new DOMSource(doc), new StreamResult(os));
			tmpFile.add(tmp);
		}
		return tmp;
	}

	private void resolveAndReplaceImports(Document doc, String baseUrl, Map<String,Path> cache) throws Exception {

		// wsdl:import
		var wimports = doc.getElementsByTagNameNS("http://schemas.xmlsoap.org/wsdl/", "import");
		for (int i = 0; i < wimports.getLength(); i++) {
			var imp = (Element) wimports.item(i);
			var loc = imp.getAttribute("location");
			if (loc == null || loc.isEmpty()) continue;
			var resolved = new URL(new URL(baseUrl), loc).toString();
			if (resolved.startsWith("http://nexea.es/")) resolved = resolved.replaceFirst("http://", "https://");
			if (!cache.containsKey(resolved)) {
				var child = fetchDocument(new URL(resolved));
				resolveAndReplaceImports(child, resolved, cache);
				var childFile = writeDocumentToTemp(child, ".wsdl");
				cache.put(resolved, childFile);
			}
			imp.setAttribute("location", cache.get(resolved).toUri().toString());
		}

		// xsd:import / xsd:include
		var ximports = doc.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "import");
		for (int i = 0; i < ximports.getLength(); i++) {
			var imp = (Element) ximports.item(i);
			var loc = imp.getAttribute("schemaLocation");
			if (loc == null || loc.isEmpty()) {
				continue;
			}
			var resolved = new URL(new URL(baseUrl), loc).toString();
			if (resolved.startsWith("http://nexea.es/")) {
				resolved = resolved.replaceFirst("http://", "https://");
			}
			if (!cache.containsKey(resolved)) {
				var child = fetchDocument(new URL(resolved));
				resolveAndReplaceImports(child, resolved, cache);
				Path childFile = writeDocumentToTemp(child, ".xsd");
				cache.put(resolved, childFile);
			}
			imp.setAttribute("schemaLocation", cache.get(resolved).toUri().toString());
		}
	}

	public void deleteTmpFile() {
		try {
			for (var path : tmpFile) {
				Files.delete(path);
			}
		} catch (Exception ex) {
			log.error("Error esborrant el tmpFile " + tmpFile);
		}
	}

	public T generarClientWs(URL wsdlResourceUrl, String endpoint, QName qname, String username, String password, String soapAction, boolean logMissatgesActiu,
							boolean disableCxfChunking, boolean generateTmpFile, Class<T> clazz, Handler<?>... handlers)
							throws MalformedURLException, InstanceNotFoundException, MalformedObjectNameException, RemoteException, NamingException, CreateException {

		var url = wsdlResourceUrl;
		var wsdl = "?wsdl";
		if (url == null) {
			url = !endpoint.endsWith(wsdl) ? new URL(endpoint + wsdl) : new URL(endpoint);
		}
		Service service = null;
		if (!generateTmpFile) {
			Service.create(url, qname);
		} else {
			try {
				// fetch main WSDL as DOM
				var mainDoc = fetchDocument(url);
				// resolve imports and write imported docs to temp files, rewriting locations to file:// URIs
				Map<String, Path> cache = new HashMap<>();
				resolveAndReplaceImports(mainDoc, url.toString(), cache);
				// write final combined main WSDL to temp file
				var tmp = writeDocumentToTemp(mainDoc, ".wsdl");
				service = Service.create(tmp.toUri().toURL(), qname);
			} catch (Exception ex) {
				log.error("Error creant el wsdl", ex);
				return null;
			}

		}

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

	public T generarClientWs(URL wsdlResourceUrl, String endpoint, QName qname, String userName, String password, boolean logMissatgeActiu, boolean disableCxfChunking, boolean generateTmpFile, Class<T> clazz, Handler<?>... handlers)
							throws MalformedURLException, InstanceNotFoundException, MalformedObjectNameException, RemoteException, NamingException, CreateException {
		return this.generarClientWs(wsdlResourceUrl, endpoint, qname, userName, password, null, logMissatgeActiu, disableCxfChunking, generateTmpFile, clazz, handlers);
	}

	public T generarClientWs(URL wsdlResourceUrl, String endpoint, QName qname, String userName, String password, Class<T> clazz, Handler<?>... handlers)
							throws MalformedURLException, InstanceNotFoundException, MalformedObjectNameException, RemoteException, NamingException, CreateException {
		return this.generarClientWs(wsdlResourceUrl, endpoint, qname, userName, password, null, false, false, false, clazz, handlers);
	}

	public T generarClientWs(String endpoint, QName qname, String userName, String password, Class<T> clazz, Handler<?>... handlers)
							throws MalformedURLException, InstanceNotFoundException, MalformedObjectNameException, RemoteException, NamingException, CreateException {
		return this.generarClientWs(null, endpoint, qname, userName, password, null, false, false, false, clazz,  handlers);
	}

	public T generarClientWs(String endpoint, QName qname, Class<T> clazz, Handler<?>... handlers)
							throws MalformedURLException, InstanceNotFoundException, MalformedObjectNameException, RemoteException, NamingException, CreateException {
		return this.generarClientWs(null, endpoint, qname, null, null, null, false, false, false, clazz, handlers);
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

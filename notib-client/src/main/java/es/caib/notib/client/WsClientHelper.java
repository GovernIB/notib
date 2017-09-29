/**
 * 
 */
package es.caib.notib.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.CreateException;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;

import org.jboss.mx.util.MBeanProxyCreationException;

import es.caib.loginModule.auth.ControladorSesion;
import es.caib.loginModule.client.AuthenticationFailureException;
import es.caib.loginModule.client.AuthorizationToken;

/**
 * Utilitat per a instanciar clients per al servei d'enviament
 * de contingut a bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class WsClientHelper<T> {

	public T generarClientWs(
			URL wsdlResourceUrl,
			String endpoint,
			QName qname,
			String username,
			String password,
			Class<T> clazz,
			Handler<?>... handlers) throws MalformedURLException, InstanceNotFoundException, MalformedObjectNameException, MBeanProxyCreationException, RemoteException, NamingException, CreateException, AuthenticationFailureException {
		URL url = wsdlResourceUrl;
		if (url == null) {
			if (!endpoint.endsWith("?wsdl"))
				url = new URL(endpoint + "?wsdl");
			else
				url = new URL(endpoint);
		}
		Service service = Service.create(url, qname);
		T bustiaWs = service.getPort(clazz);
		BindingProvider bindingProvider = (BindingProvider)bustiaWs;
		// Configura l'adreça del servei
		String endpointAddress;
		if (!endpoint.endsWith("?wsdl"))
			endpointAddress = endpoint;
		else
			endpointAddress = endpoint.substring(0, endpoint.length() - "?wsdl".length());
		bindingProvider.getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				endpointAddress);
		if (username != null && !username.isEmpty()) {
			if (isExecucioDinsJBoss()) {
				ControladorSesion controlador = new ControladorSesion();
				controlador.autenticar(username, password);
				AuthorizationToken token = controlador.getToken();
				bindingProvider.getRequestContext().put(
						BindingProvider.USERNAME_PROPERTY,
						token.getUser());
				bindingProvider.getRequestContext().put(
						BindingProvider.PASSWORD_PROPERTY,
						token.getPassword());
			} else {
				bindingProvider.getRequestContext().put(
						BindingProvider.USERNAME_PROPERTY,
						username);
				bindingProvider.getRequestContext().put(
						BindingProvider.PASSWORD_PROPERTY,
						password);
			}
		}
		// Configura el log de les peticions
		@SuppressWarnings("rawtypes")
		List<Handler> handlerChain = new ArrayList<Handler>();
		handlerChain.add(new SoapLoggingHandler(clazz));
		// Configura handlers addicionals
		for (int i = 0; i < handlers.length; i++) {
			if (handlers[i] != null)
				handlerChain.add(handlers[i]);
		}
		bindingProvider.getBinding().setHandlerChain(handlerChain);
		return bustiaWs;
	}

	public T generarClientWs(
			String endpoint,
			QName qname,
			String username,
			String password,
			Class<T> clazz,
			Handler<?>... handlers) throws MalformedURLException, InstanceNotFoundException, MalformedObjectNameException, MBeanProxyCreationException, RemoteException, NamingException, CreateException, AuthenticationFailureException {
		return this.generarClientWs(
				null,
				endpoint,
				qname,
				username,
				password,
				clazz);
	}

	private boolean isExecucioDinsJBoss() {
		return System.getProperty("jboss.server.name") != null;
	}

}

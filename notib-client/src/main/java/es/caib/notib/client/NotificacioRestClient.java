/**
 * 
 */
package es.caib.notib.client;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.CreateException;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;

import org.jboss.mx.util.MBeanProxyCreationException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;

import es.caib.loginModule.auth.ControladorSesion;
import es.caib.loginModule.client.AuthenticationFailureException;
import es.caib.loginModule.client.AuthorizationToken;
import es.caib.notib.ws.notificacio.InformacioEnviament;
import es.caib.notib.ws.notificacio.Notificacio;
import es.caib.notib.ws.notificacio.NotificacioServiceWsException_Exception;

/**
 * Client REST per al servei de notificacions de NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioRestClient {

	private static final String NOTIFICACIO_SERVICE_PATH = "/api/services/notificacio";

	private String baseUrl;
	private String username;
	private String password;

	private boolean execucioDinsJBoss = true;

	public NotificacioRestClient(
			String baseUrl,
			String username,
			String password) {
		super();
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
	}

	public List<String> alta(
			Notificacio notificacio) throws NotificacioServiceWsException_Exception, InstanceNotFoundException, MalformedObjectNameException, MBeanProxyCreationException, NamingException, CreateException, AuthenticationFailureException, IOException {
		String urlAmbMetode = baseUrl + NOTIFICACIO_SERVICE_PATH + "/alta";
		ObjectMapper mapper  = new ObjectMapper();
		String body = mapper.writeValueAsString(notificacio);
		Client jerseyClient = generarClient();
		if (username != null) {
			autenticarClient(
					jerseyClient,
					urlAmbMetode,
					username,
					password);
		}
		String json = jerseyClient.
				resource(urlAmbMetode).
				type("application/json").
				post(String.class, body);
		return Arrays.asList(
				mapper.readValue(
						json,
						String[].class));
	}

	public InformacioEnviament consulta(
			String referencia) throws NotificacioServiceWsException_Exception, InstanceNotFoundException, MalformedObjectNameException, MBeanProxyCreationException, NamingException, CreateException, AuthenticationFailureException, IOException {
		String urlAmbMetode = baseUrl + NOTIFICACIO_SERVICE_PATH + "/consulta/" + referencia;
		Client jerseyClient = generarClient();
		if (username != null) {
			autenticarClient(
					jerseyClient,
					urlAmbMetode,
					username,
					password);
		}
		String json = jerseyClient.
				resource(urlAmbMetode).
				type("application/json").
				get(String.class);
		ObjectMapper mapper  = new ObjectMapper();
		return mapper.readValue(json, InformacioEnviament.class);
	}

	public void setExecucioDinsJBoss(boolean execucioDinsJBoss) {
		this.execucioDinsJBoss = execucioDinsJBoss;
	}



	private Client generarClient() {
		Client jerseyClient = Client.create();
		if (!execucioDinsJBoss) {
			jerseyClient.addFilter(
					new ClientFilter() {
						private ArrayList<Object> cookies;
						@Override
						public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
							if (cookies != null) {
								request.getHeaders().put("Cookie", cookies);
							}
							ClientResponse response = getNext().handle(request);
							if (response.getCookies() != null) {
								if (cookies == null) {
									cookies = new ArrayList<Object>();
								}
								cookies.addAll(response.getCookies());
							}
							return response;
						}
					}
			);
		}
		return jerseyClient;
	}

	private void autenticarClient(
			Client jerseyClient,
			String urlAmbMetode,
			String username,
			String password) throws InstanceNotFoundException, MalformedObjectNameException, MBeanProxyCreationException, RemoteException, NamingException, CreateException, AuthenticationFailureException {
		if (execucioDinsJBoss) {
			ControladorSesion controlador = new ControladorSesion();
			controlador.autenticar(username, password);
			AuthorizationToken token = controlador.getToken();
			jerseyClient.addFilter(
					new HTTPBasicAuthFilter(token.getUser(), token.getPassword()));
		} else {
			jerseyClient.resource(urlAmbMetode).get(String.class);
			Form form = new Form();
			form.putSingle("j_username", username);
			form.putSingle("j_password", password);
			jerseyClient.
			resource(baseUrl + "/j_security_check").
			type("application/x-www-form-urlencoded").
			post(form);
		}
	}

}

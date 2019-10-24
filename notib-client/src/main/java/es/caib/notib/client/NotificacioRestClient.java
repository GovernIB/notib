/**
 * 
 */
package es.caib.notib.client;

import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.ejb.CreateException;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;

import org.jboss.mx.util.MBeanProxyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;

import es.caib.loginModule.client.AuthenticationFailureException;
import es.caib.notib.ws.notificacio.NotificacioServiceV2;
import es.caib.notib.ws.notificacio.NotificacioV2;
import es.caib.notib.ws.notificacio.PermisConsulta;
import es.caib.notib.ws.notificacio.RespostaAlta;
import es.caib.notib.ws.notificacio.RespostaConsultaEstatEnviament;
import es.caib.notib.ws.notificacio.RespostaConsultaEstatNotificacio;

/**
 * Client REST per al servei de notificacions de NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioRestClient implements NotificacioServiceV2 {

	//private static final String NOTIFICACIO_SERVICE_PATH = "/api/services/notificacio";
	private static final String NOTIFICACIOV2_SERVICE_PATH = "/api/services/notificacioV2";
	
	private String baseUrl;
	private String username;
	private String password;

	private boolean serveiDesplegatDamuntJbossCaib = true;

	public NotificacioRestClient(
			String baseUrl,
			String username,
			String password) {
		super();
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
	}

	@Override
	public RespostaAlta alta(
			NotificacioV2 notificacio) {
		try {
			String urlAmbMetode = baseUrl + NOTIFICACIOV2_SERVICE_PATH + "/alta";
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
			logger.debug("Missatge REST enviat: " + body);
			String json = jerseyClient.
					resource(urlAmbMetode).
					type("application/json").
					post(String.class, body);
			logger.debug("Missatge REST rebut: " + json);
			return mapper.readValue(json, RespostaAlta.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public RespostaConsultaEstatNotificacio consultaEstatNotificacio(
			String identificador) {
		try {
			String urlAmbMetode = baseUrl + NOTIFICACIOV2_SERVICE_PATH + "/consultaEstatNotificacio/" + identificador;
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
			return mapper.readValue(json, RespostaConsultaEstatNotificacio.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public RespostaConsultaEstatEnviament consultaEstatEnviament(
			String referencia) {
		try {
			String urlAmbMetode = baseUrl + NOTIFICACIOV2_SERVICE_PATH + "/consultaEstatEnviament/" + referencia;
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
			return mapper.readValue(json, RespostaConsultaEstatEnviament.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public boolean isServeiDesplegatDamuntJboss() {
		return serveiDesplegatDamuntJbossCaib;
	}
	public void setServeiDesplegatDamuntJboss(boolean serveiDesplegatDamuntJboss) {
		this.serveiDesplegatDamuntJbossCaib = serveiDesplegatDamuntJboss;
	}



	private Client generarClient() {
		Client jerseyClient = Client.create();
		//jerseyClient.addFilter(new LoggingFilter(System.out));
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
		return jerseyClient;
	}

	private void autenticarClient(
			Client jerseyClient,
			String urlAmbMetode,
			String username,
			String password) throws InstanceNotFoundException, MalformedObjectNameException, MBeanProxyCreationException, RemoteException, NamingException, CreateException, AuthenticationFailureException {
		if (serveiDesplegatDamuntJbossCaib) {
			logger.debug(
					"Autenticant client REST per a fer peticions cap a servei desplegat a damunt jBoss (" +
					"urlAmbMetode=" + urlAmbMetode + ", " +
					"username=" + username +
					"password=********)");
			jerseyClient.resource(urlAmbMetode).get(String.class);
			Form form = new Form();
			form.putSingle("j_username", username);
			form.putSingle("j_password", password);
			jerseyClient.
			resource(baseUrl + "/j_security_check").
			type("application/x-www-form-urlencoded").
			post(form);
		} else {
			logger.debug(
					"Autenticant REST amb autenticació de tipus HTTP basic (" +
					"urlAmbMetode=" + urlAmbMetode + ", " +
					"username=" + username +
					"password=********)");
			jerseyClient.addFilter(
					new HTTPBasicAuthFilter(username, password));
		}
	}

	@Override
	public boolean donarPermisConsulta(PermisConsulta permisConsulta) {
		try {
			String urlAmbMetode = baseUrl + NOTIFICACIOV2_SERVICE_PATH + "/permisConsulta";
			ObjectMapper mapper  = new ObjectMapper();
			String body = mapper.writeValueAsString(permisConsulta);
			Client jerseyClient = generarClient();
			if (username != null) {
				autenticarClient(
						jerseyClient,
						urlAmbMetode,
						username,
						password);
			}
			logger.debug("Missatge REST enviat: " + body);
			String json = jerseyClient.
					resource(urlAmbMetode).
					type("application/json").
					post(String.class, body);
			logger.debug("Missatge REST rebut: " + json);
			return mapper.readValue(json, boolean.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private boolean isExecucioDinsJBoss() {
		return System.getProperty("jboss.server.name") != null;
	}

	private static final Logger logger = LoggerFactory.getLogger(NotificacioRestClient.class);

}

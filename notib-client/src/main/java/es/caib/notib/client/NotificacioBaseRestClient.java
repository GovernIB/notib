/**
 * 
 */
package es.caib.notib.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;
import es.caib.notib.client.domini.PermisConsulta;
import es.caib.notib.client.domini.RespostaConsultaJustificantEnviament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;

;

/**
 * Client REST per al servei de notificacions de NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class NotificacioBaseRestClient {

	protected String baseUrl;
	protected String username;
	protected String password;

	protected boolean autenticacioBasic = false;
	protected int connecTimeout = 20000;
	protected int readTimeout = 120000;

	protected Client jerseyClient;

	public RespostaConsultaJustificantEnviament consultaJustificantEnviament(String identificador, String serviceUrl) {
		try {
			String urlAmbMetode = baseUrl + serviceUrl + "/consultaJustificantNotificacio/" + identificador;
			jerseyClient = generarClient(urlAmbMetode);
			String json = jerseyClient.
					resource(urlAmbMetode).
					type("application/json").
					get(String.class);
			return getMapper().readValue(json, RespostaConsultaJustificantEnviament.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public boolean donarPermisConsulta(PermisConsulta permisConsulta, String serviceUrl) {
		try {
			String urlAmbMetode = baseUrl + serviceUrl + "/permisConsulta";
			ObjectMapper mapper = getMapper();
			String body = mapper.writeValueAsString(permisConsulta);
			jerseyClient = generarClient(urlAmbMetode);
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

	public boolean isAutenticacioBasic() {
		return autenticacioBasic;
	}

	protected Client generarClient(String urlAmbMetode) throws Exception {

		if (jerseyClient != null) {
			return jerseyClient;
		}
		jerseyClient = generarClient();
		if (username != null) {
			autenticarClient(jerseyClient, urlAmbMetode, username, password);
		}
		return jerseyClient;
	}

	protected Client generarClient() {

		jerseyClient = Client.create();
		jerseyClient.setConnectTimeout(connecTimeout);
		jerseyClient.setReadTimeout(readTimeout);
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
		jerseyClient.addFilter(
				new ClientFilter() {
					@Override
					public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
						ClientHandler ch = getNext();
				        ClientResponse resp = ch.handle(request);

						if (resp.getStatus()/100 != 3) {
//				        if (resp.getStatusInfo().getFamily() != Response.Status.Family.REDIRECTION) {
				            return resp;
				        } else {
				            String redirectTarget = resp.getHeaders().getFirst("Location");
				            request.setURI(UriBuilder.fromUri(redirectTarget).build());
				            return ch.handle(request);
				        }
					}
				}
		);
		return jerseyClient;
	}

	protected void autenticarClient(
			Client jerseyClient,
			String urlAmbMetode,
			String username,
			String password) throws Exception {
		if (!autenticacioBasic) {
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

	protected ObjectMapper getMapper() {
		return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	protected static final Logger logger = LoggerFactory.getLogger(NotificacioBaseRestClient.class);

}

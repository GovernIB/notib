/**
 * 
 */
package es.caib.notib.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import es.caib.notib.client.domini.PermisConsulta;
import es.caib.notib.client.domini.RespostaConsultaDadesRegistreV2;
import es.caib.notib.client.domini.RespostaConsultaJustificantEnviament;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.TimeUnit;

;

/**
 * Client REST per al servei de notificacions de NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public abstract class NotificacioBaseRestClient {

	protected String baseUrl;
	protected String username;
	protected String password;

	protected boolean autenticacioBasic = true;
	protected Integer connecTimeout = 20000;
	protected Integer readTimeout = 120000;

	protected Client jerseyClient;
	private ObjectMapper mapper;

	public RespostaConsultaJustificantEnviament consultaJustificantEnviament(String identificador, String serviceUrl) {

		try {
			var urlAmbMetode = baseUrl + serviceUrl + "/consultaJustificantNotificacio/" + identificador;
			jerseyClient = generarClient(urlAmbMetode);
			var wt = jerseyClient.target(urlAmbMetode);
			var json = wt.request(MediaType.APPLICATION_JSON).get(String.class);
			return getMapper().readValue(json, RespostaConsultaJustificantEnviament.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public boolean donarPermisConsulta(PermisConsulta permisConsulta, String serviceUrl) {

		try {
			var urlAmbMetode = baseUrl + serviceUrl + "/permisConsulta";
			var mapper = getMapper();
			var body = mapper.writeValueAsString(permisConsulta);
			jerseyClient = generarClient(urlAmbMetode);
			log.debug("Missatge REST enviat: " + body);
			var wt = jerseyClient.target(urlAmbMetode);
			var r = wt.request(MediaType.APPLICATION_JSON).post(Entity.json(body)).readEntity(Boolean.class);
			log.debug("Missatge REST rebut: " + r);
			return r;
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
//		if (username != null) {
//			autenticarClient(jerseyClient, urlAmbMetode, username, password);
//		}
		return jerseyClient;
	}

	protected Client generarClient() {

		var config = new ClientConfig();
		config.register(JacksonFeature.class);
		if (username != null && !username.isBlank()) {
			log.debug("Autenticant REST amb autenticació de tipus HTTP basic (usuari= {})", username);
//			var feature = HttpAuthenticationFeature.basic(username, password);
//			config.register(feature);
			config.register(HttpAuthenticationFeature.basic(username, password));
		}
		config.register(ResponseClientFilter.class);
		var clientBuilder = ClientBuilder.newBuilder().withConfig(config);
		if (connecTimeout != null) {
			clientBuilder.connectTimeout(connecTimeout, TimeUnit.MILLISECONDS);
		}
		if (readTimeout != null) {
			clientBuilder.readTimeout(readTimeout, TimeUnit.MILLISECONDS);
		}
		jerseyClient = clientBuilder.build();
		mapper = new ObjectMapper();
		// Permet rebre un sol objecte en el lloc a on hi hauria d'haver una llista.
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		// Mecanisme de deserialització dels enums
		mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		// Per a no serialitzar propietats amb valors NULL
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

//
//
//		jerseyClient = Client.create();
//		jerseyClient.setConnectTimeout(connecTimeout);
//		jerseyClient.setReadTimeout(readTimeout);
//		//jerseyClient.addFilter(new LoggingFilter(System.out));
//		jerseyClient.addFilter(
//				new ClientFilter() {
//					private ArrayList<Object> cookies;
//					@Override
//					public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
//						if (cookies != null) {
//							request.getHeaders().put("Cookie", cookies);
//						}
//						ClientResponse response = getNext().handle(request);
//						if (response.getCookies() != null) {
//							if (cookies == null) {
//								cookies = new ArrayList<Object>();
//							}
//							cookies.addAll(response.getCookies());
//						}
//						return response;
//					}
//				}
//		);
//		jerseyClient.addFilter(
//				new ClientFilter() {
//					@Override
//					public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
//						ClientHandler ch = getNext();
//				        ClientResponse resp = ch.handle(request);
//
//						if (resp.getStatus()/100 != 3) {
////				        if (resp.getStatusInfo().getFamily() != Response.Status.Family.REDIRECTION) {
//				            return resp;
//				        } else {
//				            String redirectTarget = resp.getHeaders().getFirst("Location");
//				            request.setURI(UriBuilder.fromUri(redirectTarget).build());
//				            return ch.handle(request);
//				        }
//					}
//				}
//		);
		return jerseyClient;
	}

//	protected void autenticarClient(Client jerseyClient, String urlAmbMetode, String username, String password) throws Exception {
//
//		if (!autenticacioBasic) {
//			log.debug("Autenticant client REST per a fer peticions cap a servei desplegat a damunt jBoss (urlAmbMetode=" + urlAmbMetode + ", username=" + username);
//			var form = new Form();
//			form.param("j_username", username);
//			form.param("j_password", password);
//			var wt = jerseyClient.target(baseUrl + "/j_security_check");
//			var r = wt.request(MediaType.MULTIPART_FORM_DATA).post(Entity.form(form));
////			jerseyClient.
////			resource(baseUrl + "/j_security_check").
////			type("application/x-www-form-urlencoded").
////			post(form);
//		} else {
//			log.debug("Autenticant REST amb autenticació de tipus HTTP basic (urlAmbMetode=" + urlAmbMetode + ", username=" + username);
////			jerseyClient.addFilter(new HTTPBasicAuthFilter(username, password));
//		}
//	}

	protected ObjectMapper getMapper() {
		return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

}

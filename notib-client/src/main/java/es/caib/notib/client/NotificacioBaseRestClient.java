package es.caib.notib.client; /**
 *
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import es.caib.notib.client.domini.PermisConsulta;
import es.caib.notib.client.domini.RespostaConsultaJustificantEnviament;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

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

	protected boolean debug = false;
	protected Integer connecTimeout = 20000;
	protected Integer readTimeout = 120000;
	protected Client restClient;

	public RespostaConsultaJustificantEnviament consultaJustificantEnviament(String identificador, String serviceUrl) {

		try {
			var urlAmbMetode = baseUrl + serviceUrl + "/consultaJustificantNotificacio/" + identificador;
			restClient = generarClient();
			var wt = restClient.target(urlAmbMetode);
			var json = wt.request(MediaType.APPLICATION_JSON).get(String.class);
			return getMapper().readValue(json, RespostaConsultaJustificantEnviament.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public RespostaConsultaJustificantEnviament consultaJustificantEnviamentBase64(String identificador, String serviceUrl) {

		try {
			var urlAmbMetode = baseUrl + serviceUrl + "/consultaJustificantNotificacioBase64/" + identificador;
			restClient = generarClient();
			var wt = restClient.target(urlAmbMetode);
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
			restClient = generarClient();
			log.debug("Missatge REST enviat: " + body);
			var wt = restClient.target(urlAmbMetode);
			var r = wt.request(MediaType.APPLICATION_JSON).post(Entity.json(body)).readEntity(Boolean.class);
			log.debug("Missatge REST rebut: " + r);
			return r;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public void resetClient() {
		restClient = null;
	}

	public void enableDegub() {
		this.debug = true;
		restClient = null;
	}

	public void disableDegub() {
		this.debug = false;
		restClient = null;
	}

	protected Client generarClient() {

		restClient = crearClient();
		return restClient;
	}

	protected Client crearClient() {

		var mapper = new ObjectMapper();
		// Permet rebre un sol objecte en el lloc a on hi hauria d'haver una llista.
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		// Mecanisme de deserialització dels enums
		mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		// Per a no serialitzar propietats amb valors NULL
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

		var clientBuilder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
		clientBuilder.register(new JacksonJsonProvider(mapper));
		clientBuilder.register(new ResponseClientFilter());
		if (username != null && !username.isBlank()) {
			log.debug("Autenticant REST amb autenticació de tipus HTTP basic (usuari= {})", username);
			var credentials = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
			clientBuilder.register((ClientRequestFilter) ctx -> ctx.getHeaders().putSingle("Authorization", "Basic " + credentials));
		}
		if (debug) {
			clientBuilder.register(new DebugLoggingFilter());
		}
		if (connecTimeout != null) {
			clientBuilder.connectTimeout(connecTimeout, TimeUnit.MILLISECONDS);
		}
		if (readTimeout != null) {
			clientBuilder.readTimeout(readTimeout, TimeUnit.MILLISECONDS);
		}
		restClient = clientBuilder.build();

		return restClient;
	}

	protected ObjectMapper getMapper() {
		return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	private static final class DebugLoggingFilter implements ClientRequestFilter, ClientResponseFilter {

		@Override
		public void filter(ClientRequestContext requestContext) {
			log.debug("REST {} {}", requestContext.getMethod(), requestContext.getUri());
		}

		@Override
		public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) {
			log.debug("REST resposta {} {} -> {}", requestContext.getMethod(), requestContext.getUri(), responseContext.getStatus());
		}
	}

}

package es.caib.notib.client; /**
 * 
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import es.caib.notib.client.domini.PermisConsulta;
import es.caib.notib.client.domini.RespostaConsultaJustificantEnviament;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
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
	protected boolean autenticacioBasic = true;
	protected Integer connecTimeout = 20000;
	protected Integer readTimeout = 120000;
	protected Client jerseyClient;

	public RespostaConsultaJustificantEnviament consultaJustificantEnviament(String identificador, String serviceUrl) {

		try {
			var urlAmbMetode = baseUrl + serviceUrl + "/consultaJustificantNotificacio/" + identificador;
			jerseyClient = generarClient();
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
			jerseyClient = generarClient();
			log.debug("Missatge REST enviat: " + body);
			var wt = jerseyClient.target(urlAmbMetode);
			var r = wt.request(MediaType.APPLICATION_JSON).post(Entity.json(body)).readEntity(Boolean.class);
			log.debug("Missatge REST rebut: " + r);
			return r;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public void resetClient() {
		jerseyClient = null;
	}

	public void enableDegub() {
		this.debug = true;
		jerseyClient = null;
	}

	public void disableDegub() {
		this.debug = false;
		jerseyClient = null;
	}

	protected Client generarClient() {
		return jerseyClient != null ? jerseyClient : crearClient();
	}

	protected Client crearClient() {

		var config = new ClientConfig();
		config.register(JacksonFeature.class);
		if (username != null && !username.isBlank()) {
			log.debug("Autenticant REST amb autenticació de tipus HTTP basic (usuari= {})", username);
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
		var mapper = new ObjectMapper();
		// Permet rebre un sol objecte en el lloc a on hi hauria d'haver una llista.
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		// Mecanisme de deserialització dels enums
		mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		// Per a no serialitzar propietats amb valors NULL
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

		return jerseyClient;
	}

	protected ObjectMapper getMapper() {
		return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

}

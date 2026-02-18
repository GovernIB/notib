package es.caib.notib.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.notib.client.domini.PermisConsulta;
import es.caib.notib.client.domini.RespostaConsultaJustificantEnviament;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Base64;

/**
 * Client REST per al servei de notificacions de NOTIB (JAX-RS).
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
	protected int connecTimeout = 20000;
	protected int readTimeout = 120000;

	protected Client jaxrsClient;

	public RespostaConsultaJustificantEnviament consultaJustificantEnviament(String identificador, String serviceUrl) {
		try {
			String urlAmbMetode = baseUrl + serviceUrl + "/consultaJustificantNotificacio/" + identificador;
			return clientGet(urlAmbMetode, RespostaConsultaJustificantEnviament.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public boolean donarPermisConsulta(PermisConsulta permisConsulta, String serviceUrl) {
		try {
			String urlAmbMetode = baseUrl + serviceUrl + "/permisConsulta";
			return clientPost(urlAmbMetode, permisConsulta, boolean.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public void enableDegub() {
		this.debug = true;
		closeClient();
	}

	public void disableDegub() {
		this.debug = false;
		closeClient();
	}

	protected synchronized Client generarClient() {
		if (jaxrsClient == null) {
			ClientBuilder builder = ClientBuilder.newBuilder();
			// El timeout es configura en la Configuration en JAX-RS 2.0 (o via propietats de la implementació)
			// builder.connectTimeout(connecTimeout, TimeUnit.MILLISECONDS)
			// builder.readTimeout(readTimeout, TimeUnit.MILLISECONDS);
			
			jaxrsClient = builder.build();
			jaxrsClient.property("javax.ws.rs.client.connectTimeout", connecTimeout);
			jaxrsClient.property("javax.ws.rs.client.readTimeout", readTimeout);
		}
		return jaxrsClient;
	}

	protected void closeClient() {
		if (jaxrsClient != null) {
			jaxrsClient.close();
			jaxrsClient = null;
		}
	}

	protected <T> T clientGet(String urlAmbMetode, Class<T> returnClazz) throws IOException {
		Response response = generarClient().target(urlAmbMetode)
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", getBasicAuthHeader())
				.get();
		
		if (response.getStatus() >= 400) {
			throw new RuntimeException("Error en crida REST GET " + urlAmbMetode + ". Status: " + response.getStatus());
		}
		
		String json = response.readEntity(String.class);
		return getMapper().readValue(json, returnClazz);
	}

	protected <T> T clientPost(String urlAmbMetode, Object body, Class<T> returnClazz) throws IOException {
		ObjectMapper mapper  = getMapper();
		String strBody = mapper.writeValueAsString(body);
		log.debug("Missatge REST enviat: " + strBody);

		Response response = generarClient().target(urlAmbMetode)
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", getBasicAuthHeader())
				.post(Entity.entity(strBody, MediaType.APPLICATION_JSON));

		if (response.getStatus() >= 400) {
			throw new RuntimeException("Error en crida REST POST " + urlAmbMetode + ". Status: " + response.getStatus());
		}

		String json = response.readEntity(String.class);
		log.debug("Missatge REST rebut: " + json);
		return mapper.readValue(json, returnClazz);
	}

	protected String getBasicAuthHeader() {
		if (username != null && password != null) {
			String auth = username + ":" + password;
			return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
		}
		return null;
	}

	protected ObjectMapper getMapper() {
		return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
}

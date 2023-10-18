/**
 * 
 */
package es.caib.notib.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import es.caib.notib.client.domini.PermisConsulta;
import es.caib.notib.client.domini.RespostaConsultaJustificantEnviament;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
	protected int connecTimeout = 20000;
	protected int readTimeout = 120000;
	protected boolean debug = false;

	protected Client jerseyClient;

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
		jerseyClient = null;
	}

	public void disableDegub() {
		this.debug = false;
		jerseyClient = null;
	}

	protected Client generarClient() {

		jerseyClient = Client.create();
		jerseyClient.setConnectTimeout(connecTimeout);
		jerseyClient.setReadTimeout(readTimeout);
		if (this.debug)
			jerseyClient.addFilter(new LoggingFilter(System.out));
		if (username != null)
			jerseyClient.addFilter(new HTTPBasicAuthFilter(username, password));
		return jerseyClient;
	}

	protected <T> T clientGet(String urlAmbMetode, Class<T> returnClazz) throws IOException {
		Client jerseyClient = generarClient();
		String json = jerseyClient.
				resource(urlAmbMetode).
				type("application/json").
				get(String.class);
		return getMapper().readValue(json, returnClazz);
	}

	protected <T> T clientPost(String urlAmbMetode, Object body, Class<T> returnClazz) throws IOException {
		Client jerseyClient = generarClient();
		ObjectMapper mapper  = getMapper();
		String strBody = mapper.writeValueAsString(body);
		logger.debug("Missatge REST enviat: " + strBody);
		String json = jerseyClient.
				resource(urlAmbMetode).
				type("application/json").
				post(String.class, strBody);
		logger.debug("Missatge REST rebut: " + json);
		return mapper.readValue(json, returnClazz);
	}

	protected ObjectMapper getMapper() {
		return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	protected static final Logger logger = LoggerFactory.getLogger(NotificacioBaseRestClient.class);

}

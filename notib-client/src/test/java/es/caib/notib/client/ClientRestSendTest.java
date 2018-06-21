/**
 * 
 */
package es.caib.notib.client;

import java.io.IOException;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.codec.DecoderException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.LoggingFilter;

import es.caib.notib.domini.NotificacioCanviClient;

/**
 * Test per al client REST del servei de notificacions de NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientRestSendTest extends ClientBaseTest {

	private static final String NOTIFICACIO_SERVICE_PATH = "/notificaCanvi";
	private NotificacioCanviClient notificacio;
	private String baseUrl;

	@Before
	public void setUp() throws IOException, DecoderException {
		baseUrl = "http://localhost:8280/ripea/rest/notib";
		notificacio = new NotificacioCanviClient(
				"f2fce489c3c971a1", 
				"f2fce489c3c971a0");
	}

	@Test
	public void test() throws DatatypeConfigurationException, IOException, DecoderException {
		
		try {
			String urlAmbMetode = baseUrl + NOTIFICACIO_SERVICE_PATH;
			ObjectMapper mapper  = new ObjectMapper();
			String body = mapper.writeValueAsString(notificacio);
			Client jerseyClient = Client.create();
			jerseyClient.addFilter(new LoggingFilter(System.out));
			ClientResponse response = jerseyClient.
					resource(urlAmbMetode).
					type("application/json").
					post(ClientResponse.class, body);
			
			if ( ClientResponse.Status.OK.getStatusCode() != response.getStatusInfo().getStatusCode()) {
				Assert.fail("No s'ha retornat l'status OK");
			}
				
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
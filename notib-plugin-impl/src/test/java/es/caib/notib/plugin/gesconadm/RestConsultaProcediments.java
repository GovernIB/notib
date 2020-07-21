package es.caib.notib.plugin.gesconadm;

import static org.junit.Assert.assertNotNull;

import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.ejb.CreateException;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.junit.Test;

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

public class RestConsultaProcediments {

	private static final String ROLSAC_URL = "https://dev.caib.es/rolsac/";
	private static final String ROLSAC_SERVICE_PATH = "api/rest/v1/";
	private static final String ROLSAC_USERNAME = "$distribucio_rolsac";
	private static final String ROLSAC_PASSWORD = "distribucio_rolsac";
	private static final Boolean ROLSAC_BASICAUTH = true;
	
	
	@Test
	public void obtenirProcedimentsRolsac() throws Exception {
		String urlAmbMetode = ROLSAC_URL + ROLSAC_SERVICE_PATH + "procedimientos";
		
		Client jerseyClient = generarClient();
		autenticarClient(
				jerseyClient,
				urlAmbMetode);
		
		String json = jerseyClient.
				resource(urlAmbMetode).
				type("application/json").
				post(String.class);
		assertNotNull(json);
		System.out.println("Missatge REST rebut: ");
		System.out.println(json);
		
		ObjectMapper mapper  = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		RespostaProcediments resposta = mapper.readValue(json, RespostaProcediments.class);
		
		assertNotNull(resposta);
		assertNotNull(resposta.getResultado());
		
	}
		
	
	
	private Client generarClient() {
		Client jerseyClient = Client.create();
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

				        if (resp.getStatusInfo().getFamily() != Response.Status.Family.REDIRECTION) {
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

	private void autenticarClient(
			Client jerseyClient,
			String urlAmbMetode) throws InstanceNotFoundException, MalformedObjectNameException, RemoteException, NamingException, CreateException {
		String username = ROLSAC_USERNAME;
		String password = ROLSAC_PASSWORD;
		
		if (!ROLSAC_BASICAUTH) {
			jerseyClient.resource(urlAmbMetode).get(String.class);
			Form form = new Form();
			form.putSingle("j_username", username);
			form.putSingle("j_password", password);
			jerseyClient.
				resource(ROLSAC_URL + "j_security_check").
				type("application/x-www-form-urlencoded").
				post(form);
		} else {
			jerseyClient.addFilter(new HTTPBasicAuthFilter(username, password));
		}
	}
}

package es.caib.notib.core.test;

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import es.caib.notib.core.api.ws.callback.NotificacioEstatClient;
import es.caib.notib.core.api.ws.notificacio.NotificacioDestinatariEstatEnum;

public class CallbackRestTest {
	
	private static final String NOTIFICACIO_ESTAT = "notificaEstat";
	
	public static void main(String[] args) throws JsonProcessingException {
		
		Client jerseyClient = new Client();
		ObjectMapper mapper  = new ObjectMapper();
		
		String user = "siona";
		String pass = "siona15";
		String urlAmbMetode = "http://localhost:8181/ripea/rest/v1/callback";
		
		if(urlAmbMetode.charAt(urlAmbMetode.length() - 1) != '/')
			urlAmbMetode = urlAmbMetode + "/";
		
		urlAmbMetode = urlAmbMetode + NOTIFICACIO_ESTAT;
		
		NotificacioEstatClient notificacioEstat = new NotificacioEstatClient(
				NotificacioDestinatariEstatEnum.NOTIFICADA,
				new Date(),
				null,
				null,
				null,
				null,
				"8vzkicPJCHM=");
		
		String body = mapper.writeValueAsString(notificacioEstat);
		
		jerseyClient.addFilter( new HTTPBasicAuthFilter(user, pass) );
		ClientResponse response = jerseyClient.
				resource(urlAmbMetode).
				type("application/json").
				post(ClientResponse.class, body);
		
		System.out.println(response.toString());
	}
}

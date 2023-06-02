package es.caib.notib.logic.test;

import com.fasterxml.jackson.core.JsonProcessingException;

public class CallbackRestTest {
	
	private static final String NOTIFICACIO_ESTAT = "notificaEstat";
	
	public static void main(String[] args) throws JsonProcessingException {
		
//		Client jerseyClient = new Client();
//		ObjectMapper mapper  = new ObjectMapper();
//
//		String user = "admin";
//		String pass = "admin";
//		String urlAmbMetode = "http://localhost:8180/ripea/rest/v1/callback";
//
//		if(urlAmbMetode.charAt(urlAmbMetode.length() - 1) != '/')
//			urlAmbMetode = urlAmbMetode + "/";
//
//		urlAmbMetode = urlAmbMetode + NOTIFICACIO_ESTAT;
//
//		NotificacioEstatClient notificacioEstat = new NotificacioEstatClient(
//				NotificacioDestinatariEstatEnum.NOTIFICADA,
//				new Date(),
//				null,
//				null,
//				null,
//				null,
//				"8vzkicPJCHM=");
//
//		String body = mapper.writeValueAsString(notificacioEstat);
//
//		jerseyClient.addFilter( new HTTPBasicAuthFilter(user, pass) );
//		jerseyClient.addFilter(new LoggingFilter(System.out));
//
//		ClientResponse response = jerseyClient.
//				resource(urlAmbMetode).
//				type("application/json").
//				post(ClientResponse.class, body);
//
//		System.out.println(response.toString());
	}
}

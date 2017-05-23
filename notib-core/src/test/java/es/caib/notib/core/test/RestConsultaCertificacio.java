package es.caib.notib.core.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class RestConsultaCertificacio {

	public static void main(String[] args) {
		
		
		Client jerseyClient = new Client();
		ObjectMapper mapper  = new ObjectMapper();
		
		String user = "prova01";
		String pass = "prova0115";
		String urlAmbMetode = "http://localhost:8080/notib/notificacio/rest/consultaCertificacio/8vzkicPJC2k=";
		
		jerseyClient.addFilter( new HTTPBasicAuthFilter(user, pass) );
		ClientResponse response = jerseyClient.
				resource(urlAmbMetode).
				type("application/json").
				get(ClientResponse.class);
		
		
		String json = response.getEntity(String.class);
		
		System.out.println( json );
		
	}

}

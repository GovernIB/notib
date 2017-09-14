package es.caib.notib.core.test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class RestConsultaEstat {

	public static void main(String[] args) {
		
		
		Client jerseyClient = new Client();
		
		String user = "prova01";
		String pass = "prova0115";
		String urlAmbMetode = "http://localhost:8080/notib/api/services/consultaEstat/8vzkicPJC2k=";
		
		jerseyClient.addFilter( new HTTPBasicAuthFilter(user, pass) );
		ClientResponse response = jerseyClient.
				resource(urlAmbMetode).
				type("application/json").
				get(ClientResponse.class);
		
		
		System.out.println( response.getStatus() );
		System.out.println( response.getEntity(String.class) );
		
	}

}

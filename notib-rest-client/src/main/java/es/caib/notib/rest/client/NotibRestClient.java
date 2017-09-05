package es.caib.notib.rest.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;

import es.caib.notib.ws.notificacio.NotificacioCertificacio;
import es.caib.notib.ws.notificacio.NotificacioEstat;
import es.caib.notib.ws.notificacio.Notificacio_Type;

public class NotibRestClient {
	
    public static List<String> alta(
    		String urlBaseNotib,
			String user,
			String pass,
    		Notificacio_Type notificacio) throws NotibRestException, JsonProcessingException, IOException {
    	
    	urlBaseNotib = urlBaseNotib + (urlBaseNotib.endsWith("/") ? "" : "/");
    	
		String urlAmbMetode = urlBaseNotib + "api/services/altaEnviament";
		String urlLogin = urlBaseNotib + "j_security_check";
		
		Client jerseyClient = generarClientRestForm();
		
		ObjectMapper mapper  = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		String body = mapper.writeValueAsString(notificacio);
		
		String json = executaPeticioPost(
    			jerseyClient, 
    			urlLogin, 
    			urlAmbMetode, 
    			body,
    			user, 
    			pass);
		
//		Amb TOMCAT - basic authentication
//    	
//		Client jerseyClient = generarClientRest(
//				urlBaseNotib,
//				user,
//				pass);
//
//		ClientResponse response = jerseyClient.
//				resource(urlAmbMetode).
//				type("application/json").
//				post(ClientResponse.class, body);
//
//		String json = response.getEntity(String.class);  		
		
//	Amb Jsoup - Jboss Form authentication 
//
//		Response responsePost = Jsoup.connect(urlAmbMetode)
//                .method(Method.POST)
//                .execute();
//        Response responseLogin = Jsoup.connect(urlBaseNotib + "notificacio/rest/j_security_check").
//            data("j_username", usuari).
//            data("j_password", contrasenya).
//            cookies(responsePost.cookies()).
//            ignoreContentType(true).
//            method(Method.POST).
//            execute();
//		
//		 String json = responseLogin.body();

		List<String> references = mapper.readValue(json, new TypeReference<List<String>>(){});
//		List<String> references = mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, String.class));
		return references;
    }

    public static Notificacio_Type consulta(
    		String urlBaseNotib,
			String user,
			String pass,
			String referencia) throws NotibRestException, JsonParseException, JsonMappingException, IOException {

    	urlBaseNotib = urlBaseNotib + (urlBaseNotib.endsWith("/") ? "" : "/");
    	
    	String urlAmbMetode = urlBaseNotib + (urlBaseNotib.endsWith("/") ? "" : "/") + "api/services/infoEnviament/" + referencia;
    	String urlLogin = urlBaseNotib + "j_security_check";
    	
    	Client jerseyClient = generarClientRestForm();
    	
    	String json = executaPeticioGet(
    			jerseyClient, 
    			urlLogin, 
    			urlAmbMetode, 
    			user, 
    			pass);
    	
//		Amb TOMCAT - basic authentication
//		
//    	Client jerseyClient = generarClientRest(
//    			urlBaseNotib,
//				user,
//				pass);
//		
//		ClientResponse response = jerseyClient.
//				resource(urlAmbMetode).
//				type("application/json").
//				get(ClientResponse.class);
//		
//		String json = response.getEntity(String.class);
    	
//	Amb Jsoup - Jboss Form authentication
//    	
//		Response responseGet = Jsoup.connect(urlAmbMetode)
//                .method(Method.GET)
//                .execute();
//        Response responseLogin = Jsoup.connect(urlLogin).//urlBaseNotib + "notificacio/rest/j_security_check").
//            data("j_username", user).
//            data("j_password", pass).
//            cookies(responseGet.cookies()).
//            ignoreContentType(true).
//            method(Method.POST).
//            execute();
//		
//		 String json = responseLogin.body();
		
		ObjectMapper mapper  = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); 
		
		Notificacio_Type notificacio = mapper.readValue(json, Notificacio_Type.class);
    	
		return notificacio;
    }

    public static NotificacioCertificacio consultaCertificacio(
    		String urlBaseNotib,
			String user,
			String pass,
    		String referencia) throws NotibRestException, JsonParseException, JsonMappingException, IOException {

    	urlBaseNotib = urlBaseNotib + (urlBaseNotib.endsWith("/") ? "" : "/");
    	
		String urlAmbMetode = urlBaseNotib + (urlBaseNotib.endsWith("/") ? "" : "/") + "api/services/consultaCertificacio/" + referencia;
		String urlLogin = urlBaseNotib + "j_security_check";
		
		Client jerseyClient = generarClientRestForm();
		
		String json = executaPeticioGet(
    			jerseyClient, 
    			urlLogin, 
    			urlAmbMetode, 
    			user, 
    			pass);
		
		ObjectMapper mapper  = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); 
		
		NotificacioCertificacio certificacio = mapper.readValue(json, NotificacioCertificacio.class);
		
    	return certificacio;
    }

    public static NotificacioEstat consultaEstat(
    		String urlBaseNotib,
			String user,
			String pass,
    		String referencia) throws NotibRestException, JsonParseException, JsonMappingException, IOException {

    	urlBaseNotib = urlBaseNotib + (urlBaseNotib.endsWith("/") ? "" : "/");
    	
    	String urlAmbMetode = urlBaseNotib + (urlBaseNotib.endsWith("/") ? "" : "/") + "api/services/consultaEstat/" + referencia;
    	String urlLogin = urlBaseNotib + "j_security_check";
    	
    	Client jerseyClient = generarClientRestForm();
    	
    	String json = executaPeticioGet(
    			jerseyClient, 
    			urlLogin, 
    			urlAmbMetode, 
    			user, 
    			pass);
		
		ObjectMapper mapper  = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		NotificacioEstat estat = mapper.readValue(json, NotificacioEstat.class);
		
    	return estat;
    }
    
//    private static Client generarClientRest(
//			String url,
//			String user,
//			String pass) {
//		
//		Client jerseyClient = new Client();
//		jerseyClient.addFilter( new HTTPBasicAuthFilter(user, pass) );
//		
//		return jerseyClient;
//		
//	}
    
    private static Client generarClientRestForm() {

    	Client jerseyClient = Client.create();

    	// Afegim un filtre per a posar les cookies rebudes del servidor i comprovar si s'ha activat el login
    	jerseyClient.addFilter(new ClientFilter() {
    		private ArrayList<Object> cookies;

    		@Override
    		public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
    			if (cookies != null) {
    				request.getHeaders().put("Cookie", cookies);
    			}
    			ClientResponse response = getNext().handle(request);
    			// copia cookies
    			if (response.getCookies() != null) {
    				if (cookies == null) {
    					cookies = new ArrayList<Object>();
    				}
    				// Afegimtotes les cookies (probablement hauriem de comprovar duplicats i cookies expirades...)
    				cookies.addAll(response.getCookies());
    			}
    			System.out.println("Cookies: " + cookies);
    			return response;
    		}
    	});
    	return jerseyClient;
    }
    
    private static String executaPeticioGet(
    		Client jerseyClient,
    		String urlLogin,
    		String urlAmbMetode,
    		String user,
    		String pass) {
    	
    	jerseyClient.resource(urlAmbMetode).get(String.class);
		
    	WebResource webResource = jerseyClient.resource(urlLogin);
		
		Form form = new Form();
		form.putSingle("j_username", user);
		form.putSingle("j_password", pass);
		webResource.type("application/x-www-form-urlencoded").post(form);
		
		ClientResponse response = jerseyClient.
				resource(urlAmbMetode).
				type("application/json").
				get(ClientResponse.class);
		
		String json = response.getEntity(String.class);
		if (response.getStatus() == Response.Status.OK.getStatusCode()) {
			if (json.startsWith("<")) // Rebem una resposta amb XML
				throw new NotibRestException(
						"Error amb l'autenticació de la petició. Revisi les dades:\n"
						+ "\t-Usuari: " + user + "\n"
						+ "\t-Constrassenya: " + "******" + "\n"
						+ "\t-URL: " + urlAmbMetode + "\n");
		} else {
			throw new NotibRestException(
					"Error al executar la petició REST.\n"
					+ "Status: " + response.getStatus() + " (" + response.getStatusInfo() + ")\n"
					+ "Resposta " + json + "\n");
		}
    	return json;
    }
    
    private static String executaPeticioPost(
    		Client jerseyClient,
    		String urlLogin,
    		String urlAmbMetode,
    		String body,
    		String user,
    		String pass) {
    	
//    	jerseyClient.resource(urlAmbMetode).post(String.class, body);
//    	
//		WebResource webResource = jerseyClient.resource(urlLogin);
//		
//		Form form = new Form();
//		form.putSingle("j_username", user);
//		form.putSingle("j_password", pass);
//		webResource.type("application/x-www-form-urlencoded").post(form);
		
		
		jerseyClient.addFilter( new HTTPBasicAuthFilter(user, pass) );
		ClientResponse response = jerseyClient.
				resource(urlAmbMetode).
				type("application/json").
				post(ClientResponse.class, body);
		
		String json = response.getEntity(String.class);
		if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
			if (json.startsWith("<")) // Rebem una resposta amb XML
				throw new NotibRestException(
						"Error amb l'autenticació de la petició. Revisi les dades:\n"
						+ "\t-Usuari: " + user + "\n"
						+ "\t-Constrassenya: " + "******" + "\n"
						+ "\t-URL: " + urlAmbMetode + "\n");
		} else {
			throw new NotibRestException(
					"Error al executar la petició REST.\n"
					+ "Status: " + response.getStatus() + " (" + response.getStatusInfo() + ")\n"
					+ "Resposta " + json + "\n");
		}
    	return json;
    }
    
}

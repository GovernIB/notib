package es.caib.notib.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.caib.notib.rest.client.NotibRestClient;
import es.caib.notib.ws.notificacio.NotificacioCertificacio;
import es.caib.notib.ws.notificacio.NotificacioEstat;
import es.caib.notib.ws.notificacio.Notificacio_Type;

public class NotibRestTest extends NotibBaseTest {

	public static void main(String[] args) {
		try {
			NotibRestTest test = new NotibRestTest();
			
			List<String> referencies = test.testAlta();
			
			for (String referencia: referencies) {
				test.testInfo(referencia);
				test.consultaEstat(referencia);
				test.consultaCertificacio(referencia);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private List<String> testAlta() {

		List<String> referencies = null;
		try {
			referencies = NotibRestClient.alta(
					properties.getProperty("es.caib.notib.base.url"), 
					properties.getProperty("es.caib.notib.client.userName"), 
					properties.getProperty("es.caib.notib.client.password"), 
					this.createNotificacio());
			
			System.out.println("Referencies:" + referencies);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return referencies;
	}

	private void testInfo(String referencia) {
		
		try {
			
			Notificacio_Type notificacio = NotibRestClient.consulta(
					properties.getProperty("es.caib.notib.base.url"), 
					properties.getProperty("es.caib.notib.client.userName"), 
					properties.getProperty("es.caib.notib.client.password"), 
					referencia);
			
			System.out.println("Notificacio (" + referencia + "): " + notificacio);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void consultaEstat(String referencia) {

		try {
			
			NotificacioEstat estat = NotibRestClient.consultaEstat(
					properties.getProperty("es.caib.notib.base.url"), 
					properties.getProperty("es.caib.notib.client.userName"), 
					properties.getProperty("es.caib.notib.client.password"), 
					referencia);
			
			System.out.println("Estat (" + referencia + "): " + estat);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void consultaCertificacio(String referencia) {

		try {
			
			NotificacioCertificacio certificacio = NotibRestClient.consultaCertificacio(
					properties.getProperty("es.caib.notib.base.url"), 
					properties.getProperty("es.caib.notib.client.userName"), 
					properties.getProperty("es.caib.notib.client.password"), 
					referencia);
			
			System.out.println("Certificacio (" + referencia + "): " + certificacio);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

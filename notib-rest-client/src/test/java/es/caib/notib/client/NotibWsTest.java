package es.caib.notib.client;

import java.net.MalformedURLException;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import es.caib.notib.ws.client.NotibWsClientFactory;
import es.caib.notib.ws.notificacio.Notificacio;
import es.caib.notib.ws.notificacio.NotificacioCertificacio;
import es.caib.notib.ws.notificacio.NotificacioEstat;
import es.caib.notib.ws.notificacio.Notificacio_Type;

public class NotibWsTest extends NotibBaseTest {

	public static void main(String[] args) {
		try {
			NotibWsTest test = new NotibWsTest();
			
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
			
			Notificacio notificacioClient = NotibWsClientFactory.getWsClient(
					properties.getProperty("es.caib.notib.client.ws.endpoint"), 
					properties.getProperty("es.caib.notib.client.userName"), 
					properties.getProperty("es.caib.notib.client.password"));
			
			referencies = notificacioClient.alta(this.createNotificacio());
			System.out.println("Referencies:" + referencies);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return referencies;
	}

	private void testInfo(String referencia) {
		
		try {
			
			Notificacio notificacioClient = NotibWsClientFactory.getWsClient(
					properties.getProperty("es.caib.notib.client.ws.endpoint"), 
					properties.getProperty("es.caib.notib.client.userName"), 
					properties.getProperty("es.caib.notib.client.password"));
			
			Notificacio_Type notificacio = notificacioClient.consulta(referencia);
			System.out.println("Notificacio (" + referencia + "): " + notificacio);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void consultaEstat(String referencia) {

		try {
			
			Notificacio notificacioClient = NotibWsClientFactory.getWsClient(
					properties.getProperty("es.caib.notib.client.ws.endpoint"), 
					properties.getProperty("es.caib.notib.client.userName"), 
					properties.getProperty("es.caib.notib.client.password"));
			
			NotificacioEstat estat = notificacioClient.consultaEstat(referencia);
			System.out.println("Estat (" + referencia + "): " + estat);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void consultaCertificacio(String referencia) {

		try {
			
			Notificacio notificacioClient = NotibWsClientFactory.getWsClient(
					properties.getProperty("es.caib.notib.client.ws.endpoint"), 
					properties.getProperty("es.caib.notib.client.userName"), 
					properties.getProperty("es.caib.notib.client.password"));
			
			NotificacioCertificacio certificacio = notificacioClient.consultaCertificacio(referencia);
			System.out.println("Certificacio (" + referencia + "): " + certificacio);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}

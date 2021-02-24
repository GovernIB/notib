/**
 * 
 */
package es.caib.notib.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.codec.DecoderException;
import org.junit.Before;
import org.junit.Test;

import es.caib.notib.ws.notificacio.EnviamentReferencia;
import es.caib.notib.ws.notificacio.NotificacioEstatEnum;
import es.caib.notib.ws.notificacio.NotificacioV2;
import es.caib.notib.ws.notificacio.RespostaAlta;

/**
 * Test per al client REST del servei de notificacions de NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientRestTest extends ClientBaseTest {

	
	private static final String URL = "http://localhost:8180/notib";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";
	
//	private static final String URL = "https://dev.caib.es/notib";
//	private static final String USERNAME = "$ripea_notib";
//	private static final String PASSWORD = "ripea_notib";

	/*
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	*/

	private NotificacioRestClient client;

	@Before
	public void setUp() throws IOException, DecoderException {
		client = NotificacioRestClientFactory.getRestClient(
				URL,
				USERNAME,
				PASSWORD,
				true);
	}

	@Test
	public void test() throws DatatypeConfigurationException, IOException, DecoderException {
		String notificacioId = new Long(System.currentTimeMillis()).toString();
		RespostaAlta respostaAlta = client.alta(
				generarNotificacioV2(
						notificacioId,
						1,
						false));
		if (respostaAlta.isError()) {
			System.out.println(">>> Reposta amb error: " + respostaAlta.getErrorDescripcio());
			
		} else {
			System.out.println(">>> Reposta Ok");
		}
		assertNotNull(respostaAlta);
		assertFalse(respostaAlta.isError());
		assertNull(respostaAlta.getErrorDescripcio());
		assertNotNull(respostaAlta.getReferencies());
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertEquals(1, referencies.size());
		assertNotNull(referencies.get(0).getReferencia());
		assertEquals(
				NotificacioEstatEnum.ENVIADA,
				respostaAlta.getEstat());
	}
	
	@Test
	public void testCarga() throws DatatypeConfigurationException, IOException, DecoderException {
		
		List<NotificacioV2> notificacions = generarMultiplesNotificacioV2(20000,1,false);
		
		for (int i = 0; i < notificacions.size(); i++) {
			System.out.println(">>> Peitició de la notificació: " +notificacions.get(i).getConcepte());
			RespostaAlta respostaAlta = client.alta(notificacions.get(i));
			if (respostaAlta.isError()) {
				System.out.println(">>> Reposta amb error: " + respostaAlta.getErrorDescripcio());
				
			} else {
				System.out.println(">>> Reposta Ok");
			}
			System.out.println(">>> Finalitzan de la notificació: " +notificacions.get(i).getConcepte());
			assertNotNull(respostaAlta);
//			assertFalse(respostaAlta.isError());
//			assertNull(respostaAlta.getErrorDescripcio());
//			assertNotNull(respostaAlta.getReferencies());
//			List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
//			assertEquals(1, referencies.size());
//			assertNotNull(referencies.get(0).getReferencia());
//			assertEquals(
//					NotificacioEstatEnum.ENVIADA,
//					respostaAlta.getEstat());
			
		}

		
	}
	

}
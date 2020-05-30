/**
 * 
 */
package es.caib.notib.client;

import java.io.IOException;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.codec.DecoderException;
import org.junit.Before;
import org.junit.Test;

/**
 * Test per al client REST del servei de notificacions de NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientRestTestV2 extends ClientBaseTest {

	
	private static final String URL = "http://localhost:8081/notib";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";
	
//	private static final String URL = "http://dev.caib.es/notib";
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
				PASSWORD);
	}
	
	@Test
	public void testV2() throws DatatypeConfigurationException, IOException, DecoderException {
//		String notificacioId = new Long(System.currentTimeMillis()).toString();
//		RespostaAlta respostaAlta = clientV2.alta(
//				generarNotificacioV2(
//						notificacioId,
//						1,
//						false));
//		if (respostaAlta.isError()) {
//			System.out.println(">>> Reposta amb error: " + respostaAlta.getErrorDescripcio());
//			
//		} else {
//			System.out.println(">>> Reposta Ok");
//		}
//		assertNotNull(respostaAlta);
//		assertFalse(respostaAlta.isError());
//		assertNull(respostaAlta.getErrorDescripcio());
//		assertNotNull(respostaAlta.getReferencies());
//		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
//		assertEquals(1, referencies.size());
//		assertNotNull(referencies.get(0).getReferencia());
//		assertEquals(
//				NotificacioEstatEnum.ENVIADA,
//				respostaAlta.getEstat());
		
		client.consultaEstatNotificacio("dsad");
		// asserts
		
//		for (EnviamentReferencia referencia: respostaAlta.getReferencies()) {
//			clientV2.consultaEstatEnviament(referencia.getReferencia());
//			// aserts
//		}
	}
}
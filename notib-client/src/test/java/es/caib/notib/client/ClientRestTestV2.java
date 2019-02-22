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
import es.caib.notib.ws.notificacio.RespostaAlta;

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

	private NotificacioRestClient clientV2;

	@Before
	public void setUp() throws IOException, DecoderException {
		clientV2 = NotificacioRestClientFactory.getRestClientV2(
				URL,
				USERNAME,
				PASSWORD);
	}
	
	@Test
	public void testV2() throws DatatypeConfigurationException, IOException, DecoderException {
		String notificacioId = new Long(System.currentTimeMillis()).toString();
		RespostaAlta respostaAlta = clientV2.alta(
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
}
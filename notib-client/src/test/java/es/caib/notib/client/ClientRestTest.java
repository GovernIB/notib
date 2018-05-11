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

import es.caib.notib.ws.notificacio.EnviamentEstatEnum;
import es.caib.notib.ws.notificacio.EnviamentReferencia;
import es.caib.notib.ws.notificacio.NotificacioEstatEnum;
import es.caib.notib.ws.notificacio.RespostaAlta;
import es.caib.notib.ws.notificacio.RespostaConsultaEstatEnviament;

/**
 * Test per al client REST del servei de notificacions de NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientRestTest extends ClientBaseTest {

	/*@Rule
	public ExpectedException expectedException = ExpectedException.none();*/

	private NotificacioRestClient client;

	@Before
	public void setUp() throws IOException, DecoderException {
		client = NotificacioRestClientFactory.getRestClient(
				"http://localhost:8080/notib",
				"notibapp",
				"notibapp");
		/*client = NotificacioRestClientFactory.getRestClient(
				"http://10.35.3.118:8180/notib",
				"notapp",
				"notapp");*/
		client.setServeiDesplegatDamuntJboss(false);
	}

	@Test
	public void test() throws DatatypeConfigurationException, IOException, DecoderException {
		String notificacioId = new Long(System.currentTimeMillis()).toString();
		RespostaAlta respostaAlta = client.alta(
				generarNotificacio(
						notificacioId,
						1,
						true));
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
		RespostaConsultaEstatEnviament respostaConsultaEstatEnviament = client.consultaEstatEnviament(referencies.get(0).getReferencia());
		assertNotNull(respostaConsultaEstatEnviament);
		assertFalse(respostaConsultaEstatEnviament.isError());
		assertNull(respostaConsultaEstatEnviament.getErrorDescripcio());
		assertEquals(
				EnviamentEstatEnum.PENDENT_SEU,
				respostaConsultaEstatEnviament.getEstat());
	}

}
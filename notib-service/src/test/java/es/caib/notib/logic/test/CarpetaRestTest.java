/**
 * 
 */
package es.caib.notib.logic.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.codec.DecoderException;

import es.caib.notib.logic.intf.rest.consulta.Arxiu;
import es.caib.notib.logic.intf.rest.consulta.Resposta;

/**
 * Test per al client REST del servei de carpeta de NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CarpetaRestTest {

	private static final String URL = "http://localhost:8080/notib";
	private static final String USERNAME = "notibcar";
	private static final String PASSWORD = "notibcar";

	private CarpetaRestClient client;

//	@Before
	public void setUp() throws IOException, DecoderException {
		client = new CarpetaRestClient(
				URL,
				USERNAME,
				PASSWORD,
				true);
	}

//	@Test
	public void testConsulta() throws DatatypeConfigurationException, IOException, DecoderException {
		String nif = "18225486x";
		int pagina = 0;
		int mida = 10;
		
		try {
			Resposta resposta = client.consultaNotificacions(
					nif, 
					pagina, 
					mida);
	
			assertNotNull(resposta);
			assertNotNull(resposta.getNumeroElementsTotals());
			assertNotNull(resposta.getNumeroElementsRetornats());
		} catch (Exception e) {
			fail("Error en la petició");
		}
	}
	
//	@Test
	public void testDocument() throws DatatypeConfigurationException, IOException, DecoderException {
		Long notificacioId = 173875L;
		
		try {
			Arxiu document = client.consultaDocument(notificacioId);
	
			assertNotNull(document);
			assertNotNull(document.getNom());
			assertNotNull(document.getContingut());
		} catch (Exception e) {
			fail("Error en la petició");
		}
	}

}
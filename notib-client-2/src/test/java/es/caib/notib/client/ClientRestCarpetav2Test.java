package es.caib.notib.client;

import es.caib.notib.client.domini.IdiomaEnumDto;
import es.caib.notib.client.domini.consulta.RespostaConsultaV2;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;

import static org.junit.Assert.*;

/**
 * Test per al client REST del servei de notificacions de NOTIB.
 *
 * Notes:
 *  * Per a passar els tests el client ha d'estar configurat amb mode síncron
 *  	- es.caib.notib.comunicacio.tipus.defecte=SINCRON
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientRestCarpetav2Test {

	
	private static final String URL = "http://localhost:8280/notibapi";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";

//	private static final String URL = "https://dev.caib.es/notib";
//	private static final String USERNAME = "$carpeta_notib";
//	private static final String PASSWORD = "carpeta_notib";

	private static final String DNI_TITULAR = "12345678z";
	private static final String DATA_INICI = "07/02/2022";
	private static final String DATA_FI = "07/10/2022";
	private static final Boolean VISIBLE = true;

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	private NotificacioRestClientV2 client;

	@Before
	public void setUp() throws Exception {

//		String keystorePath = ClientRestv2Test.class.getResource("/es/caib/notib/client/truststore.jks").toURI().getPath();
//		System.setProperty("javax.net.ssl.trustStore", keystorePath);
//		System.setProperty("javax.net.ssl.trustStorePassword", "tecnologies");

		client = NotificacioRestClientFactory.getRestClientV2(
				URL,
				USERNAME,
				PASSWORD,
				true); //tomcat = true jboss = false/true
	}

	@Test
	public void testNotificacioByTitularVisibleCat() throws Exception {

		RespostaConsultaV2 resposta = client.notificacionsByTitular(DNI_TITULAR, sdf.parse(DATA_INICI), sdf.parse(DATA_FI), VISIBLE, IdiomaEnumDto.CA, 0, 10);
		assertNotNull(resposta);

		assertTrue("No s'han trobat resultats", resposta.getNumeroElementsTotals() > 0);
		assertTrue("La pàgina és buida", resposta.getNumeroElementsRetornats() > 0);
		assertNotNull("No ha retornat resultats", resposta.getResultat());
		assertEquals("No coincideix el nombre de", resposta.getNumeroElementsRetornats(), resposta.getResultat().size());


		System.out.println(">>> Resposta: " + resposta.toString());
	}

	@Test
	public void testNotificacioByTitularNoVisibleCat() throws Exception {

		RespostaConsultaV2 resposta = client.notificacionsByTitular(DNI_TITULAR, sdf.parse(DATA_INICI), sdf.parse(DATA_FI), !VISIBLE, IdiomaEnumDto.CA, 0, 10);
		assertNotNull(resposta);

		assertTrue("No s'han trobat resultats", resposta.getNumeroElementsTotals() > 0);
		assertTrue("La pàgina és buida", resposta.getNumeroElementsRetornats() > 0);
		assertNotNull("No ha retornat resultats", resposta.getResultat());
		assertEquals("No coincideix el nombre de", resposta.getNumeroElementsRetornats(), resposta.getResultat().size());


		System.out.println(">>> Resposta: " + resposta.toString());
	}

	@Test
	public void testNotificacioByTitularVisibleEsp() throws Exception {

		RespostaConsultaV2 resposta = client.notificacionsByTitular(DNI_TITULAR, sdf.parse(DATA_INICI), sdf.parse(DATA_FI), VISIBLE, IdiomaEnumDto.ES, 0, 10);
		assertNotNull(resposta);

		assertTrue("No s'han trobat resultats", resposta.getNumeroElementsTotals() > 0);
		assertTrue("La pàgina és buida", resposta.getNumeroElementsRetornats() > 0);
		assertNotNull("No ha retornat resultats", resposta.getResultat());
		assertEquals("No coincideix el nombre de", resposta.getNumeroElementsRetornats(), resposta.getResultat().size());


		System.out.println(">>> Resposta: " + resposta.toString());
	}

	@Test
	public void testNotificacioLlegidesByTitularVisibleCat() throws Exception {

		RespostaConsultaV2 resposta = client.notificacionsLlegidesByTitular(DNI_TITULAR, sdf.parse(DATA_INICI), sdf.parse(DATA_FI), VISIBLE, IdiomaEnumDto.CA, 0, 10);
		assertNotNull(resposta);

		assertTrue("No s'han trobat resultats", resposta.getNumeroElementsTotals() > 0);
		assertTrue("La pàgina és buida", resposta.getNumeroElementsRetornats() > 0);
		assertNotNull("No ha retornat resultats", resposta.getResultat());
		assertEquals("No coincideix el nombre de", resposta.getNumeroElementsRetornats(), resposta.getResultat().size());


		System.out.println(">>> Resposta: " + resposta.toString());
	}

	@Test
	public void testNotificacioPendentsByTitularVisibleCat() throws Exception {

		RespostaConsultaV2 resposta = client.notificacionsPendentsByTitular("00000000T", sdf.parse(DATA_INICI), sdf.parse(DATA_FI), VISIBLE, IdiomaEnumDto.CA, 0, 10);
		assertNotNull(resposta);

		assertTrue("No s'han trobat resultats", resposta.getNumeroElementsTotals() > 0);
		assertTrue("La pàgina és buida", resposta.getNumeroElementsRetornats() > 0);
		assertNotNull("No ha retornat resultats", resposta.getResultat());
		assertEquals("No coincideix el nombre de", resposta.getNumeroElementsRetornats(), resposta.getResultat().size());


		System.out.println(">>> Resposta: " + resposta.toString());
	}

	@Test
	public void testComunicacioByTitularVisibleCat() throws Exception {

		RespostaConsultaV2 resposta = client.comunicacionsByTitular(DNI_TITULAR, sdf.parse(DATA_INICI), sdf.parse(DATA_FI), VISIBLE, IdiomaEnumDto.CA, 0, 10);
		assertNotNull(resposta);

		assertTrue("No s'han trobat resultats", resposta.getNumeroElementsTotals() > 0);
		assertTrue("La pàgina és buida", resposta.getNumeroElementsRetornats() > 0);
		assertNotNull("No ha retornat resultats", resposta.getResultat());
		assertEquals("No coincideix el nombre de", resposta.getNumeroElementsRetornats(), resposta.getResultat().size());


		System.out.println(">>> Resposta: " + resposta.toString());
	}

	@Test
	public void testComunicacioLlegidesByTitularVisibleCat() throws Exception {

		RespostaConsultaV2 resposta = client.comunicacionsLlegidesByTitular(DNI_TITULAR, sdf.parse(DATA_INICI), sdf.parse(DATA_FI), VISIBLE, IdiomaEnumDto.CA, 0, 10);
		assertNotNull(resposta);

		assertTrue("No s'han trobat resultats", resposta.getNumeroElementsTotals() > 0);
		assertTrue("La pàgina és buida", resposta.getNumeroElementsRetornats() > 0);
		assertNotNull("No ha retornat resultats", resposta.getResultat());
		assertEquals("No coincideix el nombre de", resposta.getNumeroElementsRetornats(), resposta.getResultat().size());


		System.out.println(">>> Resposta: " + resposta.toString());
	}

	@Test
	public void testComunicacioPendentsByTitularVisibleCat() throws Exception {

		RespostaConsultaV2 resposta = client.comunicacionsPendentsByTitular(DNI_TITULAR, sdf.parse(DATA_INICI), sdf.parse(DATA_FI), VISIBLE, IdiomaEnumDto.CA, 0, 10);
		assertNotNull(resposta);

		assertTrue("No s'han trobat resultats", resposta.getNumeroElementsTotals() > 0);
		assertTrue("La pàgina és buida", resposta.getNumeroElementsRetornats() > 0);
		assertNotNull("No ha retornat resultats", resposta.getResultat());
		assertEquals("No coincideix el nombre de", resposta.getNumeroElementsRetornats(), resposta.getResultat().size());


		System.out.println(">>> Resposta: " + resposta.toString());
	}



}
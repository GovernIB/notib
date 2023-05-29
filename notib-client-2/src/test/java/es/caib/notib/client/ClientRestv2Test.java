package es.caib.notib.client;

import es.caib.notib.client.domini.*;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
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
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ClientRestv2Test extends ClientBaseTest {

	private static final String URL = "http://localhost:8280/notibapi";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";


	// Indicar si el servidor esta configurat en mode síncron
	private static final boolean SYNC_MODE = false;

	private String identificacdor;

	/*
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	*/

	private NotificacioRestClientV2 client;

	@Before
	public void setUp() throws Exception {

//		String keystorePath = ClientRestv2Test.class.getResource("/es/caib/notib/client/truststore.jks").toURI().getPath();
//		System.setProperty("javax.net.ssl.trustStore", keystorePath);
//		System.setProperty("javax.net.ssl.trustStorePassword", "tecnologies");

		client = NotificacioRestClientFactory.getRestClientV2(URL, USERNAME, PASSWORD, true); //tomcat = true jboss = false/true
	}

	@Test
	public void alta() throws DatatypeConfigurationException, IOException {

		String notificacioId = Long.toString(System.currentTimeMillis());
		RespostaAltaV2 respostaAlta = client.alta(generarNotificacioV2(notificacioId, 1, false));
		assertNotNull(respostaAlta);
		if (respostaAlta.isError()) {
			System.out.println(">>> Reposta amb error: " + respostaAlta.getErrorDescripcio());
		} else {
			System.out.println(">>> Reposta Ok");
		}
		assertFalse(respostaAlta.isError());
		assertNull(respostaAlta.getErrorDescripcio());
		assertNotNull(respostaAlta.getReferencies());
		List<EnviamentReferenciaV2> referencies = respostaAlta.getReferencies();
		assertEquals(1, referencies.size());
		assertNotNull(referencies.get(0).getReferencia());
		assertEquals(NotificacioEstatEnum.PENDENT, respostaAlta.getEstat());
		System.out.println(">>> Identificador: " + respostaAlta.getIdentificador());
		System.out.println(">>> Referencia: " + referencies.get(0).getReferencia());
		System.out.println(">>> Informació resposta: " + respostaAlta.toString());
	}

	@Test
	public void testConsultaEstatNotificacio() throws DatatypeConfigurationException, IOException {

		// Given
		String notificacioId = Long.toString(System.currentTimeMillis());
		RespostaAltaV2 respostaAlta = client.alta(generarNotificacioV2(notificacioId,1,false));
		assertFalse(respostaAlta.isError());
		assertNull(respostaAlta.getErrorDescripcio());
		assertNotNull(respostaAlta.getReferencies());
		List<EnviamentReferenciaV2> referencies = respostaAlta.getReferencies();
		assertEquals(1, referencies.size());
		assertNotNull(referencies.get(0).getReferencia());


		// When
		RespostaConsultaEstatNotificacioV2 respostaConsultaEstatNotificacio = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertNotNull(respostaConsultaEstatNotificacio);
		if (respostaConsultaEstatNotificacio.isError()) {
			System.out.println(">>> Reposta amb error: " + respostaConsultaEstatNotificacio.getErrorDescripcio());
		} else {
			System.out.println(">>> Reposta Ok");
		}
		System.out.println(">>> Informació notificacio: " + respostaConsultaEstatNotificacio.toString());
		assertFalse(respostaConsultaEstatNotificacio.isError());
		assertNull(respostaConsultaEstatNotificacio.getErrorDescripcio());
	}

	@Test
	public void testConsultaEstatEnviament() throws DatatypeConfigurationException, IOException {

		// Given
		String notificacioId = Long.toString(System.currentTimeMillis());
		RespostaAltaV2 respostaAlta = client.alta(generarNotificacioV2(notificacioId, 1, false));
		assertFalse(respostaAlta.isError());
		assertNull(respostaAlta.getErrorDescripcio());
		assertNotNull(respostaAlta.getReferencies());
		List<EnviamentReferenciaV2> referencies = respostaAlta.getReferencies();
		assertEquals(1, referencies.size());
		assertNotNull(referencies.get(0).getReferencia());

		// When
		RespostaConsultaEstatEnviamentV2 respostaConsultaEstatEnviament = client.consultaEstatEnviament(referencies.get(0).getReferencia());
		assertNotNull(respostaConsultaEstatEnviament);
		if (respostaConsultaEstatEnviament.isError()) {
			System.out.println(">>> Reposta amb error: " + respostaConsultaEstatEnviament.getErrorDescripcio());
		} else {
			System.out.println(">>> Reposta Ok");
		}
		System.out.println(">>> Informació enviament: " + respostaConsultaEstatEnviament.toString());
		assertFalse(respostaConsultaEstatEnviament.isError());
		assertNull(respostaConsultaEstatEnviament.getErrorDescripcio());
	}

//	@Test
	public void testConsultaEstatEnviament_donadaReferencia() throws DatatypeConfigurationException, IOException {
		// Given
		String referencia = "8vzkicPP5FQ=";

		// When
		RespostaConsultaEstatEnviamentV2 respostaConsultaEstatEnviament = client.consultaEstatEnviament(referencia);
		assertNotNull(respostaConsultaEstatEnviament);
		if (respostaConsultaEstatEnviament.isError()) {
			System.out.println(">>> Reposta amb error: " + respostaConsultaEstatEnviament.getErrorDescripcio());
		} else {
			System.out.println(">>> Reposta Ok");
		}
		System.out.println(">>> Informació enviament: " + respostaConsultaEstatEnviament.toString());
		assertFalse(respostaConsultaEstatEnviament.isError());
		assertNull(respostaConsultaEstatEnviament.getErrorDescripcio());
	}

//	@Test
	public void testConsultaDadesRegistre() throws DatatypeConfigurationException, IOException {
		// Given
		DadesConsulta dadesConsulta = new DadesConsulta();
		dadesConsulta.setReferencia("8vzkicPP5FQ=");

		// When
		RespostaConsultaDadesRegistreV2 resposta = client.consultaDadesRegistre(dadesConsulta);
		if (resposta.isError()) {
			System.out.println(">>> Reposta amb error: " + resposta.getErrorDescripcio());
		} else {
			System.out.println(">>> Reposta Ok");
		}

		System.out.println(">>> Informació registre: " + resposta.toString());
		assertFalse(resposta.isError());
		assertNull(resposta.getErrorDescripcio());
	}

//	@Test
	public void testConsultaJustificant() throws DatatypeConfigurationException, IOException {

		// Given

		// When
		RespostaConsultaJustificantEnviament resposta = client.consultaJustificantEnviament("2b3538f5-bf9a-4db1-8f9c-9e984320d31f");
		if (resposta.isError()) {
			System.out.println(">>> Reposta amb error: " + resposta.getErrorDescripcio());
		} else {
			System.out.println(">>> Reposta Ok");
		}

		System.out.println(">>> Informació registre: " + resposta.toString());
		assertFalse(resposta.isError());
		assertNull(resposta.getErrorDescripcio());

//		FileOutputStream outputStream = new FileOutputStream("/justificant.pdf");
//		outputStream.write(new BASE64Decoder().decodeBuffer(new String(resposta.getJustificant().getContingut())));
	}

	@Test
	public void testConsultaAppInfo() throws DatatypeConfigurationException, IOException {
		// Given

		// When
		AppInfo resposta = client.getAppInfo();

		System.out.println(">>> Nom: " + resposta.getNom());
		System.out.println(">>> Versio: " + resposta.getVersio());
		System.out.println(">>> Data: " + resposta.getData());

		assertNotNull(resposta.getVersio());
	}

//	@Test
	public void testCarga1() {
		for (int i = 0; i < 200000; i++) {
			System.out.println("Execució 1");
			notifica(i);
		}
	}

//	@Test
	public void testCarga2() {
		for (int i = 0; i < 200000; i++) {
			System.out.println("Execució 2");
			notifica(i);
		}
	}

//	@Test
	public void testCarga3() {
		for (int i = 0; i < 200000; i++) {
			System.out.println("Execució 3");
			notifica(i);
		}
	}

//	@Test
	public void testCarga4() {
		for (int i = 0; i < 200000; i++) {
			System.out.println("Execució 4");
			notifica(i);
		}
	}

//	@Test
	public void testCarga5() {
		for (int i = 0; i < 200000; i++) {
			System.out.println("Execució 5");
			notifica(i);
		}
	}

//	@Test
	public void testCarga6() {
		for (int i = 0; i < 200000; i++) {
			System.out.println("Execució 6");
			notifica(i);
		}
	}

//	@Test
	public void testCarga7() {
		for (int i = 0; i < 200000; i++) {
			System.out.println("Execució 7");
			notifica(i);
		}
	}

//	@Test
	public void testCarga8() {
		for (int i = 0; i < 200000; i++) {
			System.out.println("Execució 8");
			notifica(i);
		}
	}

//	@Test
	public void testCarga9() {
		for (int i = 0; i < 200000; i++) {
			System.out.println("Execució 9");
			notifica(i);
		}
	}

//	@Test
	public void testCarga10() {
		for (int i = 0; i < 200000; i++) {
			System.out.println("Execució 10");
			notifica(i);
		}
	}
	// PRUEBAS DE EMISIÓN – CORRECTAS
	// =====================================================================================

	// PETICIÓN CORRECTA TIPO CENTRO DE IMPRESIÓN, DOMICILIO CONCRETO Y NACIONAL
	// -------------------------------------------------------------------------------------
	// Para cada destinatario se comprobará que devuelve un
	// identificador, la referencia del emisor y el NIF del titular.
	// -------------------------------------------------------------------------------------
	@Test
	public void pruebaEmision01() throws Exception {

		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = false;

		NotificacioV2 notificacio = generaNotificacio("Test emissió 01", numDestinataris, numDestinataris, ambEnviamentPostal, tipusEnviamentPostal,
														ambEnviamentDEH, ambEnviamentDEHObligat, ambRetard);
		realitzarIComprovarEmissio(notificacio);
	}

	// PETICIÓN CORRECTA DE TIPO CENTRO DE IMPRESIÓN, DOMICILIO CONCRETO E INTERNACIONAL.
	// -------------------------------------------------------------------------------------
	// Para cada destinatario se comprobará que devuelve un
	// identificador, la referencia del emisor y el NIF del titular.
	// -------------------------------------------------------------------------------------
	@Test
	public void pruebaEmision02() throws Exception {

		// Petició TIPO CENTRO DE IMPRESIÓN, DOMICILIO CONCRETO E INTERNACIONAL
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = true;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.ESTRANGER;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = false;

		NotificacioV2 notificacio = generaNotificacio(
				"Test emissió 02",
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);


		realitzarIComprovarEmissio(notificacio);
	}


	// PETICIÓN CORRECTA DE TIPO DEH VOLUNTARIO + CIE.
	// -------------------------------------------------------------------------------------
	// Para cada destinatario se comprobará que devuelve un
	// identificador, la referencia del emisor y el NIF del titular.
	// NOTES:
	//   * L'entitat ha de tenir activat el camp entrega DEH
	// -------------------------------------------------------------------------------------
	@Test
	public void pruebaEmision03() throws Exception {

		// Petició TIPO DEH VOLUNTARIO + CIE

		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = true;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = true;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = false;

		NotificacioV2 notificacio = generaNotificacio(
				"Test emissió 03",
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);


		realitzarIComprovarEmissio(notificacio);
	}


	// PETICIÓN CORRECTA DE TIPO DEH OBLIGADO.
	// -------------------------------------------------------------------------------------
	// Para cada destinatario se comprobará que devuelve un
	// identificador, la referencia del emisor y el NIF del titular.
	// NOTES:
	//   * L'entitat ha de tenir activat el camp entrega DEH
	// -------------------------------------------------------------------------------------
	@Test
	public void pruebaEmision04() throws Exception {

		// Petició TIPO DEH OBLIGADO

		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = true;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = true;
		boolean ambEnviamentDEHObligat = true;
		boolean ambRetard = false;

		NotificacioV2 notificacio = generaNotificacio(
				"Test emissió 04",
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);

		RespostaAltaV2 respostaAlta = client.alta(notificacio);
		assertNotNull(respostaAlta);
		assertFalse(respostaAlta.getErrorDescripcio(), respostaAlta.isError());

		List<EnviamentReferenciaV2> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));

		// Consulta estat notificacio
		RespostaConsultaEstatNotificacioV2 respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertNotNull(respostaInfo);
		assertFalse(respostaInfo.isError());

		//Consulta estat enviament
		for (EnviamentReferenciaV2 referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviamentV2 info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertFalse(info.isError());

			assertNotNull(referencia.getReferencia());
			if (SYNC_MODE && info.getEstat() == EnviamentEstat.LLEGIDA) {
				assertNotNull(info.getCertificacio());
//				assertNotNull(info.getReceptorNif());
			}
		}
	}

	private void realitzarIComprovarEmissio(NotificacioV2 notificacio) {

		RespostaAltaV2 respostaAlta = client.alta(notificacio);
		assertNotNull(respostaAlta);
		assertFalse(respostaAlta.getErrorDescripcio(), respostaAlta.isError());

		List<EnviamentReferenciaV2> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(referencies.size(), is(notificacio.getEnviaments().size()));

		// Consulta estat notificacio
		RespostaConsultaEstatNotificacioV2 respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertNotNull(respostaInfo);
		assertFalse(respostaInfo.isError());

		//Consulta estat enviament
		for (EnviamentReferenciaV2 referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviamentV2 info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertFalse(info.isError());

			assertNotNull(referencia.getReferencia());
			if (SYNC_MODE) {
				assertNotNull(info.getCertificacio());
//				assertNotNull(info.getReceptorNif());
			}
		}
	}

	// PETICIÓN CORRECTA DE ENVIO SOLO CARPETA.
	// -------------------------------------------------------------------------------------
	// Para cada destinatario se comprobará que devuelve un
	// identificador, la referencia del emisor y el NIF del titular.
	// -------------------------------------------------------------------------------------
	@Test
	public void pruebaEmision05() throws Exception {

		// Petició DE ENVIO SOLO CARPETA

		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = false;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;

		NotificacioV2 notificacio = generaNotificacio(
				"Test emissió 05",
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);

		RespostaAltaV2 respostaAlta = client.alta(notificacio);
		assertNotNull(respostaAlta);
		assertFalse(respostaAlta.getErrorDescripcio(), respostaAlta.isError());

		List<EnviamentReferenciaV2> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));

		// Consulta estat notificacio
		RespostaConsultaEstatNotificacioV2 respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertNotNull(respostaInfo);
		assertFalse(respostaInfo.isError());

		//Consulta estat enviament
		for (EnviamentReferenciaV2 referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviamentV2 info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertFalse(info.isError());

			assertNotNull(referencia.getReferencia());
			if (SYNC_MODE && info.getEstat() == EnviamentEstat.LLEGIDA) {
				assertNotNull(info.getCertificacio());
//				assertNotNull(info.getReceptorNif());
			}
		}
	}

	// PETICIÓN CORRECTA DE ENVIO SOLO CARPETA Y TIPUS INTERESADO NULO.
	// -------------------------------------------------------------------------------------
	// Para cada destinatario se comprobará que devuelve un
	// identificador, la referencia del emisor y el NIF del titular.
	// -------------------------------------------------------------------------------------
	@Test
	public void pruebaEmision06() throws Exception {

		// Petició DE ENVIO SOLO CARPETA CON TIPUS INTERESADO NULO
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = false;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;

		NotificacioV2 notificacio =  generaNotificacio(
				"Test emissió 06",
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				false);

		RespostaAltaV2 respostaAlta = client.alta(notificacio);
		assertNotNull(respostaAlta);
		assertFalse(respostaAlta.getErrorDescripcio(), respostaAlta.isError());

		List<EnviamentReferenciaV2> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));

		// Consulta estat notificacio
		RespostaConsultaEstatNotificacioV2 respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertNotNull(respostaInfo);
		assertFalse(respostaInfo.isError());

		//Consulta estat enviament
		for (EnviamentReferenciaV2 referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviamentV2 info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertFalse(info.isError());

			assertNotNull(referencia.getReferencia());
			if (SYNC_MODE && info.getEstat() == EnviamentEstat.LLEGIDA) {
				assertNotNull(info.getCertificacio());
//				assertNotNull(info.getReceptorNif());
			}
		}
	}


	// PETICIÓN CORRECTA CON MAS DE UN DESTINATARIO.
	// -------------------------------------------------------------------------------------
	// Para cada destinatario se comprobará que devuelve un identificador, la referencia
	// del emisor y el NIF del titular. En NotificaWS2 se haría como una remesa con varios
	// envíos y en NotificaWS como un envio con varios destinatarios.
	//
	// NOTES:
	//  * El client ha de tenir activada la property es.caib.notib.destinatari.multiple a true
	// -------------------------------------------------------------------------------------
	@Test
	public void pruebaEmision07() throws Exception {
		// Petició CON MAS DE UN DESTINATARIO

		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = false;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		boolean ambEnviamentDEH = false;
		boolean ambEnviamentDEHObligat = false;
		boolean ambRetard = true;

		NotificacioV2 notificacio = generaNotificacio(
				"Test emissió 07",
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);

		RespostaAltaV2 respostaAlta = client.alta(notificacio);
		assertNotNull(respostaAlta);
		assertFalse(respostaAlta.getErrorDescripcio(), respostaAlta.isError());

		List<EnviamentReferenciaV2> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numEnviaments));

		// Consulta estat notificacio
		RespostaConsultaEstatNotificacioV2 respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertNotNull(respostaInfo);
		assertFalse(respostaInfo.isError());

		//Consulta estat enviament
		for (EnviamentReferenciaV2 referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviamentV2 info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertFalse(info.isError());

			assertNotNull(referencia.getReferencia());
			if (SYNC_MODE && info.getEstat() == EnviamentEstat.LLEGIDA) {
				assertNotNull(info.getCertificacio());
//				assertNotNull(info.getReceptorNif());
			}
		}
	}

	private void notifica(int i) {
		try {
			Long ti = System.currentTimeMillis();
			System.out.println(i + ".");
			NotificacioV2 notificacio = generarNotificacio(1, false);
			System.out.println(">>> Peitició de la notificació: " + notificacio.getConcepte());
			RespostaAltaV2 respostaAlta = client.alta(notificacio);
			if (respostaAlta.isError()) {
				System.out.println(">>> Reposta amb error: " + respostaAlta.getErrorDescripcio());

			} else {
				System.out.println(">>> Reposta Ok");
			}
			System.out.println(">>> Finalitzan de la notificació: " + notificacio.getConcepte());
			System.out.println(" Duració: " + (System.currentTimeMillis() - ti) + "ms");
//			assertNotNull(respostaAlta);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Test
	public void consultaEstatNotificacioTest() throws DatatypeConfigurationException, IOException {
		// Given
//		String referencia = "43573ddf-4f26-40d9-ae80-5bc9dcafbb96";
		String referencia = "b64248ba-0a80-4d0f-a1cf-d7e28fa89f2e";

		// When
		RespostaConsultaEstatNotificacioV2 respostaConsultaEstatNotificacio = client.consultaEstatNotificacio(referencia);
		assertNotNull(respostaConsultaEstatNotificacio);
		if (respostaConsultaEstatNotificacio.isError()) {
			System.out.println(">>> Reposta amb error: " + respostaConsultaEstatNotificacio.getErrorDescripcio());
		} else {
			System.out.println(">>> Reposta Ok");
		}
		System.out.println(">>> Informació enviament: " + respostaConsultaEstatNotificacio.toString());
		assertFalse(respostaConsultaEstatNotificacio.isError());
		assertNull(respostaConsultaEstatNotificacio.getErrorDescripcio());
	}

	@Test
	public void consultaEstatEnviamentTest() throws DatatypeConfigurationException, IOException {
		// Given
//		String referencia = "a4256bed-292b-4ad1-bb84-05f8f14a7f1c";
		String referencia = "8f7271df-e62a-4754-945c-9e52259f646e";

		// When
		RespostaConsultaEstatEnviamentV2 respostaConsultaEstatEnviament = client.consultaEstatEnviament(referencia);
		assertNotNull(respostaConsultaEstatEnviament);
		if (respostaConsultaEstatEnviament.isError()) {
			System.out.println(">>> Reposta amb error: " + respostaConsultaEstatEnviament.getErrorDescripcio());
		} else {
			System.out.println(">>> Reposta Ok");
		}
		System.out.println(">>> Informació enviament: " + respostaConsultaEstatEnviament.toString());
		assertFalse(respostaConsultaEstatEnviament.isError());
		assertNull(respostaConsultaEstatEnviament.getErrorDescripcio());

		// SEGONA PETICIÓ AMB diferent sessió??
		client = NotificacioRestClientFactory.getRestClientV2(
				URL,
				USERNAME,
				PASSWORD,
				true);
		respostaConsultaEstatEnviament = client.consultaEstatEnviament(referencia);
		if (respostaConsultaEstatEnviament.isError()) {
			System.out.println(">>> Reposta amb error: " + respostaConsultaEstatEnviament.getErrorDescripcio());
		} else {
			System.out.println(">>> Reposta Ok");
		}

	}

	@Test
	public void consultaDadesRegistreTest() throws DatatypeConfigurationException, IOException {

		// Given
		DadesConsulta dadesConsulta = new DadesConsulta();
		dadesConsulta.setReferencia("409efeb4-b517-42f3-8021-af4e9f8eed65");
		dadesConsulta.setAmbJustificant(true);

		// When
		RespostaConsultaDadesRegistreV2 resposta = client.consultaDadesRegistre(dadesConsulta);
		if (resposta.isError()) {
			System.out.println(">>> Reposta amb error: " + resposta.getErrorDescripcio());
		} else {
			System.out.println(">>> Reposta Ok");
		}

//		Integer numRegistre = resposta.getNumRegistre();
//		Date dataRegistre = resposta.getDataRegistre();
//		String numRegistreFormatat = resposta.getNumRegistreFormatat();

		System.out.println(">>> Informació registre: " + resposta.toString());
		assertFalse(resposta.isError());
		assertNull(resposta.getErrorDescripcio());

		Path path = Paths.get("/var/tmp/" + dadesConsulta.getReferencia() + ".pdf");
		Files.write(path, resposta.getJustificant());
	}

	@Test
	public void consultaJustificantTest() throws DatatypeConfigurationException, IOException {
		// Given
		String identificador = "32dda4e2-9c75-41a8-97aa-4b419a7dda91";

		// When
		RespostaConsultaJustificantEnviament resposta = client.consultaJustificantEnviament(identificador);
		if (resposta.isError()) {
			System.out.println(">>> Reposta amb error: " + resposta.getErrorDescripcio());
		} else {
			System.out.println(">>> Reposta Ok");
		}

		System.out.println(">>> Informació registre: " + resposta.toString());
		assertFalse(resposta.isError());
		assertNull(resposta.getErrorDescripcio());
	}
}
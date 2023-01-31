package es.caib.notib.client;

import es.caib.notib.client.domini.*;
import org.apache.commons.codec.DecoderException;
import org.junit.Before;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
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
public class ClientRestTest extends ClientBaseTest {

	
//	private static final String URL = "http://localhost:8080/notib";
//	private static final String USERNAME = "admin";
//	private static final String PASSWORD = "admin";


	private static final String URL = "https://dev.caib.es/notib";
	private static final String USERNAME = "$ripea_notib";
	private static final String PASSWORD = "ripea_notib";

	// Indicar si el servidor esta configurat en mode síncron
	private static final boolean SYNC_MODE = false;

	/*
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	*/

	private NotificacioRestClient client;

	@Before
	public void setUp() throws Exception {

		String keystorePath = ClientRestTest.class.getResource("/es/caib/notib/client/truststore.jks").toURI().getPath();
		System.setProperty("javax.net.ssl.trustStore", keystorePath);
		System.setProperty("javax.net.ssl.trustStorePassword", "tecnologies");

		client = NotificacioRestClientFactory.getRestClient(
				URL,
				USERNAME,
				PASSWORD,
				false);
	}

	@Test
	public void test() throws DatatypeConfigurationException, IOException, DecoderException {
		String notificacioId = Long.toString(System.currentTimeMillis());
		RespostaAlta respostaAlta = client.alta(
				generarNotificacioV2(
						notificacioId,
						1,
						false));

		assertNotNull(respostaAlta);
		if (respostaAlta.isError()) {
			System.out.println(">>> Reposta amb error: " + respostaAlta.getErrorDescripcio());

		} else {
			System.out.println(">>> Reposta Ok");
		}

		assertFalse(respostaAlta.isError());
		assertNull(respostaAlta.getErrorDescripcio());
		assertNotNull(respostaAlta.getReferencies());
		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertEquals(1, referencies.size());
		assertNotNull(referencies.get(0).getReferencia());
		assertEquals(
				NotificacioEstatEnum.PENDENT,
				respostaAlta.getEstat());
	}

	@Test
	public void testConsultaEstatEnviament() throws DatatypeConfigurationException, IOException, DecoderException {
		// Given
		String notificacioId = Long.toString(System.currentTimeMillis());
		RespostaAlta respostaAlta = client.alta(
				generarNotificacioV2(
						notificacioId,
						1,
						false));

		// When
		RespostaConsultaEstatEnviament respostaConsultaEstatEnviament = client.consultaEstatEnviament(
				respostaAlta.getReferencies().get(0).getReferencia());
		assertNotNull(respostaConsultaEstatEnviament);
		if (respostaConsultaEstatEnviament.isError()) {
			System.out.println(">>> Reposta amb error: " + respostaConsultaEstatEnviament.getErrorDescripcio());

		} else {
			System.out.println(">>> Reposta Ok");
		}

		assertFalse(respostaConsultaEstatEnviament.isError());
		assertNull(respostaConsultaEstatEnviament.getErrorDescripcio());
//		assertNotNull(respostaConsultaEstatEnviament.getReferencies());
//		List<EnviamentReferencia> referencies = respostaConsultaEstatEnviament.getReferencies();
//		assertEquals(1, referencies.size());
//		assertNotNull(referencies.get(0).getReferencia());
//		assertEquals(
//				NotificacioEstatEnum.ENVIADA,
//				respostaConsultaEstatEnviament.getEstat());
	}

//	@Test
	public void testConsultaEstatEnviament_donadaReferencia() throws DatatypeConfigurationException, IOException, DecoderException {
		// Given
		String referencia = "8vzkicPP5FQ=";

		// When
		RespostaConsultaEstatEnviament respostaConsultaEstatEnviament = client.consultaEstatEnviament(referencia);
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

//		@Test
	public void testConsultaDadesRegistre() throws DatatypeConfigurationException, IOException, DecoderException {
		// Given
		DadesConsulta dadesConsulta = new DadesConsulta();
		dadesConsulta.setReferencia("8vzkicPP5FQ=");

		// When
		RespostaConsultaDadesRegistre resposta = client.consultaDadesRegistre(dadesConsulta);
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
	public void testConsultaJustificant() throws DatatypeConfigurationException, IOException, DecoderException {
		// Given
		DadesConsulta dadesConsulta = new DadesConsulta();
		dadesConsulta.setReferencia("8vzkicPP5FQ=");

		// When
		RespostaConsultaJustificantEnviament resposta = client.consultaJustificantEnviament("8vzkicPP5Fg=");
		if (resposta.isError()) {
			System.out.println(">>> Reposta amb error: " + resposta.getErrorDescripcio());
		} else {
			System.out.println(">>> Reposta Ok");
		}

		System.out.println(">>> Informació registre: " + resposta.toString());
		assertFalse(resposta.isError());
		assertNull(resposta.getErrorDescripcio());
	}

	@Test
	public void testCarga1() {
		for (int i = 0; i < 200000; i++) {
			System.out.println("Execució 1");
			notifica(i);
		}
	}

	@Test
	public void testCarga2() {
		for (int i = 0; i < 200000; i++) {
			System.out.println("Execució 2");
			notifica(i);
		}
	}

	@Test
	public void testCarga3() {
		for (int i = 0; i < 200000; i++) {
			System.out.println("Execució 3");
			notifica(i);
		}
	}

	@Test
	public void testCarga4() {
		for (int i = 0; i < 200000; i++) {
			System.out.println("Execució 4");
			notifica(i);
		}
	}

	@Test
	public void testCarga5() {
		for (int i = 0; i < 200000; i++) {
			System.out.println("Execució 5");
			notifica(i);
		}
	}

	@Test
	public void testCarga6() {
		for (int i = 0; i < 200000; i++) {
			System.out.println("Execució 6");
			notifica(i);
		}
	}

	@Test
	public void testCarga7() {
		for (int i = 0; i < 200000; i++) {
			System.out.println("Execució 7");
			notifica(i);
		}
	}

	@Test
	public void testCarga8() {
		for (int i = 0; i < 200000; i++) {
			System.out.println("Execució 8");
			notifica(i);
		}
	}

	@Test
	public void testCarga9() {
		for (int i = 0; i < 200000; i++) {
			System.out.println("Execució 9");
			notifica(i);
		}
	}

	@Test
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

		NotificacioV2 notificacio = generaNotificacio(
				"Test emissió 01",
				numDestinataris,
				numDestinataris,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				ambRetard);

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

		RespostaAlta respostaAlta = client.alta(notificacio);
		assertNotNull(respostaAlta);
		assertFalse(respostaAlta.getErrorDescripcio(), respostaAlta.isError());

		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));

		// Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertNotNull(respostaInfo);
		assertFalse(respostaInfo.isError());

		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertFalse(info.isError());

			assertNotNull(referencia.getReferencia());
			if (SYNC_MODE && info.getEstat() == EnviamentEstat.LLEGIDA) {
				assertNotNull(info.getCertificacio());
				assertNotNull(info.getReceptorNif());
			}
		}
	}

	private void realitzarIComprovarEmissio(NotificacioV2 notificacio) {
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertNotNull(respostaAlta);
		assertFalse(respostaAlta.getErrorDescripcio(), respostaAlta.isError());

		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(notificacio.getEnviaments().size()));

		// Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertNotNull(respostaInfo);
		assertFalse(respostaInfo.isError());

		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertFalse(info.isError());

			assertNotNull(referencia.getReferencia());
			if (SYNC_MODE) {
				assertNotNull(info.getCertificacio());
				assertNotNull(info.getReceptorNif());
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

		RespostaAlta respostaAlta = client.alta(notificacio);
		assertNotNull(respostaAlta);
		assertFalse(respostaAlta.getErrorDescripcio(), respostaAlta.isError());

		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));

		// Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertNotNull(respostaInfo);
		assertFalse(respostaInfo.isError());

		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertFalse(info.isError());

			assertNotNull(referencia.getReferencia());
			if (SYNC_MODE && info.getEstat() == EnviamentEstat.LLEGIDA) {
				assertNotNull(info.getCertificacio());
				assertNotNull(info.getReceptorNif());
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

		RespostaAlta respostaAlta = client.alta(notificacio);
		assertNotNull(respostaAlta);
		assertFalse(respostaAlta.getErrorDescripcio(), respostaAlta.isError());

		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numDestinataris));

		// Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertNotNull(respostaInfo);
		assertFalse(respostaInfo.isError());

		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertFalse(info.isError());

			assertNotNull(referencia.getReferencia());
			if (SYNC_MODE && info.getEstat() == EnviamentEstat.LLEGIDA) {
				assertNotNull(info.getCertificacio());
				assertNotNull(info.getReceptorNif());
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

		int numDestinataris = 3;
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

		RespostaAlta respostaAlta = client.alta(notificacio);
		assertNotNull(respostaAlta);
		assertFalse(respostaAlta.getErrorDescripcio(), respostaAlta.isError());

		List<EnviamentReferencia> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(numEnviaments));

		// Consulta estat notificacio
		RespostaConsultaEstatNotificacio respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertNotNull(respostaInfo);
		assertFalse(respostaInfo.isError());

		//Consulta estat enviament
		for (EnviamentReferencia referencia: referencies) {
			//Si no hay error
			RespostaConsultaEstatEnviament info = client.consultaEstatEnviament(referencia.getReferencia());
			assertNotNull(info);
			assertFalse(info.isError());

			assertNotNull(referencia.getReferencia());
			if (SYNC_MODE && info.getEstat() == EnviamentEstat.LLEGIDA) {
				assertNotNull(info.getCertificacio());
				assertNotNull(info.getReceptorNif());
			}
		}
	}

	private void notifica(int i) {
		try {
			Long ti = System.currentTimeMillis();
			System.out.println(i + ".");
			NotificacioV2 notificacio = generarNotificacio(1, false);
			System.out.println(">>> Peitició de la notificació: " + notificacio.getConcepte());
			RespostaAlta respostaAlta = client.alta(notificacio);
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


//	private RespostaAlta alta(
//			NotificacioV2 notificacio) {
//		try {
//			String urlAmbMetode = URL + "/api/services/notificacioV2/alta";
//			ObjectMapper mapper  = new ObjectMapper();
//			String body = mapper.writeValueAsString(notificacio);
//			Client jerseyClient = generarClient();
//			if (USERNAME != null) {
//				autenticarClient(
//						jerseyClient,
//						urlAmbMetode,
//						USERNAME,
//						PASSWORD);
//			}
//			String json = jerseyClient.
//					resource(urlAmbMetode).
//					type("application/json").
//					post(String.class, body);
//			return mapper.readValue(json, RespostaAlta.class);
//		} catch (UniformInterfaceException ue) {
//			RespostaAlta respostaAlta = new RespostaAlta();
//			ClientResponse response = ue.getResponse();
//
//			if (response != null && response.getStatus() == 401) {
//				respostaAlta.setError(true);
//				respostaAlta.setErrorDescripcio("[CLIENT] Hi ha hagut un problema d'autenticació: "  + ue.getMessage());
//				return respostaAlta;
//			}
//			throw new RuntimeException(ue);
//		} catch (Exception ex) {
//			throw new RuntimeException(ex);
//		}
//	}
//
//	private Client generarClient() {
//		Client jerseyClient = Client.create();
//		jerseyClient.setConnectTimeout(connecTimeout);
//		jerseyClient.setReadTimeout(readTimeout);
//		//jerseyClient.addFilter(new LoggingFilter(System.out));
//		jerseyClient.addFilter(
//				new ClientFilter() {
//					private ArrayList<Object> cookies;
//					@Override
//					public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
//						if (cookies != null) {
//							request.getHeaders().put("Cookie", cookies);
//						}
//						ClientResponse response = getNext().handle(request);
//						if (response.getCookies() != null) {
//							if (cookies == null) {
//								cookies = new ArrayList<Object>();
//							}
//							cookies.addAll(response.getCookies());
//						}
//						return response;
//					}
//				}
//		);
//		jerseyClient.addFilter(
//				new ClientFilter() {
//					@Override
//					public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
//						ClientHandler ch = getNext();
//						ClientResponse resp = ch.handle(request);
//
//						if (resp.getStatusInfo().getFamily() != Response.Status.Family.REDIRECTION) {
//							return resp;
//						} else {
//							String redirectTarget = resp.getHeaders().getFirst("Location");
//							request.setURI(UriBuilder.fromUri(redirectTarget).build());
//							return ch.handle(request);
//						}
//					}
//				}
//		);
//		return jerseyClient;
//	}
//
//	private void autenticarClient(
//			Client jerseyClient,
//			String urlAmbMetode,
//			String username,
//			String password) throws InstanceNotFoundException, MalformedObjectNameException, RemoteException, NamingException, CreateException {
//		if (!autenticacioBasic) {
//			jerseyClient.resource(urlAmbMetode).get(String.class);
//			Form form = new Form();
//			form.putSingle("j_username", username);
//			form.putSingle("j_password", password);
//			jerseyClient.
//					resource(URL + "/j_security_check").
//					type("application/x-www-form-urlencoded").
//					post(form);
//		} else {
//			jerseyClient.addFilter(
//					new HTTPBasicAuthFilter(username, password));
//		}
//	}


	@Test
	public void consultaEstatNotificacioTest() throws DatatypeConfigurationException, IOException, DecoderException {
		// Given
//		String referencia = "43573ddf-4f26-40d9-ae80-5bc9dcafbb96";
//		String referencia = "8vzkicOY8Nc=";
		String referencia = "4a125809-08a9-4073-8eaf-edfe1aef45c3";

		// When
		RespostaConsultaEstatNotificacio respostaConsultaEstatNotificacio = client.consultaEstatNotificacio(referencia);
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
	public void consultaEstatEnviamentTest() throws DatatypeConfigurationException, IOException, DecoderException {
		// Given
//		String referencia = "a4256bed-292b-4ad1-bb84-05f8f14a7f1c";
//		String referencia = "8vzkicOY8NM=";
		String referencia = "8154b056-6458-4913-a5dd-248686846c1d";

		// When
		RespostaConsultaEstatEnviament respostaConsultaEstatEnviament = client.consultaEstatEnviament(referencia);
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

	@Test
	public void consultaDadesRegistreTest() throws DatatypeConfigurationException, IOException, DecoderException {
		// Given
		DadesConsulta dadesConsulta = new DadesConsulta();
//		dadesConsulta.setReferencia("a4256bed-292b-4ad1-bb84-05f8f14a7f1c");
		dadesConsulta.setReferencia("8154b056-6458-4913-a5dd-248686846c1d");
		dadesConsulta.setAmbJustificant(true);

		// When
		RespostaConsultaDadesRegistre resposta = client.consultaDadesRegistre(dadesConsulta);
		if (resposta.isError()) {
			System.out.println(">>> Reposta amb error: " + resposta.getErrorDescripcio());
		} else {
			System.out.println(">>> Reposta Ok");
		}

		System.out.println(">>> Informació registre: " + resposta.toString());
		assertFalse(resposta.isError());
		assertNull(resposta.getErrorDescripcio());
	}

	@Test
	public void consultaJustificantTest() throws DatatypeConfigurationException, IOException, DecoderException {
		// Given
		DadesConsulta dadesConsulta = new DadesConsulta();
//		dadesConsulta.setReferencia("2d991961-cb95-46d9-b74c-6472952b296c");
		dadesConsulta.setReferencia("8154b056-6458-4913-a5dd-248686846c1d");

		// When
		RespostaConsultaJustificantEnviament resposta = client.consultaJustificantEnviament("2d991961-cb95-46d9-b74c-6472952b296c");
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
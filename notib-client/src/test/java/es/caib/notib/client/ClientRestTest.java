/**
 * 
 */
package es.caib.notib.client;

import es.caib.notib.ws.notificacio.EnviamentReferencia;
import es.caib.notib.ws.notificacio.NotificacioEstatEnum;
import es.caib.notib.ws.notificacio.NotificacioV2;
import es.caib.notib.ws.notificacio.RespostaAlta;
import org.apache.commons.codec.DecoderException;
import org.junit.Before;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Test per al client REST del servei de notificacions de NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientRestTest extends ClientBaseTest {

	
	private static final String URL = "http://localhost:8280/notib";
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
				false);
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

}
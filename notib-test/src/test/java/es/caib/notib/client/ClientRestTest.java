package es.caib.notib.client;

import es.caib.notib.ws.notificacio.DadesConsulta;
import es.caib.notib.ws.notificacio.EnviamentReferencia;
import es.caib.notib.ws.notificacio.NotificacioEstatEnum;
import es.caib.notib.ws.notificacio.RespostaAlta;
import es.caib.notib.ws.notificacio.RespostaConsultaDadesRegistre;
import es.caib.notib.ws.notificacio.RespostaConsultaEstatEnviament;
import es.caib.notib.ws.notificacio.RespostaConsultaJustificantEnviament;
import org.apache.commons.codec.DecoderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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


	private static final String URL = "http://localhost:8280/notib";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";


//	private static final String URL = "https://dev.caib.es/notib";
//	private static final String USERNAME = "$ripea_notib";
//	private static final String PASSWORD = "ripea_notib";

	// Indicar si el servidor esta configurat en mode síncron
	private static final boolean SYNC_MODE = false;

	/*
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	*/

	private NotificacioRestClient client;

	@BeforeEach
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
	}

	@Test
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

	@Test
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

	@Test
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

}
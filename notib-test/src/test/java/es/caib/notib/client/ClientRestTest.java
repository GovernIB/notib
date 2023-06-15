package es.caib.notib.client;


import es.caib.notib.client.domini.DadesConsulta;
import es.caib.notib.client.domini.EnviamentReferencia;
import es.caib.notib.client.domini.EnviamentReferenciaV2;
import es.caib.notib.client.domini.NotificacioEstatEnum;
import es.caib.notib.client.domini.RespostaAlta;
import es.caib.notib.client.domini.RespostaAltaV2;
import es.caib.notib.client.domini.RespostaConsultaDadesRegistre;
import es.caib.notib.client.domini.RespostaConsultaDadesRegistreV2;
import es.caib.notib.client.domini.RespostaConsultaEstatEnviament;
import es.caib.notib.client.domini.RespostaConsultaEstatEnviamentV2;
import es.caib.notib.client.domini.RespostaConsultaEstatNotificacio;
import es.caib.notib.client.domini.RespostaConsultaEstatNotificacioV2;
import es.caib.notib.client.domini.RespostaConsultaJustificantEnviament;
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


//	private static final String URL = "http://localhost:8280/notib";
//	private static final String USERNAME = "admin";
//	private static final String PASSWORD = "admin";


	private static final String URL = "https://dev.caib.es/notibapi";
	private static final String USERNAME = "$ripea_notib";
	private static final String PASSWORD = "ripea_notib";

	// Indicar si el servidor esta configurat en mode síncron
	private static final boolean SYNC_MODE = false;

	/*
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	*/

	private NotificacioRestClientV2 client;

	@BeforeEach
	public void setUp() throws Exception {

		String keystorePath = ClientRestTest.class.getResource("/es/caib/notib/client/truststore.jks").toURI().getPath();
		System.setProperty("javax.net.ssl.trustStore", keystorePath);
		System.setProperty("javax.net.ssl.trustStorePassword", "tecnologies");

		client = NotificacioRestClientFactory.getRestClientV2(
				URL,
				USERNAME,
				PASSWORD);
	}

	@Test
	public void test() throws DatatypeConfigurationException, IOException, DecoderException {
		String notificacioId = Long.toString(System.currentTimeMillis());
		RespostaAltaV2 respostaAlta = client.alta(
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
		List<EnviamentReferenciaV2> referencies = respostaAlta.getReferencies();
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
		RespostaAltaV2 respostaAlta = client.alta(
				generarNotificacioV2(
						notificacioId,
						1,
						false));

		// When
		RespostaConsultaEstatEnviamentV2 respostaConsultaEstatEnviament = client.consultaEstatEnviament(
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
	public void testConsultaEstatEnviament_donadaReferencia() throws DatatypeConfigurationException, IOException, DecoderException, InterruptedException {
		// Given
		String referencia = "f791cfe9-8e79-4af0-9935-0b72d1a6b6cb";

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

	@Test
	public void testConsultaDadesRegistre() throws DatatypeConfigurationException, IOException, DecoderException {
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










	@Test
	public void consultaEstatNotificacioTest() throws DatatypeConfigurationException, IOException, DecoderException {
		// Given
		String referencia = "43573ddf-4f26-40d9-ae80-5bc9dcafbb96";

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
	public void consultaEstatEnviamentTest() throws DatatypeConfigurationException, IOException, DecoderException {
		// Given
		String referencia = "a4256bed-292b-4ad1-bb84-05f8f14a7f1c";

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

	@Test
	public void consultaDadesRegistreTest() throws DatatypeConfigurationException, IOException, DecoderException {
		// Given
		DadesConsulta dadesConsulta = new DadesConsulta();
		dadesConsulta.setReferencia("a4256bed-292b-4ad1-bb84-05f8f14a7f1c");
		dadesConsulta.setAmbJustificant(true);

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

	@Test
	public void consultaJustificantTest() throws DatatypeConfigurationException, IOException, DecoderException {
		// Given
		DadesConsulta dadesConsulta = new DadesConsulta();
		dadesConsulta.setReferencia("2d991961-cb95-46d9-b74c-6472952b296c");

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
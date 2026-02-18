package es.caib.notib.client;

import es.caib.notib.client.domini.AppInfo;
import es.caib.notib.client.domini.EnviamentReferenciaV2;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
import es.caib.notib.client.domini.NotificacioEstatEnum;
import es.caib.notib.client.domini.NotificacioV2;
import es.caib.notib.client.domini.RespostaAltaV2;
import es.caib.notib.client.domini.RespostaConsultaEstatEnviamentV2;
import es.caib.notib.client.domini.RespostaConsultaEstatNotificacioV2;
import es.caib.notib.client.domini.ampliarPlazo.AmpliarPlazoOE;
import es.caib.notib.client.domini.ampliarPlazo.Envios;
import es.caib.notib.client.domini.ampliarPlazo.RespuestaAmpliarPlazoOE;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per al client REST del servei de notificacions de NOTIB.
 */
public class ClientRestv2Test extends ClientBaseTest {

	private static final String URL = "http://localhost:8080/notibapi";
	private static final String USERNAME = "u999000";
	private static final String PASSWORD = "u999000";
	private static final boolean SYNC_MODE = false;

	private NotificacioRestClientV2 client;

	@BeforeEach
	public void setUp() throws Exception {
		client = NotificacioRestClientFactory.getRestClientV2(URL, USERNAME, PASSWORD, false);
	}

	@Test
	public void test() throws Exception {
		String notificacioId = Long.toString(System.currentTimeMillis());
		RespostaAltaV2 respostaAlta = client.alta(generarNotificacioV2(notificacioId, 1, false));
		assertNotNull(respostaAlta);
		assertFalse(respostaAlta.isError(), (String) respostaAlta.getErrorDescripcio());
		assertNotNull(respostaAlta.getReferencies());
		List<EnviamentReferenciaV2> referencies = respostaAlta.getReferencies();
		assertEquals(1, referencies.size());
		assertEquals(NotificacioEstatEnum.PENDENT, respostaAlta.getEstat());
	}

	@Test
	public void ampliarPlazo() throws Exception {
		Envios envios = new Envios();
		envios.getIdentificador().add("5894cf6f-aa62-461a-b59e-3858796f3f3f");
		AmpliarPlazoOE ampliacio = new AmpliarPlazoOE();
		ampliacio.setEnvios(envios);
		ampliacio.setMotivo("Test");
		ampliacio.setPlazo(2);
		RespuestaAmpliarPlazoOE resposta = client.ampliarPlazoOE(ampliacio);
		assertNotNull(resposta);
		assertTrue(resposta.isOk());
	}

	@Test
	public void testConsultaEstatNotificacio() throws Exception {
		String notificacioId = Long.toString(System.currentTimeMillis());
		RespostaAltaV2 respostaAlta = client.alta(generarNotificacioV2(notificacioId,1,false));
		assertFalse(respostaAlta.isError(), (String) respostaAlta.getErrorDescripcio());
		RespostaConsultaEstatNotificacioV2 respostaInfo = client.consultaEstatNotificacio(respostaAlta.getIdentificador());
		assertNotNull(respostaInfo);
		assertFalse(respostaInfo.isError(), (String) respostaInfo.getErrorDescripcio());
	}

	@Test
	public void testConsultaEstatEnviament() throws Exception {
		String notificacioId = Long.toString(System.currentTimeMillis());
		RespostaAltaV2 respostaAlta = client.alta(generarNotificacioV2(notificacioId, 1, false));
		assertFalse(respostaAlta.isError(), (String) respostaAlta.getErrorDescripcio());
		List<EnviamentReferenciaV2> referencies = respostaAlta.getReferencies();
		RespostaConsultaEstatEnviamentV2 respostaConsultaEstatEnviament = client.consultaEstatEnviament(referencies.get(0).getReferencia());
		assertNotNull(respostaConsultaEstatEnviament);
		assertFalse(respostaConsultaEstatEnviament.isError(), (String) respostaConsultaEstatEnviament.getErrorDescripcio());
	}

	@Test
	public void testConsultaAppInfo() throws Exception {
		AppInfo resposta = client.getAppInfo();
		assertNotNull(resposta.getVersio());
	}

	@Test
	public void pruebaEmision01() throws Exception {
		int numDestinataris = 1;
		boolean ambEnviamentPostal = true;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		NotificacioV2 notificacio = generaNotificacio("Test emissió 01", numDestinataris, numDestinataris, ambEnviamentPostal, tipusEnviamentPostal, false, false, false);
		realitzarIComprovarEmissio(notificacio);
	}

	@Test
	public void pruebaEmision04() throws Exception {
		int numDestinataris = 1;
		int numEnviaments = 1;
		boolean ambEnviamentPostal = true;
		NotificaDomiciliConcretTipus tipusEnviamentPostal = NotificaDomiciliConcretTipus.NACIONAL;
		NotificacioV2 notificacio = generaNotificacio("Test emissió 04", numDestinataris, numEnviaments, ambEnviamentPostal, tipusEnviamentPostal, true, true, false);
		RespostaAltaV2 respostaAlta = client.alta(notificacio);
		assertNotNull(respostaAlta);
		assertFalse(respostaAlta.isError(), (String) respostaAlta.getErrorDescripcio());
		List<EnviamentReferenciaV2> referencies = respostaAlta.getReferencies();
		assertEquals(numDestinataris, referencies.size());
	}

	private void realitzarIComprovarEmissio(NotificacioV2 notificacio) {
		RespostaAltaV2 respostaAlta = client.alta(notificacio);
		assertNotNull(respostaAlta);
		assertFalse(respostaAlta.isError(), (String) respostaAlta.getErrorDescripcio());
		List<EnviamentReferenciaV2> referencies = respostaAlta.getReferencies();
		assertNotNull(referencies);
		assertEquals(notificacio.getEnviaments().size(), referencies.size());
	}
}

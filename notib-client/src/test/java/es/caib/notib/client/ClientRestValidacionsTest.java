/**
 * 
 */
package es.caib.notib.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.codec.DecoderException;
import org.junit.Before;
import org.junit.Test;

import es.caib.notib.ws.notificacio.EnviamentTipusEnum;
import es.caib.notib.ws.notificacio.InteressatTipusEnumDto;
import es.caib.notib.ws.notificacio.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.ws.notificacio.NotificacioEstatEnum;
import es.caib.notib.ws.notificacio.NotificacioV2;
import es.caib.notib.ws.notificacio.Persona;
import es.caib.notib.ws.notificacio.RespostaAlta;

/**
 * Test per al client REST del servei de notificacions de NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientRestValidacionsTest extends ClientBaseTest {

	
	private static final String URL = "http://localhost:8280/notib";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";
	
	//private static final String URL = "http://dev.caib.es/notib";
	//private static final String USERNAME = "$ripea_notib";
	//private static final String PASSWORD = "ripea_notib";

	/*
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	*/

	private NotificacioRestClient client;

	@Before
	public void setUp() throws IOException, DecoderException, DatatypeConfigurationException {
		client = NotificacioRestClientFactory.getRestClient(
				URL,
				USERNAME,
				PASSWORD);
		// client.setServeiDesplegatDamuntJboss(false);
	}

	@Test
	public void test1000() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setEmisorDir3Codi(null);
		enviaNotificacioError(notificacio, "1000");
	}
	
	@Test
	public void test1001() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setEmisorDir3Codi("A01234567890");
		enviaNotificacioError(notificacio, "1001");
	}
	
	@Test
	public void test1010() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setEmisorDir3Codi("XXXXXXX");
		enviaNotificacioError(notificacio, "1010");
	}
	
	@Test
	public void test1011() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setEmisorDir3Codi("DE0000001");
		enviaNotificacioError(notificacio, "1011");
	}
	
	
	@Test
	public void test1020() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setProcedimentCodi(null);
		enviaNotificacioError(notificacio, "1020");
	}
	
	@Test
	public void test1021() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setProcedimentCodi("P01234567890");
		enviaNotificacioError(notificacio, "1021");
	}
	
	@Test
	public void test1030() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setConcepte(null);
		enviaNotificacioError(notificacio, "1030");
	}
	
	@Test
	public void test1031() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setConcepte("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz");
		enviaNotificacioError(notificacio, "1031");
	}
	
	@Test
	public void test1032() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setConcepte("<Concepte>");
		enviaNotificacioError(notificacio, "1032");
	}
	
	@Test
	public void test1040() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setDescripcio("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz");
		enviaNotificacioError(notificacio, "1040");
	}
	
	@Test
	public void test1041() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setDescripcio("<Descripció>");
		enviaNotificacioError(notificacio, "1041");
	}
	
	@Test
	public void test1050() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setEnviamentTipus(null);
		enviaNotificacioError(notificacio, "1050");
	}
	
	@Test
	public void test1060() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setDocument(null);
		enviaNotificacioError(notificacio, "1060");
	}
	
	@Test
	public void test1061() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getDocument().setArxiuNom(null);
		enviaNotificacioError(notificacio, "1061");
	}
	
	@Test
	public void test1062() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getDocument().setContingutBase64(null);
		enviaNotificacioError(notificacio, "1062");
	}
	
	@Test
	public void test1070() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setUsuariCodi(null);
		enviaNotificacioError(notificacio, "1070");
	}
	
	@Test
	public void test1071() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setUsuariCodi("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXY");
		enviaNotificacioError(notificacio, "1071");
	}
	
	@Test
	public void test1080() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setNumExpedient("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz");
		enviaNotificacioError(notificacio, "1080");
	}
	
	@Test
	public void test1090() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setGrupCodi("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXY");
		enviaNotificacioError(notificacio, "1090");
	}
	
	@Test
	public void test1100() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().clear();
		enviaNotificacioError(notificacio, "1100");
	}
	
	@Test
	public void test1101() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).setServeiTipus(null);
		enviaNotificacioError(notificacio, "1101");
	}
	
	@Test
	public void test1110() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).setTitular(null);
		enviaNotificacioError(notificacio, "1110");
	}
	
	@Test
	public void test1111() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setInteressatTipus(null);
		enviaNotificacioError(notificacio, "1111");
	}
	
	@Test
	public void test1112() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setNom("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz");
		enviaNotificacioError(notificacio, "1112");
	}
	
	@Test
	public void test1113() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setLlinatge1("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz");
		enviaNotificacioError(notificacio, "1113");
	}
	
	@Test
	public void test1114() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setLlinatge2("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz");
		enviaNotificacioError(notificacio, "1114");
	}
	
	@Test
	public void test1115() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setNif("0123456789X");
		enviaNotificacioError(notificacio, "1115");
	}
	
	@Test
	public void test1116() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setNif("12345678A");
		enviaNotificacioError(notificacio, "1116");
	}
	
	@Test
	public void test1117() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setEmail("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz");
		enviaNotificacioError(notificacio, "1117");
	}
	
	@Test
	public void test1118() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setTelefon("12345678901234567890");
		enviaNotificacioError(notificacio, "1118");
	}
	
	@Test
	public void test1119() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setRaoSocial("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz");
		enviaNotificacioError(notificacio, "1119");
	}
	
	@Test
	public void test1120() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setDir3Codi("A0123456789");
		enviaNotificacioError(notificacio, "1120");
	}
	
	@Test
	public void test1121() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setIncapacitat(true);
		notificacio.getEnviaments().get(0).getDestinataris().clear();
		enviaNotificacioError(notificacio, "1121");
	}
	
	@Test
	public void test1130() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setInteressatTipus(InteressatTipusEnumDto.FISICA);
		notificacio.getEnviaments().get(0).getTitular().setNom(null);
		enviaNotificacioError(notificacio, "1130");
	}
	
	@Test
	public void test1131() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setInteressatTipus(InteressatTipusEnumDto.FISICA);
		notificacio.getEnviaments().get(0).getTitular().setLlinatge1(null);
		enviaNotificacioError(notificacio, "1131");
	}
	
	@Test
	public void test1132() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setInteressatTipus(InteressatTipusEnumDto.FISICA);
		notificacio.getEnviaments().get(0).getTitular().setNif(null);
		enviaNotificacioError(notificacio, "1132");
	}
	
	@Test
	public void test1140() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setInteressatTipus(InteressatTipusEnumDto.JURIDICA);
		notificacio.getEnviaments().get(0).getTitular().setRaoSocial(null);
		enviaNotificacioError(notificacio, "1140");
	}
	
	@Test
	public void test1141() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setInteressatTipus(InteressatTipusEnumDto.JURIDICA);
		notificacio.getEnviaments().get(0).getTitular().setRaoSocial("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz");
		notificacio.getEnviaments().get(0).getTitular().setNif(null);
		enviaNotificacioError(notificacio, "1141");
	}
	
	@Test
	public void test1150() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setInteressatTipus(InteressatTipusEnumDto.ADMINISTRACIO);
		notificacio.getEnviaments().get(0).getTitular().setNom(null);
		enviaNotificacioError(notificacio, "1150");
	}
	
	@Test
	public void test1151() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setInteressatTipus(InteressatTipusEnumDto.ADMINISTRACIO);
		notificacio.getEnviaments().get(0).getTitular().setDir3Codi(null);
		enviaNotificacioError(notificacio, "1151");
	}
	
	@Test
	public void test1160() throws DatatypeConfigurationException, IOException, DecoderException {
		// El servidor ha d'estar configurat amb: es.caib.notib.destinatari.multiple=false
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		Persona destinatari = new Persona();
		destinatari.setNom("aaa");
		destinatari.setLlinatge1("bbb");
		destinatari.setLlinatge2("ccc");
		destinatari.setNif("18225486x");
		destinatari.setTelefon("666020202");
		destinatari.setEmail("sandreu@limit.es");
		destinatari.setInteressatTipus(InteressatTipusEnumDto.FISICA);
		notificacio.getEnviaments().get(0).getDestinataris().add(destinatari);
		enviaNotificacioError(notificacio, "1160");
	}
	
	@Test
	public void test1170() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setInteressatTipus(null);
		enviaNotificacioError(notificacio, "1170");
	}
	
	@Test
	public void test1171() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setNom("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz");
		enviaNotificacioError(notificacio, "1171");
	}
	
	@Test
	public void test1172() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setLlinatge1("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz");;
		enviaNotificacioError(notificacio, "1172");
	}
	
	@Test
	public void test1173() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setLlinatge2("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz");
		enviaNotificacioError(notificacio, "1173");
	}
	
	@Test
	public void test1174() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setNif("0123456789A");
		enviaNotificacioError(notificacio, "1174");
	}
	
	@Test
	public void test1175() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setNif("12345678A");
		enviaNotificacioError(notificacio, "1175");
	}
	
	@Test
	public void test1176() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setEmail("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz");
		enviaNotificacioError(notificacio, "1176");
	}
	
	@Test
	public void test1177() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setTelefon("01234567890123456789");
		enviaNotificacioError(notificacio, "1177");
	}
	
	@Test
	public void test1178() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setRaoSocial("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz");
		enviaNotificacioError(notificacio, "1178");
	}
	
	@Test
	public void test1179() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setDir3Codi("A0123456789");
		enviaNotificacioError(notificacio, "1179");
	}
	
	@Test
	public void test1190() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setInteressatTipus(InteressatTipusEnumDto.FISICA);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setNom(null);
		enviaNotificacioError(notificacio, "1190");
	}
	
	@Test
	public void test1191() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setInteressatTipus(InteressatTipusEnumDto.FISICA);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setLlinatge1(null);
		enviaNotificacioError(notificacio, "1191");
	}
	
	@Test
	public void test1192() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setInteressatTipus(InteressatTipusEnumDto.FISICA);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setNif(null);
		enviaNotificacioError(notificacio, "1192");
	}
	
	@Test
	public void test1200() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setInteressatTipus(InteressatTipusEnumDto.JURIDICA);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setRaoSocial(null);
		enviaNotificacioError(notificacio, "1200");
	}
	
	@Test
	public void test1201() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setInteressatTipus(InteressatTipusEnumDto.JURIDICA);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setRaoSocial("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz");
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setNif(null);
		enviaNotificacioError(notificacio, "1201");
	}
	
	@Test
	public void test1210() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setInteressatTipus(InteressatTipusEnumDto.ADMINISTRACIO);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setNom(null);
		enviaNotificacioError(notificacio, "1210");
	}
	
	@Test
	public void test1211() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setInteressatTipus(InteressatTipusEnumDto.ADMINISTRACIO);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setDir3Codi(null);
		enviaNotificacioError(notificacio, "1211");
	}
	
	@Test
	public void test1220() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setEnviamentTipus(EnviamentTipusEnum.NOTIFICACIO);
		notificacio.getEnviaments().get(0).getTitular().setInteressatTipus(InteressatTipusEnumDto.ADMINISTRACIO);
		notificacio.getEnviaments().get(0).getTitular().setNif(null);
		notificacio.getEnviaments().get(0).getTitular().setDir3Codi("A00000000");;
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setInteressatTipus(InteressatTipusEnumDto.ADMINISTRACIO);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setNif(null);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setDir3Codi("A00000000");;
		enviaNotificacioError(notificacio, "1220");
	}
	
	@Test
	public void test1230() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setTipus(null);
		enviaNotificacioError(notificacio, "1230");
	}
	
	@Test
	public void test1231() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setCodiPostal(null);
		enviaNotificacioError(notificacio, "1231");
	}
	
	@Test
	public void test1232() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setViaNom("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz");
		enviaNotificacioError(notificacio, "1232");
	}
	
	@Test
	public void test1233() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setNumeroCasa("123456");
		enviaNotificacioError(notificacio, "1233");
	}
	
	@Test
	public void test1234() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setPuntKm("123456");
		enviaNotificacioError(notificacio, "1234");
	}
	
	@Test
	public void test1235() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setPortal("1234");
		enviaNotificacioError(notificacio, "1235");
	}
	
	@Test
	public void test1236() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setPorta("1234");
		enviaNotificacioError(notificacio, "1236");
	}
	
	@Test
	public void test1237() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setEscala("1234");
		enviaNotificacioError(notificacio, "1237");
	}
	
	@Test
	public void test1238() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setPlanta("1234");
		enviaNotificacioError(notificacio, "1238");
	}
	
	@Test
	public void test1239() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setBloc("1234");
		enviaNotificacioError(notificacio, "1239");
	}
	
	@Test
	public void test1240() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setComplement("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz");
		enviaNotificacioError(notificacio, "1240");
	}
	
	@Test
	public void test1241() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setNumeroQualificador("1234");
		enviaNotificacioError(notificacio, "1241");
	}
	
	@Test
	public void test1242() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setCodiPostal("01234567890");
		enviaNotificacioError(notificacio, "1242");
	}
	
	@Test
	public void test1243() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setApartatCorreus("01234567890");
		enviaNotificacioError(notificacio, "1243");
	}
	
	@Test
	public void test1244() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setMunicipiCodi("1234567");
		enviaNotificacioError(notificacio, "1244");
	}
	
	@Test
	public void test1245() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setProvincia("123");
		enviaNotificacioError(notificacio, "1245");
	}
	
	@Test
	public void test1246() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setPaisCodi("123");
		enviaNotificacioError(notificacio, "1246");
	}
	
	@Test
	public void test1247() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setPoblacio("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz");
		enviaNotificacioError(notificacio, "1247");
	}
	
	@Test
	public void test1248() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setLinea1("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz");
		enviaNotificacioError(notificacio, "1248");
	}
	
	@Test
	public void test1249() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setLinea2("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz");
		enviaNotificacioError(notificacio, "1249");
	}
	
	@Test
	public void test1260() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setViaTipus(null);
		enviaNotificacioError(notificacio, "1260");
	}
	
	@Test
	public void test1261() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setViaNom(null);
		enviaNotificacioError(notificacio, "1261");
	}
	
	@Test
	public void test1262() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setNumeroCasa(null);
		notificacio.getEnviaments().get(0).getEntregaPostal().setPuntKm(null);
		enviaNotificacioError(notificacio, "1262");
	}
	
	@Test
	public void test1263() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setMunicipiCodi(null);
		enviaNotificacioError(notificacio, "1263");
	}
	
	@Test
	public void test1264() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setProvincia(null);
		enviaNotificacioError(notificacio, "1264");
	}
	
	@Test
	public void test1265() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setPoblacio(null);
		enviaNotificacioError(notificacio, "1265");
	}
	
	@Test
	public void test1270() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setTipus(NotificaDomiciliConcretTipusEnumDto.ESTRANGER);
		notificacio.getEnviaments().get(0).getEntregaPostal().setViaNom(null);
		enviaNotificacioError(notificacio, "1270");
	}
	
	@Test
	public void test1271() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setTipus(NotificaDomiciliConcretTipusEnumDto.ESTRANGER);
		notificacio.getEnviaments().get(0).getEntregaPostal().setPaisCodi(null);
		enviaNotificacioError(notificacio, "1271");
	}
	
	@Test
	public void test1272() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setTipus(NotificaDomiciliConcretTipusEnumDto.ESTRANGER);
		notificacio.getEnviaments().get(0).getEntregaPostal().setPoblacio(null);
		enviaNotificacioError(notificacio, "1272");
	}
	
	@Test
	public void test1280() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setTipus(NotificaDomiciliConcretTipusEnumDto.APARTAT_CORREUS);
		notificacio.getEnviaments().get(0).getEntregaPostal().setApartatCorreus(null);
		enviaNotificacioError(notificacio, "1280");
	}
	
	@Test
	public void test1281() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setTipus(NotificaDomiciliConcretTipusEnumDto.APARTAT_CORREUS);
		notificacio.getEnviaments().get(0).getEntregaPostal().setApartatCorreus("0228");
		notificacio.getEnviaments().get(0).getEntregaPostal().setMunicipiCodi(null);
		enviaNotificacioError(notificacio, "1281");
	}
	
	@Test
	public void test1282() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setTipus(NotificaDomiciliConcretTipusEnumDto.APARTAT_CORREUS);
		notificacio.getEnviaments().get(0).getEntregaPostal().setApartatCorreus("0228");
		notificacio.getEnviaments().get(0).getEntregaPostal().setProvincia(null);
		enviaNotificacioError(notificacio, "1282");
	}
	
	@Test
	public void test1283() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setTipus(NotificaDomiciliConcretTipusEnumDto.APARTAT_CORREUS);
		notificacio.getEnviaments().get(0).getEntregaPostal().setApartatCorreus("0228");
		notificacio.getEnviaments().get(0).getEntregaPostal().setPoblacio(null);
		enviaNotificacioError(notificacio, "1283");
	}
	
	@Test
	public void test1290() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setTipus(NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR);
		notificacio.getEnviaments().get(0).getEntregaPostal().setLinea1(null);
		enviaNotificacioError(notificacio, "1290");
	}
	
	@Test
	public void test1291() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setTipus(NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR);
		notificacio.getEnviaments().get(0).getEntregaPostal().setLinea1("asdfasdf");
		notificacio.getEnviaments().get(0).getEntregaPostal().setLinea2(null);
		enviaNotificacioError(notificacio, "1291");
	}
	
	@Test
	public void test1300() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).setEntregaDehActiva(true);
		enviaNotificacioError(notificacio, "1300");
	}
	
	@Test
	public void test1301() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setEmisorDir3Codi("A04013511");
		notificacio.getEnviaments().get(0).setEntregaDehActiva(true);
		notificacio.getEnviaments().get(0).setEntregaDeh(null);
		enviaNotificacioError(notificacio, "1301");
	}
	
	@Test
	public void test1302() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setEmisorDir3Codi("A04013511");
		notificacio.getEnviaments().get(0).setEntregaDehActiva(true);
		notificacio.getEnviaments().get(0).getTitular().setInteressatTipus(InteressatTipusEnumDto.ADMINISTRACIO);
		notificacio.getEnviaments().get(0).getTitular().setNif(null);
		notificacio.getEnviaments().get(0).getTitular().setDir3Codi("A00000000");
		enviaNotificacioError(notificacio, "1302");
	}
	
	@Test
	public void test1310() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 2, false);
		notificacio.setEnviamentTipus(EnviamentTipusEnum.COMUNICACIO);
		notificacio.getEnviaments().get(1).getTitular().setInteressatTipus(InteressatTipusEnumDto.ADMINISTRACIO);
		notificacio.getEnviaments().get(1).getTitular().setDir3Codi("A00000000");
		enviaNotificacioError(notificacio, "1310");
	}
	
	@Test
	public void test1320() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setProcedimentCodi("846823");
		notificacio.setGrupCodi("XXXX");
		enviaNotificacioError(notificacio, "1320");
	}
	
	@Test
	public void test1321() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setProcedimentCodi("846823");
		notificacio.setGrupCodi("NOT_APL");
		enviaNotificacioError(notificacio, "1321");
	}
	
	@Test
	public void test1322() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setProcedimentCodi("234257");
		notificacio.setGrupCodi("NOT_APL");
		enviaNotificacioError(notificacio, "1322");
	}
	
	@Test
	public void test1330() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setProcedimentCodi("000000");
		enviaNotificacioError(notificacio, "1330");
	}
	
	
	
	
	private void enviaNotificacioError(NotificacioV2 notificacio, String codiError) {
		RespostaAlta respostaAlta = client.alta(notificacio);
		assertNotNull(respostaAlta);
		System.out.println(">>> Reposta " + (respostaAlta.isError() ? "amb error: " + respostaAlta.getErrorDescripcio() : "Ok"));
		assertTrue(respostaAlta.isError());
		assertNotNull(respostaAlta.getErrorDescripcio());
		assertTrue(respostaAlta.getErrorDescripcio().startsWith("[" + codiError + "]"));
		assertEquals(NotificacioEstatEnum.PENDENT, respostaAlta.getEstat());
	}
	

}
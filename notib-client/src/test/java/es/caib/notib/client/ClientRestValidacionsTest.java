/**
 * 
 */
package es.caib.notib.client;

import es.caib.notib.client.domini.DocumentV2;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.EnviamentV2;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
import es.caib.notib.client.domini.NotificacioEstatEnum;
import es.caib.notib.client.domini.NotificacioV2;
import es.caib.notib.client.domini.PersonaV2;
import es.caib.notib.client.domini.RespostaAlta;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test per al client REST del servei de notificacions de NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientRestValidacionsTest extends ClientBaseTest {


	private static final String URL = "http://localhost:8080/notibapi";
	private static final String USERNAME = "u990000";
	private static final String PASSWORD = "u999000";

//	private static final String URL = "https://dev.caib.es/notib2api";
//	private static final String USERNAME = "";
//	private static final String PASSWORD = "";

	/*
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	*/

	private NotificacioRestClient client;

	@Before
	public void setUp() throws IOException, DecoderException, DatatypeConfigurationException {
		client = NotificacioRestClientFactory.getRestClient(URL, USERNAME, PASSWORD);
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
		notificacio.setEmisorDir3Codi(ENTITAT_DESACTIVADA);
		enviaNotificacioError(notificacio, "1011");
	}

	@Test
	public void test1012() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setEmisorDir3Codi(ENTITAT_ERROR);
		enviaNotificacioError(notificacio, "1012");
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
	public void test1022() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setEnviamentTipus(EnviamentTipus.NOTIFICACIO);
		notificacio.setProcedimentCodi("1");
//		notificacio.setOrganGestor(null);
		enviaNotificacioError(notificacio, "1022");
	}
	@Test
	public void test1023() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setProcedimentCodi("875082");
		notificacio.setOrganGestor(null);
		enviaNotificacioError(notificacio, "1023");
	}

	@Test
	public void test1024() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setEnviamentTipus(EnviamentTipus.NOTIFICACIO);
		notificacio.setProcedimentCodi("879066");
		enviaNotificacioError(notificacio, "1024");
	}

	@Test
	public void test1025() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setEnviamentTipus(EnviamentTipus.COMUNICACIO);
		notificacio.setProcedimentCodi(null);
		notificacio.setOrganGestor(null);
		enviaNotificacioError(notificacio, "1025");
	}

	@Test
	public void test1026() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setEnviamentTipus(EnviamentTipus.COMUNICACIO);
		notificacio.setProcedimentCodi(null);
		notificacio.setOrganGestor("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		enviaNotificacioError(notificacio, "1026");
	}

	@Test
	public void test1027() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setEnviamentTipus(EnviamentTipus.COMUNICACIO);
		notificacio.setProcedimentCodi(null);
		notificacio.setOrganGestor("aaa");
		enviaNotificacioError(notificacio, "1027");
	}

	@Test
	public void test1028() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setEnviamentTipus(EnviamentTipus.COMUNICACIO);
		notificacio.setOrganGestor("aaaaaaaaaaaaaaaaaaaaaaaaaa");
		enviaNotificacioError(notificacio, "1028");
	}

	@Test
	public void test1029() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setEnviamentTipus(EnviamentTipus.SIR);
		PersonaV2 titular = notificacio.getEnviaments().get(0).getTitular();
		titular.setInteressatTipus(InteressatTipus.ADMINISTRACIO);
		titular.setDir3Codi("LA0001886");
		notificacio.setProcedimentCodi("894547");
		notificacio.setOrganGestor("A04003003");
		enviaNotificacioError(notificacio, "1029");
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
	public void test1042() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setDescripcio("Descripció\ni algo més");
		enviaNotificacioError(notificacio, "1042");
	}

	@Test
	public void test1050() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setEnviamentTipus(null);
		enviaNotificacioError(notificacio, "1050");
	}

	@Test
	public void test1051() throws DatatypeConfigurationException, IOException, DecoderException, ParseException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date date = format.parse("26/10/2022");
		notificacio.setEnviamentDataProgramada(date);
		enviaNotificacioError(notificacio, "1051");
	}

	@Test
	public void test1052() throws DatatypeConfigurationException, IOException, DecoderException, ParseException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date date = format.parse("26/10/2022");
		notificacio.setCaducitat(date);
		enviaNotificacioError(notificacio, "1052");
	}

	@Test
	public void test1053() throws DatatypeConfigurationException, IOException, DecoderException, ParseException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date date = format.parse("26/10/2024");
		notificacio.setCaducitat(date);
		notificacio.setEnviamentDataProgramada(format.parse("27/10/2024"));
		enviaNotificacioError(notificacio, "1053");
	}

	@Test
	public void test1055() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setNumExpedient("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		enviaNotificacioError(notificacio, "1055");
	}

	@Test
	public void test1060() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setUsuariCodi(null);
		enviaNotificacioError(notificacio, "1060");
	}

	@Test
	public void test1061() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setUsuariCodi("usuariCodiusuariCodiusuariCodiusuariCodiusuariCodiusuariCodiusuariCodiusuariCodiusuariCodiusuariCodiusuariCodiusuariCodi");
		enviaNotificacioError(notificacio, "1061");
	}

	@Test
	public void test1062() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setUsuariCodi("notib-test");
		enviaNotificacioError(notificacio, "1062");
	}

//	 @Test
	// A veces da timeout en DEV. Se usa un archivo de pruebas de 11 MB, justo por encima del máximo.
	public void test1065() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjuntGrande());
		String arxiuB64 = Base64.encodeBase64String(arxiuBytes);
		notificacio.getDocument().setContingutBase64(arxiuB64);
		enviaNotificacioError(notificacio, "1065");
	}

	// @Test
	// No se puede realizar este test por falta de un CSV de antigua custodia sin metadatos
//	public void test1066() throws DatatypeConfigurationException, IOException, DecoderException {
//		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
//		notificacio.getDocument().setContingutBase64(null);
//		notificacio.getDocument().setCsv("csvSinMetadatos");
//		notificacio.getDocument().setValidesa(null);
//		enviaNotificacioError(notificacio, "1066");
//	}

	@Test
	public void test1070() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setDocument(null);
		enviaNotificacioError(notificacio, "1070");
	}

	@Test
	public void test1071() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getDocument().setArxiuNom(null);
		enviaNotificacioError(notificacio, "1071");
	}

	@Test
	public void test1072() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getDocument().setArxiuNom("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz"+
				"ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXY" +
				"ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXY" +
				"ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJK");
		enviaNotificacioError(notificacio, "1072");
	}

	@Test
	public void test1073() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getDocument().setContingutBase64(null);
		enviaNotificacioError(notificacio, "1073");
	}

	@Test
	public void test1074() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getDocument().setCsv("csv");
		enviaNotificacioError(notificacio, "1074");
	}

	@Test
	public void test1075() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjuntTxt());
		String arxiuB64 = Base64.encodeBase64String(arxiuBytes);
		notificacio.getDocument().setContingutBase64(arxiuB64);
		enviaNotificacioError(notificacio, "1075");
	}

	@Test
	public void test1076() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjuntZip());
		notificacio.setEnviamentTipus(EnviamentTipus.SIR);
		notificacio.getEnviaments().get(0).getTitular().setInteressatTipus(InteressatTipus.ADMINISTRACIO);
		String arxiuB64 = Base64.encodeBase64String(arxiuBytes);
		notificacio.getDocument().setContingutBase64(arxiuB64);
		enviaNotificacioError(notificacio, "1076");
	}

	@Test
	public void test1077() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getDocument().setContingutBase64(null);
		notificacio.getDocument().setCsv("csvInexistente");
		enviaNotificacioError(notificacio, "1077");
	}

	@Test
	public void test1078() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		// Falta saber document que no tingui metadades
		enviaNotificacioError(notificacio, "1078");
	}

	@Test
	public void test1079() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		// Falta saber document que tingui firma valida
		enviaNotificacioError(notificacio, "1079");
	}

	@Test
	public void test1080() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getDocument().setOrigen(null);
		enviaNotificacioError(notificacio, "1080");
	}

	// del 1080 al 1083 no pot donar error ja que s'assignen les per defecte si venen null

	@Test
	public void test1087() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		DocumentV2 document2 = new DocumentV2();
		document2.setContingutBase64(notificacio.getDocument().getContingutBase64());
		notificacio.setDocument2(document2);
		enviaNotificacioError(notificacio, "1087");
	}

	@Test
	public void test1090() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setGrupCodi("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXY");
		enviaNotificacioError(notificacio, "1090");
	}

	@Test
	public void test1091() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setGrupCodi("testtest");
		enviaNotificacioError(notificacio, "1091");
	}

	@Test
	public void test1092() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setGrupCodi("prova");
		notificacio.setOrganGestor("A04027061");
		notificacio.setProcedimentCodi("1778910");
		enviaNotificacioError(notificacio, "1092");
	}

	@Test
	public void test1093() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setGrupCodi("serveiconcret");
		notificacio.setProcedimentCodi("888732");
		enviaNotificacioError(notificacio, "1093");
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
	public void test102() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setNif(null);
		enviaNotificacioError(notificacio, "1102");
	}

	@Test
	public void test103() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).setEntregaPostalActiva(true);
		enviaNotificacioError(notificacio, "1103");
	}

	@Test
	public void test104() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).setEntregaDehActiva(true);
		enviaNotificacioError(notificacio, "1104");
	}

	@Test
	public void test105() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 2, false);
		List<EnviamentV2> enviaments = notificacio.getEnviaments();
		PersonaV2 titular = enviaments.get(1).getTitular();
		titular.setNif(enviaments.get(0).getTitular().getNif());
		titular.setInteressatTipus(InteressatTipus.FISICA);
		enviaNotificacioError(notificacio, "1105");
	}

	@Test
	public void test106() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 2, false);
		notificacio.setEnviamentTipus(EnviamentTipus.SIR);
		List<EnviamentV2> enviaments = notificacio.getEnviaments();
		PersonaV2 titular = enviaments.get(1).getTitular();
		titular.setNif(enviaments.get(0).getTitular().getNif());
		titular.setInteressatTipus(InteressatTipus.FISICA);
		enviaNotificacioError(notificacio, "1106");
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
		notificacio.getEnviaments().get(0).getTitular().setIncapacitat(true);
		enviaNotificacioError(notificacio, "1111");
	}

	@Test
	public void test1112() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		PersonaV2 titular = notificacio.getEnviaments().get(0).getTitular();
		titular.setInteressatTipus(InteressatTipus.FISICA_SENSE_NIF);
		titular.setEmail(null);
		enviaNotificacioError(notificacio, "1112");
	}

//	@Test
	public void test1113() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setLlinatge1("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz");
		enviaNotificacioError(notificacio, "1113");
	}

//	@Test
	public void test1114() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setLlinatge2("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz");
		enviaNotificacioError(notificacio, "1114");
	}

//	@Test
	public void test1115() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setNif("0123456789X");
		enviaNotificacioError(notificacio, "1115");
	}

//	@Test
	public void test1116() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setNif("12345678A");
		enviaNotificacioError(notificacio, "1116");
	}

//	@Test
	public void test1117() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setEmail("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz");
		enviaNotificacioError(notificacio, "1117");
	}

//	@Test
	public void test1118() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setEmail("emailConFormatoIncorrecto");
		enviaNotificacioError(notificacio, "1118");
	}

//	@Test
	public void test1119() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setTelefon("12345678901234567890");
		enviaNotificacioError(notificacio, "1119");
	}

	@Test
	public void test1120() throws DatatypeConfigurationException, IOException, DecoderException {

		NUM_DESTINATARIS = 2;
		ENVIAMENT_AMB_DESTINATARIS = true;
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		enviaNotificacioError(notificacio, "1120");
	}

	@Test
	public void test1121() throws DatatypeConfigurationException, IOException, DecoderException {

		ENVIAMENT_AMB_DESTINATARIS = true;
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setInteressatTipus(InteressatTipus.FISICA_SENSE_NIF);
		enviaNotificacioError(notificacio, "1121");
	}

//	@Test
	public void test1122() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setIncapacitat(true);
		notificacio.getEnviaments().get(0).getDestinataris().clear();
		enviaNotificacioError(notificacio, "1122");
	}

	@Test
	public void test1130() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setInteressatTipus(null);
		enviaNotificacioError(notificacio, "1130");
	}

	@Test
	public void test1131() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setInteressatTipus(InteressatTipus.FISICA);
		notificacio.getEnviaments().get(0).getTitular().setNom(null);
		enviaNotificacioError(notificacio, "1131");
	}

	@Test
	public void test1132() throws DatatypeConfigurationException, IOException, DecoderException {
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setInteressatTipus(InteressatTipus.FISICA);
		notificacio.getEnviaments().get(0).getTitular().setNom("nomnomnomnomnomnomnomnomnomnomnom");
		enviaNotificacioError(notificacio, "1132");
	}

	@Test
	public void test1133() throws DatatypeConfigurationException, IOException, DecoderException {

		ENVIAMENT_AMB_DESTINATARIS = true;
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setInteressatTipus(InteressatTipus.FISICA);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setLlinatge1(null);
		enviaNotificacioError(notificacio, "1133");
	}

	@Test
	public void test1134() throws DatatypeConfigurationException, IOException, DecoderException {

		ENVIAMENT_AMB_DESTINATARIS = true;
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setInteressatTipus(InteressatTipus.FISICA);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setLlinatge1("llinatgellinatgellinatgellinatgellinatgellinatgellinatgellinatgellinatge");
		enviaNotificacioError(notificacio, "1134");
	}

	@Test
	public void test1135() throws DatatypeConfigurationException, IOException, DecoderException {

		ENVIAMENT_AMB_DESTINATARIS = true;
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setInteressatTipus(InteressatTipus.FISICA);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setLlinatge2("llinatgellinatgellinatgellinatgellinatgellinatgellinatgellinatgellinatge");
		enviaNotificacioError(notificacio, "1135");
	}

	@Test
	public void test1136() throws DatatypeConfigurationException, IOException, DecoderException {

		ENVIAMENT_AMB_DESTINATARIS = true;
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setNif(null);
		enviaNotificacioError(notificacio, "1136");
	}

	@Test
	public void test1137() throws DatatypeConfigurationException, IOException, DecoderException {

		ENVIAMENT_AMB_DESTINATARIS = true;
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setNif("1234567890");
		enviaNotificacioError(notificacio, "1137");
	}

	@Test
	public void test1138() throws DatatypeConfigurationException, IOException, DecoderException {

		ENVIAMENT_AMB_DESTINATARIS = true;
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setNif("12345678");
		enviaNotificacioError(notificacio, "1138");
	}

	@Test
	public void test1138_fisica() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setInteressatTipus(InteressatTipus.FISICA);
		notificacio.getEnviaments().get(0).getTitular().setNif("W2460343C"); //CIF
		enviaNotificacioError(notificacio, "1138");
	}

	@Test
	public void test1138_juridica() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setInteressatTipus(InteressatTipus.JURIDICA);
		notificacio.getEnviaments().get(0).getTitular().setNif("58848076T"); //NIF o NIE
		enviaNotificacioError(notificacio, "1138");
	}

//	@Test
	public void test1139() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setEmail("1234567812345678123456781234567812345678123456781234567812345678123456781234567812345678@limit.es");
		enviaNotificacioError(notificacio, "1139");
	}

	@Test
	public void test1140() throws DatatypeConfigurationException, IOException, DecoderException {


		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setEmail("email");
		enviaNotificacioError(notificacio, "1140");
	}

	@Test
	public void test1141() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setTelefon("1111111111111111111");
		enviaNotificacioError(notificacio, "1141");
	}

	@Test
	public void test1142() throws DatatypeConfigurationException, IOException, DecoderException {

		ENVIAMENT_AMB_DESTINATARIS = true;
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setInteressatTipus(InteressatTipus.JURIDICA);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setNif("F31513518");
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setRaoSocial(null);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setNom(null);
		enviaNotificacioError(notificacio, "1142");
	}

	@Test
	public void test1143() throws DatatypeConfigurationException, IOException, DecoderException {

		ENVIAMENT_AMB_DESTINATARIS = true;
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setInteressatTipus(InteressatTipus.JURIDICA);
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setNif("F31513518");
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setRaoSocial("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyzaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		notificacio.getEnviaments().get(0).getDestinataris().get(0).setNom(null);
		enviaNotificacioError(notificacio, "1143");
	}

	@Test
	public void test1144() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setInteressatTipus(InteressatTipus.ADMINISTRACIO);
		notificacio.getEnviaments().get(0).getTitular().setNif("F31513518");
		notificacio.getEnviaments().get(0).getTitular().setDir3Codi(null);
		notificacio.getEnviaments().get(0).getTitular().setNom(null);
		enviaNotificacioError(notificacio, "1144");
	}

	@Test
	public void test1145() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setInteressatTipus(InteressatTipus.ADMINISTRACIO);
		notificacio.getEnviaments().get(0).getTitular().setNif("F31513518");
		notificacio.getEnviaments().get(0).getTitular().setDir3Codi("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		notificacio.getEnviaments().get(0).getTitular().setNom(null);
		enviaNotificacioError(notificacio, "1144");
	}

	@Test
	public void test1146() throws DatatypeConfigurationException, IOException, DecoderException {

		ENVIAMENT_AMB_DESTINATARIS = true;
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setInteressatTipus(InteressatTipus.ADMINISTRACIO);
		notificacio.getEnviaments().get(0).getTitular().setNif("F31513518");
		notificacio.getEnviaments().get(0).getTitular().setDir3Codi("aaaaa");
		notificacio.getEnviaments().get(0).getTitular().setNom(null);
		enviaNotificacioError(notificacio, "1146");
	}

	@Test
	public void test1147() throws DatatypeConfigurationException, IOException, DecoderException {

		ENVIAMENT_AMB_DESTINATARIS = true;
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.setEnviamentTipus(EnviamentTipus.SIR);
		notificacio.getEnviaments().get(0).getTitular().setInteressatTipus(InteressatTipus.ADMINISTRACIO);
		notificacio.getEnviaments().get(0).getTitular().setNif("F31513518");
		notificacio.getEnviaments().get(0).getTitular().setDir3Codi("LA0014870");
		enviaNotificacioError(notificacio, "1147");
	}

	@Test
	public void test1148() throws DatatypeConfigurationException, IOException, DecoderException {

		ENVIAMENT_AMB_DESTINATARIS = true;
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).getTitular().setInteressatTipus(InteressatTipus.ADMINISTRACIO);
		notificacio.getEnviaments().get(0).getTitular().setNif("F31513518");
		notificacio.getEnviaments().get(0).getTitular().setDir3Codi("A04035957");
		notificacio.getEnviaments().get(0).getTitular().setNom(null);
		enviaNotificacioError(notificacio, "1147");
	}

	@Test
	public void test1150() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).setEntregaDehActiva(true);
		notificacio.getEnviaments().get(0).setEntregaDeh(null);
		enviaNotificacioError(notificacio, "1150");
	}

	@Test
	public void test1151() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, false);
		notificacio.getEnviaments().get(0).setEntregaDehActiva(true);
		notificacio.getEnviaments().get(0).getTitular().setNif(null);
		enviaNotificacioError(notificacio, "1151");
	}

	@Test
	public void test1155() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).setEntregaPostalActiva(true);
		notificacio.getEnviaments().get(0).setEntregaPostal(null);
		enviaNotificacioError(notificacio, "1155");
	}

	@Test
	public void test1160() throws DatatypeConfigurationException, IOException, DecoderException {

		// El servidor ha d'estar configurat amb: es.caib.notib.destinatari.multiple=false
		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		EnviamentV2 enviament = notificacio.getEnviaments().get(0);
		enviament.setEntregaPostalActiva(true);
		enviament.getEntregaPostal().setTipus(null);
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1160");
	}

	@Test
	public void test1161() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setCodiPostal(null);
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1161");
	}

	@Test
	public void test1162() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setCodiPostal("01234567890");
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1162");
	}

	@Test
	public void test1163() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setViaTipus(null);
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1163");
	}

	@Test
	public void test1164() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setViaNom(null);
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1164");
	}

	@Test
	public void test1165() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setViaNom("ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz");
        notificacio.setOrganGestor(ORGAN_CODI_CIE);
        notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1165");
	}

	@Test
	public void test1166() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setNumeroCasa(null);
		notificacio.getEnviaments().get(0).getEntregaPostal().setPuntKm(null);
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1166");
	}

	@Test
	public void test1167() throws DatatypeConfigurationException, IOException, DecoderException {

        NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setNumeroCasa("123456");
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1167");
	}


	@Test
	public void test1168() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setPuntKm("1168312");
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1168");
	}

	@Test
	public void test1169() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setPortal("1234");
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1169");
	}

	@Test
	public void test1170() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		EnviamentV2 enviament = notificacio.getEnviaments().get(0);
		enviament.setEntregaPostalActiva(true);
		enviament.getEntregaPostal().setPorta("2222");
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1170");
	}

	@Test
	public void test1171() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		EnviamentV2 enviament = notificacio.getEnviaments().get(0);
		enviament.setEntregaPostalActiva(true);
		enviament.getEntregaPostal().setEscala("2222");
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1171");
	}

	@Test
	public void test1172() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		EnviamentV2 enviament = notificacio.getEnviaments().get(0);
		enviament.setEntregaPostalActiva(true);
		enviament.getEntregaPostal().setPlanta("2222");
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1172");
	}

	@Test
	public void test1173() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		EnviamentV2 enviament = notificacio.getEnviaments().get(0);
		enviament.setEntregaPostalActiva(true);
		enviament.getEntregaPostal().setBloc("2222");
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1173");
	}

	@Test
	public void test1174() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		EnviamentV2 enviament = notificacio.getEnviaments().get(0);
		enviament.setEntregaPostalActiva(true);
		enviament.getEntregaPostal().setComplement("2222222222222222222222222222222222222222222222222222222222222222222222222222");
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1174");
	}

	@Test
	public void test1175() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		EnviamentV2 enviament = notificacio.getEnviaments().get(0);
		enviament.setEntregaPostalActiva(true);
		enviament.getEntregaPostal().setNumeroQualificador("2222");
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1175");
	}

	@Test
	public void test1176() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		EnviamentV2 enviament = notificacio.getEnviaments().get(0);
		enviament.setEntregaPostalActiva(true);
		enviament.getEntregaPostal().setTipus(NotificaDomiciliConcretTipus.APARTAT_CORREUS);
		enviament.getEntregaPostal().setApartatCorreus(null);
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1176");
	}

	@Test
	public void test1177() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		EnviamentV2 enviament = notificacio.getEnviaments().get(0);
		enviament.setEntregaPostalActiva(true);
		enviament.getEntregaPostal().setTipus(NotificaDomiciliConcretTipus.APARTAT_CORREUS);
		enviament.getEntregaPostal().setApartatCorreus("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1177");
	}

	@Test
	public void test1178() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		EnviamentV2 enviament = notificacio.getEnviaments().get(0);
		enviament.setEntregaPostalActiva(true);
		enviament.getEntregaPostal().setMunicipiCodi(null);
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1178");
	}

	@Test
	public void test1179() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		EnviamentV2 enviament = notificacio.getEnviaments().get(0);
		enviament.setEntregaPostalActiva(true);
		enviament.getEntregaPostal().setMunicipiCodi("municipiCodimunicipiCodimunicipiCodimunicipiCodi");
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1179");
	}

	@Test
	public void test1180() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		EnviamentV2 enviament = notificacio.getEnviaments().get(0);
		enviament.setEntregaPostalActiva(true);
		enviament.getEntregaPostal().setTipus(NotificaDomiciliConcretTipus.NACIONAL);
		enviament.getEntregaPostal().setProvincia(null);
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1180");
	}

	@Test
	public void test1181() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		EnviamentV2 enviament = notificacio.getEnviaments().get(0);
		enviament.setEntregaPostalActiva(true);
		enviament.getEntregaPostal().setTipus(NotificaDomiciliConcretTipus.NACIONAL);
		enviament.getEntregaPostal().setProvincia("aaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1181");
	}

	@Test
	public void test1182() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		EnviamentV2 enviament = notificacio.getEnviaments().get(0);
		enviament.setEntregaPostalActiva(true);
		enviament.getEntregaPostal().setTipus(NotificaDomiciliConcretTipus.NACIONAL);
		enviament.getEntregaPostal().setPaisCodi(null);
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1182");
	}

	@Test
	public void test1183() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		EnviamentV2 enviament = notificacio.getEnviaments().get(0);
		enviament.setEntregaPostalActiva(true);
		enviament.getEntregaPostal().setTipus(NotificaDomiciliConcretTipus.NACIONAL);
		enviament.getEntregaPostal().setPaisCodi("aaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1183");
	}

	@Test
	public void test1184() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setPoblacio(null);
		EnviamentV2 enviament = notificacio.getEnviaments().get(0);
		enviament.setEntregaPostalActiva(true);
		enviament.getEntregaPostal().setTipus(NotificaDomiciliConcretTipus.NACIONAL);
		enviament.getEntregaPostal().setPoblacio(null);
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1184");
	}

	@Test
	public void test1185() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		notificacio.getEnviaments().get(0).getEntregaPostal().setPoblacio(null);
		EnviamentV2 enviament = notificacio.getEnviaments().get(0);
		enviament.setEntregaPostalActiva(true);
		enviament.getEntregaPostal().setTipus(NotificaDomiciliConcretTipus.NACIONAL);
		enviament.getEntregaPostal().setPoblacio("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1185");
	}

	@Test
	public void test1186() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		EnviamentV2 enviament = notificacio.getEnviaments().get(0);
		enviament.setEntregaPostalActiva(true);
		enviament.getEntregaPostal().setTipus(NotificaDomiciliConcretTipus.SENSE_NORMALITZAR);
		enviament.getEntregaPostal().setLinea1(null);
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1186");
	}

	@Test
	public void test1187() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		EnviamentV2 enviament = notificacio.getEnviaments().get(0);
		enviament.setEntregaPostalActiva(true);
		enviament.getEntregaPostal().setTipus(NotificaDomiciliConcretTipus.SENSE_NORMALITZAR);
		enviament.getEntregaPostal().setLinea1("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1187");
	}

	@Test
	public void test1188() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		EnviamentV2 enviament = notificacio.getEnviaments().get(0);
		enviament.setEntregaPostalActiva(true);
		enviament.getEntregaPostal().setTipus(NotificaDomiciliConcretTipus.SENSE_NORMALITZAR);
		enviament.getEntregaPostal().setLinea1(null);
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1188");
	}

	@Test
	public void test1189() throws DatatypeConfigurationException, IOException, DecoderException {

		NotificacioV2 notificacio = generarNotificacioV2(new Long(System.currentTimeMillis()).toString(), 1, true);
		EnviamentV2 enviament = notificacio.getEnviaments().get(0);
		enviament.setEntregaPostalActiva(true);
		enviament.getEntregaPostal().setTipus(NotificaDomiciliConcretTipus.SENSE_NORMALITZAR);
		enviament.getEntregaPostal().setLinea2("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		notificacio.setOrganGestor(ORGAN_CODI_CIE);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT_CIE);
		enviaNotificacioError(notificacio, "1189");
	}

	private void enviaNotificacioError(NotificacioV2 notificacio, String codiError) {

		RespostaAlta respostaAlta = client.alta(notificacio);
		assertNotNull(respostaAlta);
		System.out.println(">>> Reposta " + (respostaAlta.isError() ? "amb error: " + respostaAlta.getErrorDescripcio() : "Ok"));
		assertTrue(respostaAlta.isError());
		assertNotNull(respostaAlta.getErrorDescripcio());
		assertTrue(respostaAlta.getErrorDescripcio().contains("[" + codiError + "]"));
		assertEquals(NotificacioEstatEnum.PENDENT, respostaAlta.getEstat());
	}


}
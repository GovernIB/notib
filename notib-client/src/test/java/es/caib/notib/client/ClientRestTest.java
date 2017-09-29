/**
 * 
 */
package es.caib.notib.client;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.CreateException;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.jboss.mx.util.MBeanProxyCreationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import es.caib.loginModule.client.AuthenticationFailureException;
import es.caib.notib.ws.notificacio.Document;
import es.caib.notib.ws.notificacio.EntregaDeh;
import es.caib.notib.ws.notificacio.EntregaPostal;
import es.caib.notib.ws.notificacio.EntregaPostalTipusEnum;
import es.caib.notib.ws.notificacio.EntregaPostalViaTipusEnum;
import es.caib.notib.ws.notificacio.Enviament;
import es.caib.notib.ws.notificacio.EnviamentEstatEnum;
import es.caib.notib.ws.notificacio.EnviamentTipusEnum;
import es.caib.notib.ws.notificacio.InformacioEnviament;
import es.caib.notib.ws.notificacio.Notificacio;
import es.caib.notib.ws.notificacio.NotificacioServiceWsException_Exception;
import es.caib.notib.ws.notificacio.PagadorCie;
import es.caib.notib.ws.notificacio.PagadorPostal;
import es.caib.notib.ws.notificacio.ParametresSeu;
import es.caib.notib.ws.notificacio.Persona;
import es.caib.notib.ws.notificacio.ServeiTipusEnum;

/**
 * Test per al client REST del servei de notificacions de NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientRestTest {

	private static final String ENTITAT_DIR3CODI = "A04013511";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private NotificacioRestClient client;

	@Before
	public void setUp() throws IOException, DecoderException {
		client = NotificacioRestClientFactory.getRestClient(
				"http://localhost:8080/notib",
				"notapp",
				"notapp");
	}

	@Test
	public void test() throws InstanceNotFoundException, MalformedObjectNameException, MBeanProxyCreationException, NotificacioServiceWsException_Exception, NamingException, CreateException, AuthenticationFailureException, IOException, DecoderException, DatatypeConfigurationException {
		String notificacioId = new Long(System.currentTimeMillis()).toString();
		List<String> referencies = client.alta(
				generarNotificacio(
						notificacioId,
						1,
						true));
		assertNotNull(referencies);
		assertThat(
				referencies.size(),
				is(1));
		/*for (String referencia: referencies) {
			System.out.println(">>> Referencia enviament creat: " + referencia);
		}*/
		InformacioEnviament info = client.consulta(referencies.get(0));
		assertNotNull(info);
		assertThat(
				info.getEstat(),
				is(EnviamentEstatEnum.NOTIB_PENDENT));
	}



	private Notificacio generarNotificacio(
			String notificacioId,
			int numDestinataris,
			boolean ambEnviamentPostal) throws IOException, DecoderException, DatatypeConfigurationException {
		byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
		Notificacio notificacio = new Notificacio();
		notificacio.setEmisorDir3Codi(ENTITAT_DIR3CODI);
		notificacio.setEnviamentTipus(EnviamentTipusEnum.COMUNICACIO);
		notificacio.setConcepte(
				"concepte_" + notificacioId);
		notificacio.setDescripcio(
				"descripcio_" + notificacioId);
		notificacio.setEnviamentDataProgramada(null);
		notificacio.setRetard(5);
		notificacio.setCaducitat(
				toXmlGregorianCalendar(
						new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000)));
		Document document = new Document();
		document.setArxiuNom("documentArxiuNom_" + notificacioId + ".pdf");
		document.setContingutBase64(Base64.encodeBase64String(arxiuBytes));
		document.setHash(
				Base64.encodeBase64String(
						Hex.decodeHex(
								DigestUtils.sha1Hex(arxiuBytes).toCharArray())));
		document.setNormalitzat(false);
		document.setGenerarCsv(false);
		notificacio.setDocument(document);
		notificacio.setProcedimentCodi("0000");
		if (ambEnviamentPostal) {
			PagadorPostal pagadorPostal = new PagadorPostal();
			pagadorPostal.setDir3Codi("A04013511");
			pagadorPostal.setFacturacioClientCodi("ccFac_" + notificacioId);
			pagadorPostal.setContracteNum("pccNum_" + notificacioId);
			pagadorPostal.setContracteDataVigencia(
					toXmlGregorianCalendar(new Date(0)));
			notificacio.setPagadorPostal(pagadorPostal);
			PagadorCie pagadorCie = new PagadorCie();
			pagadorCie.setDir3Codi("A04013511");
			pagadorCie.setContracteDataVigencia(
					toXmlGregorianCalendar(new Date(0)));
			notificacio.setPagadorCie(pagadorCie);
		}
		for (int i = 0; i < numDestinataris; i++) {
			Enviament enviament = new Enviament();
			Persona titular = new Persona();
			titular.setNom("titularNom" + i);
			titular.setLlinatge1("titLlinatge1_" + i);
			titular.setLlinatge2("titLlinatge2_" + i);
			titular.setNif("00000000T");
			titular.setTelefon("666010101");
			titular.setEmail("titular@gmail.com");
			enviament.setTitular(titular);
			Persona destinatari = new Persona();
			destinatari.setNom("destinatariNom" + i);
			destinatari.setLlinatge1("destLlinatge1_" + i);
			destinatari.setLlinatge2("destLlinatge2_" + i);
			destinatari.setNif("12345678Z");
			destinatari.setTelefon("666020202");
			destinatari.setEmail("destinatari@gmail.com");
			enviament.getDestinataris().add(destinatari);
			if (ambEnviamentPostal) {
				EntregaPostal entregaPostal = new EntregaPostal();
				entregaPostal.setTipus(EntregaPostalTipusEnum.NACIONAL);
				entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.CALLE);
				entregaPostal.setViaNom("Bas");
				entregaPostal.setNumeroCasa("25");
				entregaPostal.setNumeroQualificador("bis");
				entregaPostal.setPuntKm("pk01");
				entregaPostal.setApartatCorreus("0228");
				entregaPostal.setPortal("portal" + i);
				entregaPostal.setEscala("escala" + i);
				entregaPostal.setPlanta("planta" + i);
				entregaPostal.setPorta("porta" + i);
				entregaPostal.setBloc("bloc" + i);
				entregaPostal.setComplement("complement" + i);
				entregaPostal.setCodiPostal("07500");
				entregaPostal.setPoblacio("poblacio" + i);
				entregaPostal.setMunicipiCodi("07033");
				entregaPostal.setProvinciaCodi("07");
				entregaPostal.setPaisCodi("ES");
				entregaPostal.setLinea1("linea1_" + i);
				entregaPostal.setLinea2("linea2_" + i);
				entregaPostal.setCie(new Integer(8));
			}
			EntregaDeh entregaDeh = new EntregaDeh();
			entregaDeh.setObligat(true);
			entregaDeh.setProcedimentCodi("0000");
			enviament.setEntregaDeh(entregaDeh);
			enviament.setServeiTipus(ServeiTipusEnum.URGENT);
			notificacio.getEnviaments().add(enviament);
		}
		ParametresSeu parametresSeu = new ParametresSeu();
		parametresSeu.setExpedientSerieDocumental(
				"0000S");
		parametresSeu.setExpedientUnitatOrganitzativa(
				"00000000T");
		parametresSeu.setExpedientIdentificadorEni(
				"seuExpedientIdentificadorEni_" + notificacioId);
		parametresSeu.setExpedientTitol(
				"seuExpedientTitol_" + notificacioId);
		parametresSeu.setRegistreOficina(
				"seuRegistreOficina_" + notificacioId);
		parametresSeu.setRegistreLlibre(
				"seuRegistreLlibre_" + notificacioId);
		parametresSeu.setIdioma(
				"seuIdioma_" + notificacioId);
		parametresSeu.setAvisTitol(
				"seuAvisTitol_" + notificacioId);
		parametresSeu.setAvisText(
				"seuAvisText_" + notificacioId);
		parametresSeu.setAvisTextMobil(
				"seuAvisTextMobil_" + notificacioId);
		parametresSeu.setOficiTitol(
				"seuOficiTitol_" + notificacioId);
		parametresSeu.setOficiText(
				"seuOficiText_" + notificacioId);
		notificacio.setParametresSeu(parametresSeu);
		return notificacio;
	}

	private InputStream getContingutNotificacioAdjunt() {
		return getClass().getResourceAsStream(
				"/es/caib/notib/client/notificacio_adjunt.pdf");
	}

	private XMLGregorianCalendar toXmlGregorianCalendar(Date date) throws DatatypeConfigurationException {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
	}

}

/**
 * 
 */
package es.caib.notib.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import es.caib.notib.ws.notificacio.Document;
import es.caib.notib.ws.notificacio.EntregaDeh;
import es.caib.notib.ws.notificacio.EntregaPostal;
import es.caib.notib.ws.notificacio.EntregaPostalTipusEnum;
import es.caib.notib.ws.notificacio.EntregaPostalViaTipusEnum;
import es.caib.notib.ws.notificacio.Enviament;
import es.caib.notib.ws.notificacio.EnviamentTipusEnum;
import es.caib.notib.ws.notificacio.Notificacio;
import es.caib.notib.ws.notificacio.PagadorCie;
import es.caib.notib.ws.notificacio.PagadorPostal;
import es.caib.notib.ws.notificacio.ParametresSeu;
import es.caib.notib.ws.notificacio.Persona;
import es.caib.notib.ws.notificacio.ServeiTipusEnum;

/**
 * Base per als tests del servei de notificacions de NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientBaseTest {

//	Entitat: A04013511 (DGTIC) ò A04003003 (Govern)
	protected static final String ENTITAT_DIR3CODI = "A04013511";
//	protected static final String ORGAN_CODI = "A04013501";
	protected static final String ORGAN_CODI = "A04013511";
	protected static final String LLIBRE_OFICINA = "L99.O00009390";
	protected static final String UNITAT_ADMINISTRATIVA_SISTRA = "1";
	protected static final String IDENTIFICADOR_PROCEDIMENT = "846823";
	protected static final String IDIOMA = "ca";

	protected Notificacio generarNotificacio(
			String notificacioId,
			int numDestinataris,
			boolean ambEnviamentPostal) throws DatatypeConfigurationException, IOException, DecoderException {
		byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
		Notificacio notificacio = new Notificacio();
		notificacio.setEmisorDir3Codi(ENTITAT_DIR3CODI);
		notificacio.setEnviamentTipus(EnviamentTipusEnum.NOTIFICACIO);
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
		
		String arxiuB64 = Base64.encodeBase64String(arxiuBytes);
		
//		System.out.println("Hash: " + new String(DigestUtils.sha256(arxiuBytes)));
//		System.out.println("Hash: " + new String(DigestUtils.sha256(arxiuB64)));
//		System.out.println("Hash: " + Base64.encodeBase64String(DigestUtils.sha256(arxiuBytes)));
//		System.out.println("Hash: " + Base64.encodeBase64String(DigestUtils.sha256(arxiuB64)));
		
		document.setContingutBase64(arxiuB64);
		document.setHash(
				Base64.encodeBase64String(
						Hex.decodeHex(
								DigestUtils.sha256Hex(arxiuBytes).toCharArray())));
		document.setNormalitzat(false);
		document.setGenerarCsv(false);
		notificacio.setDocument(document);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT);
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
			titular.setNom("Siòn");
			titular.setLlinatge1("Andreu");
			titular.setLlinatge2("Nadal");
			titular.setNif("00000000T");
			titular.setTelefon("666010101");
			titular.setEmail("sandreu@limit.es");
			enviament.setTitular(titular);
			Persona destinatari = new Persona();
			destinatari.setNom("melcior");
			destinatari.setLlinatge1("Andreu");
			destinatari.setLlinatge2("Nadal");
			destinatari.setNif("18225486x");
			destinatari.setTelefon("666020202");
			destinatari.setEmail("sandreu@limit.es");
			enviament.getDestinataris().add(destinatari);
			if (ambEnviamentPostal) {
				EntregaPostal entregaPostal = new EntregaPostal();
				entregaPostal.setTipus(EntregaPostalTipusEnum.NACIONAL);
				entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.CALLE);
				entregaPostal.setViaNom("Bas");
				entregaPostal.setNumeroCasa("25");
				entregaPostal.setNumeroQualificador("bis");
				//entregaPostal.setApartatCorreus("0228");
				entregaPostal.setPortal("pt" + i);
				entregaPostal.setEscala("es" + i);
				entregaPostal.setPlanta("pl" + i);
				entregaPostal.setPorta("pr" + i);
				entregaPostal.setBloc("bl" + i);
				entregaPostal.setComplement("complement" + i);
				entregaPostal.setCodiPostal("07500");
				entregaPostal.setPoblacio("poblacio" + i);
				entregaPostal.setMunicipiCodi("070337");
				entregaPostal.setProvinciaCodi("07");
				entregaPostal.setPaisCodi("ES");
				entregaPostal.setLinea1("linea1_" + i);
				entregaPostal.setLinea2("linea2_" + i);
				entregaPostal.setCie(new Integer(0));
				enviament.setEntregaPostal(entregaPostal);
			}
			EntregaDeh entregaDeh = new EntregaDeh();
			entregaDeh.setObligat(true);
			entregaDeh.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT);
			enviament.setEntregaDeh(entregaDeh);
			enviament.setServeiTipus(ServeiTipusEnum.URGENT);
			notificacio.getEnviaments().add(enviament);
		}
		ParametresSeu parametresSeu = new ParametresSeu();
		parametresSeu.setExpedientSerieDocumental("0000S");
		parametresSeu.setExpedientUnitatOrganitzativa(UNITAT_ADMINISTRATIVA_SISTRA);
		parametresSeu.setExpedientIdentificadorEni("ES_" + ORGAN_CODI + "_2018_EXP_NOTIB" + "0000000000000000000000005");//+ String.format("%25s", notificacioId).replace(' ', '0'));
		parametresSeu.setExpedientTitol("seuExpedientTitol_" + notificacioId);
		parametresSeu.setRegistreOficina(ORGAN_CODI);
		parametresSeu.setRegistreLlibre(LLIBRE_OFICINA);
		parametresSeu.setIdioma(IDIOMA);
		parametresSeu.setAvisTitol("seuAvisTitol_" + notificacioId);
		parametresSeu.setAvisText("seuAvisText_" + notificacioId);
		parametresSeu.setAvisTextMobil("seuAvisTextMobil_" + notificacioId);
		parametresSeu.setOficiTitol("seuOficiTitol_" + notificacioId);
		parametresSeu.setOficiText("seuOficiText_" + notificacioId);
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
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
import org.apache.commons.io.IOUtils;

import es.caib.notib.ws.notificacio.DocumentV2;
import es.caib.notib.ws.notificacio.EntregaDeh;
import es.caib.notib.ws.notificacio.EntregaPostal;
import es.caib.notib.ws.notificacio.EntregaPostalViaTipusEnum;
import es.caib.notib.ws.notificacio.Enviament;
import es.caib.notib.ws.notificacio.EnviamentTipusEnum;
import es.caib.notib.ws.notificacio.InteressatTipusEnumDto;
import es.caib.notib.ws.notificacio.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.ws.notificacio.NotificaServeiTipusEnumDto;
import es.caib.notib.ws.notificacio.NotificacioV2;
import es.caib.notib.ws.notificacio.Persona;

/**
 * Base per als tests del servei de notificacions de NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientBaseTest {

//	Entitat: A04013511 (DGTIC) ò A04003003 (Govern)
	protected static final String ENTITAT_DIR3CODI = "A04003003";
//	protected static final String ENTITAT_DIR3CODI = "A04013511";
	protected static final String ORGAN_CODI = "A04003003";
	protected static final String LLIBRE = "L16";
	protected static final String OFICINA = "O00009390";
	protected static final String IDENTIFICADOR_PROCEDIMENT = "847185";
	protected static final String IDIOMA = "ca";
	protected static final String USUARI_CODI = "e18225486x";

	protected NotificacioV2 generarNotificacioV2(
			String notificacioId,
			int numDestinataris,
			boolean ambEnviamentPostal) throws DatatypeConfigurationException, IOException, DecoderException {
		byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
		NotificacioV2 notificacio = new NotificacioV2();
		notificacio.setEmisorDir3Codi(ENTITAT_DIR3CODI);
		notificacio.setEnviamentTipus(EnviamentTipusEnum.NOTIFICACIO);
		notificacio.setUsuariCodi(USUARI_CODI);
//		notificacio.setComunicacioTipus(ComunicacioTipusEnum.ASINCRON);
		notificacio.setConcepte("concepte_" + notificacioId);
		notificacio.setDescripcio("descripcio_" + notificacioId);
		notificacio.setEnviamentDataProgramada(null);
		notificacio.setRetard(5);
		notificacio.setCaducitat(toXmlGregorianCalendar(new Date(System.currentTimeMillis() + 12 * 24 * 3600 * 1000)));
		DocumentV2 document = new DocumentV2();
		document.setArxiuNom("documentArxiuNom_" + notificacioId + ".pdf");
		
		String arxiuB64 = Base64.encodeBase64String(arxiuBytes);
		document.setContingutBase64(arxiuB64);
//		document.setUuid("8f3e508c-d304-4502-bd45-2061b47d3eda");
		document.setNormalitzat(false);
//		document.setGenerarCsv(false);
		
		notificacio.setDocument(document);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT);
		for (int i = 0; i < numDestinataris; i++) {
			Enviament enviament = new Enviament();
			Persona titular = new Persona();
			titular.setNom("Siòn");
			titular.setLlinatge1("Andreu");
			titular.setLlinatge2("Nadal");
			titular.setNif("00000000T");
			titular.setTelefon("666010101");
			titular.setEmail("sandreu@limit.es");
			titular.setInteressatTipus(InteressatTipusEnumDto.FISICA);
			if (titular.getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO))
				titular.setDir3Codi(ENTITAT_DIR3CODI);
			enviament.setTitular(titular);
			Persona destinatari = new Persona();
			destinatari.setNom("melcior");
			destinatari.setLlinatge1("Andreu");
			destinatari.setLlinatge2("Nadal");
			destinatari.setNif("18225486x");
			destinatari.setTelefon("666020202");
			destinatari.setEmail("sandreu@limit.es");
			destinatari.setInteressatTipus(InteressatTipusEnumDto.FISICA);
			if (destinatari.getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO))
				destinatari.setDir3Codi(ENTITAT_DIR3CODI);
			enviament.getDestinataris().add(destinatari);
			if (ambEnviamentPostal) {
				EntregaPostal entregaPostal = new EntregaPostal();
				entregaPostal.setTipus(NotificaDomiciliConcretTipusEnumDto.NACIONAL);
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
				entregaPostal.setProvincia("07");
				entregaPostal.setPaisCodi("ES");
				entregaPostal.setLinea1("linea1_" + i);
				entregaPostal.setLinea2("linea2_" + i);
				entregaPostal.setCie(new Integer(0));
				enviament.setEntregaPostal(entregaPostal);
				enviament.setEntregaPostalActiva(true);
			}
			EntregaDeh entregaDeh = new EntregaDeh();
			entregaDeh.setObligat(true);
			entregaDeh.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT);
			enviament.setEntregaDeh(entregaDeh);
			enviament.setServeiTipus(NotificaServeiTipusEnumDto.URGENT);
			notificacio.getEnviaments().add(enviament);
		}
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
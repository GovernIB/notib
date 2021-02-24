/**
 * 
 */
package es.caib.notib.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

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
import es.caib.notib.ws.notificacio.OrigenEnum;
import es.caib.notib.ws.notificacio.Persona;
import es.caib.notib.ws.notificacio.TipusDocumentalEnum;
import es.caib.notib.ws.notificacio.ValidesaEnum;


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
	protected static final String IDENTIFICADOR_PROCEDIMENT = "2095292";
	protected static final String IDIOMA = "ca";
	protected static final String USUARI_CODI = "e18225486x";
//	protected static final NotificaDomiciliConcretTipusEnumDto TIPUS_ENTREGA_POSTAL = NotificaDomiciliConcretTipusEnumDto.NACIONAL;
	protected static final NotificaDomiciliConcretTipusEnumDto TIPUS_ENTREGA_POSTAL = NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR;
	
	protected NotificacioV2 generarNotificacioV2(
			String notificacioId,
			int numDestinataris,
			boolean ambEnviamentPostal) throws DatatypeConfigurationException, IOException, DecoderException {
		byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
		NotificacioV2 notificacio = new NotificacioV2();
		notificacio.setEmisorDir3Codi(ENTITAT_DIR3CODI);
		notificacio.setEnviamentTipus(EnviamentTipusEnum.COMUNICACIO);
		notificacio.setUsuariCodi(USUARI_CODI);
//		notificacio.setComunicacioTipus(ComunicacioTipusEnum.ASINCRON);
//		notificacio.setOrganGestor(ORGAN_CODI);
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
//		document.setUuid("fb341a96-2cbf-4ec8-b7dd-08a1817c4b32");
//		document.setCsv("asdfa");
//		document.setUrl("asdfas");
		document.setNormalitzat(false);
//		document.setGenerarCsv(false);
		
		notificacio.setDocument(document);
//		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT);
		for (int i = 0; i < numDestinataris; i++) {
			Enviament enviament = new Enviament();
			Persona titular = new Persona();
			titular.setNom("Siòn");
			titular.setLlinatge1("Andreu");
			titular.setLlinatge2("Nadal");
			titular.setNif("00000000T");
			titular.setTelefon("666010101");
			titular.setEmail("sandreu@limit.es");
			titular.setInteressatTipus(InteressatTipusEnumDto.ADMINISTRACIO);
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
			destinatari.setInteressatTipus(InteressatTipusEnumDto.ADMINISTRACIO);
			if (destinatari.getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO))
				destinatari.setDir3Codi(ENTITAT_DIR3CODI);
			enviament.getDestinataris().add(destinatari);
			if (ambEnviamentPostal) {
				EntregaPostal entregaPostal = new EntregaPostal();
				if (NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR.equals(TIPUS_ENTREGA_POSTAL)) {
					entregaPostal.setTipus(TIPUS_ENTREGA_POSTAL);
					entregaPostal.setLinea1("linea1_" + i);
					entregaPostal.setLinea2("linea2_" + i);
				} else {
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
				}
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
	
	protected List<DocumentV2> crearArxius(Integer numeroArxius, NotificacioV2 notificacioV2, String notificacioId, byte[] arxiuBytes) {
		List<DocumentV2>  documents = new ArrayList<>();
//		List<String> extensionsDisponibles = Arrays.asList(new String[] {"jpg", "jpeg", "odt", "odp", "ods", "odg", "docx", "xlsx", "pptx", "pdf", "png", "rtf", "svg", "tiff", "txt", "xml", "xsig", "csig", "html", "csv"});;
		if(numeroArxius >= 0 && numeroArxius < 5) {
			String formatFile ="pdf";
			for(int i = 0; i <= numeroArxius; i++){
//				if(notificacioV2.getEnviamentTipus() == EnviamentTipusEnum.COMUNICACIO ) {
//					formatFile= extensionsDisponibles.get(new Random().nextInt(extensionsDisponibles.size()));
//				}
				 
				DocumentV2 document = new DocumentV2();
				document.setArxiuNom("documentArxiuNom_["+i+"]" + notificacioId + "."+ formatFile);
				String arxiuB64 = Base64.encodeBase64String(arxiuBytes);
				document.setContingutBase64(arxiuB64);
				document.setNormalitzat(false);
				document.setModoFirma(false);
				document.setOrigen(OrigenEnum.ADMINISTRACIO);
				document.setValidesa(ValidesaEnum.ORIGINAL);
				document.setTipoDocumental(TipusDocumentalEnum.ALTRES);
				documents.add(document);
			}
		}
		
		return documents;
		
	}
	
	
	protected List<NotificacioV2> generarMultiplesNotificacioV2(
			Integer numeroDeNotificacions,
			int numDestinataris,
			boolean ambEnviamentPostal) throws DatatypeConfigurationException, IOException, DecoderException {
		
		List<Integer> repetits = new ArrayList();
		List<NotificacioV2> notificacions = new ArrayList();
		for(int i = 0; i < numeroDeNotificacions; i++){
			String notificacioId = generarRandomNoRepetit(repetits, numeroDeNotificacions).toString();
			repetits.add(Integer.valueOf(notificacioId));
			notificacioId = "CARGA_MASIVA_" +  notificacioId;
			
			NotificacioV2 notificacio = new NotificacioV2();
			notificacio.setEmisorDir3Codi(ENTITAT_DIR3CODI);
			notificacio.setEnviamentTipus(EnviamentTipusEnum.COMUNICACIO);
			notificacio.setUsuariCodi(USUARI_CODI);
//			notificacio.setComunicacioTipus(ComunicacioTipusEnum.ASINCRON);
			notificacio.setOrganGestor(ORGAN_CODI);
			
			int random = new Random().nextInt(4);
			if(random == 2) {
				notificacio.setConcepte("Error registre_" + notificacioId);
			}else {
				notificacio.setConcepte("concepte_" + notificacioId);
			}
			
			notificacio.setDescripcio("descripcio_" + notificacioId);
			notificacio.setEnviamentDataProgramada(null);
			notificacio.setRetard(5);
			notificacio.setCaducitat(toXmlGregorianCalendar(new Date(System.currentTimeMillis() + 12 * 24 * 3600 * 1000)));
			byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
			List<DocumentV2> documents = crearArxius(random, notificacio, notificacioId.toString(), arxiuBytes);
			
			
			switch (documents.size()) {
            case 1:
            	notificacio.setDocument(documents.get(0));
                break;
            case 2:
            	notificacio.setDocument(documents.get(0));
            	notificacio.setDocument2(documents.get(1));
                break;
            case 3:
            	notificacio.setDocument(documents.get(0));
            	notificacio.setDocument2(documents.get(1));
            	notificacio.setDocument3(documents.get(2));
                break;
            case 4:
            	notificacio.setDocument(documents.get(0));
            	notificacio.setDocument2(documents.get(1));
            	notificacio.setDocument3(documents.get(2));
            	notificacio.setDocument4(documents.get(3));
                break;
            case 5:
            	notificacio.setDocument(documents.get(0));
            	notificacio.setDocument2(documents.get(1));
            	notificacio.setDocument3(documents.get(2));
            	notificacio.setDocument4(documents.get(3));
            	notificacio.setDocument5(documents.get(4));
                break;
			}
			
			for (int h = 0; h < numDestinataris; h++) {
				Enviament enviament = new Enviament();
				Persona titular = new Persona();
				titular.setNom("Siòn");
				titular.setLlinatge1("Andreu");
				titular.setLlinatge2("Nadal");
				titular.setNif("00000000T");
				titular.setTelefon("666010101");
				titular.setEmail("sandreu@limit.es");
				titular.setInteressatTipus(InteressatTipusEnumDto.ADMINISTRACIO);
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
				destinatari.setInteressatTipus(InteressatTipusEnumDto.ADMINISTRACIO);
				if (destinatari.getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO))
					destinatari.setDir3Codi(ENTITAT_DIR3CODI);
				enviament.getDestinataris().add(destinatari);
				if (ambEnviamentPostal) {
					EntregaPostal entregaPostal = new EntregaPostal();
					if (NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR.equals(TIPUS_ENTREGA_POSTAL)) {
						entregaPostal.setTipus(TIPUS_ENTREGA_POSTAL);
						entregaPostal.setLinea1("linea1_" + h);
						entregaPostal.setLinea2("linea2_" + h);
					} else {
						entregaPostal.setTipus(NotificaDomiciliConcretTipusEnumDto.NACIONAL);
						entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.CALLE);
						entregaPostal.setViaNom("Bas");
						entregaPostal.setNumeroCasa("25");
						entregaPostal.setNumeroQualificador("bis");
						//entregaPostal.setApartatCorreus("0228");
						entregaPostal.setPortal("pt" + h);
						entregaPostal.setEscala("es" + h);
						entregaPostal.setPlanta("pl" + h);
						entregaPostal.setPorta("pr" + h);
						entregaPostal.setBloc("bl" + h);
						entregaPostal.setComplement("complement" + h);
						entregaPostal.setCodiPostal("07500");
						entregaPostal.setPoblacio("poblacio" + h);
						entregaPostal.setMunicipiCodi("070337");
						entregaPostal.setProvincia("07");
						entregaPostal.setPaisCodi("ES");
					}
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
			
			notificacions.add(notificacio);
		}
		
		return notificacions;
	}
	
	private Integer generarRandomNoRepetit(List<Integer> repetits, Integer numeroDeNotificacions) {
		 Integer index = new Random().nextInt(numeroDeNotificacions);
	      while(repetits.contains(index)) {
	    	  index = new Random().nextInt(numeroDeNotificacions);
	      }
		return index; 
	}

}
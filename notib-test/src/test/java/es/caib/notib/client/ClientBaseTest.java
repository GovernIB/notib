/**
 * 
 */
package es.caib.notib.client;

import es.caib.notib.client.domini.DocumentV2;
import es.caib.notib.client.domini.EntregaDeh;
import es.caib.notib.client.domini.EntregaPostal;
import es.caib.notib.client.domini.EntregaPostalViaTipusEnum;
import es.caib.notib.client.domini.Enviament;
import es.caib.notib.client.domini.EnviamentTipusEnum;
import es.caib.notib.client.domini.InteressatTipusEnumDto;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.client.domini.NotificaServeiTipusEnumDto;
import es.caib.notib.client.domini.NotificacioV2;
import es.caib.notib.client.domini.OrigenEnum;
import es.caib.notib.client.domini.Persona;
import es.caib.notib.client.domini.TipusDocumentalEnum;
import es.caib.notib.client.domini.ValidesaEnum;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;


/**
 * Base per als tests del servei de notificacions de NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientBaseTest {

	protected static final String ORGAN_SIR_CODI = "E03141701";
	protected static final String LLIBRE = "L16";
	protected static final String OFICINA = "O00009390";
	protected static final String IDIOMA = "ca";
	protected static final String USUARI_CODI = "e18225486x";
	protected static final NotificaDomiciliConcretTipusEnumDto TIPUS_ENTREGA_POSTAL = NotificaDomiciliConcretTipusEnumDto.NACIONAL;

	// LOCAL data
//	Entitat: A04013511 (DGTIC) ò A04003003 (Govern)
	protected static final String ENTITAT_DIR3CODI = "A04003003";
	protected static final String ORGAN_CODI = "A04035948";
	protected static final String ORGAN_CODI_CIE = "A04026958"; // LOCAL
	protected static final String IDENTIFICADOR_PROCEDIMENT = "2095292"; // LOCAL
	protected static final String IDENTIFICADOR_PROCEDIMENT_CIE = "215981"; // LOCAL


	// DEV data
//	protected static final String ENTITAT_DIR3CODI = "A04003003";
//	protected static final String ORGAN_CODI = "A04013529";
//	protected static final String ORGAN_CODI_CIE = "A04013529";
//	protected static final String IDENTIFICADOR_PROCEDIMENT = "874105";
//	protected static final String IDENTIFICADOR_PROCEDIMENT_CIE = "874106"; // DEV


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
		notificacio.setOrganGestor(ambEnviamentPostal ? ORGAN_CODI_CIE : ORGAN_CODI);
		notificacio.setConcepte("concepte_" + notificacioId);
		notificacio.setDescripcio("descripcio_" + notificacioId);
		notificacio.setEnviamentDataProgramada(null);
		notificacio.setRetard(5);
		notificacio.setCaducitat(new Date(System.currentTimeMillis() + 12 * 24 * 3600 * 1000));
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
		notificacio.setProcedimentCodi(ambEnviamentPostal ? IDENTIFICADOR_PROCEDIMENT_CIE : IDENTIFICADOR_PROCEDIMENT);
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
			destinatari.setInteressatTipus(InteressatTipusEnumDto.ADMINISTRACIO);
			if (destinatari.getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO))
				destinatari.setDir3Codi(ORGAN_SIR_CODI);
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
				entregaPostal.setCie(0);
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
	
	protected InputStream getContingutNotificacioAdjuntTxt() {
		return getClass().getResourceAsStream(
				"/es/caib/notib/client/notificacio_adjunt.txt");
	}
	
	protected InputStream getContingutNotificacioAdjuntGrande() {
		return getClass().getResourceAsStream(
				"/es/caib/notib/client/notificacio_adjunt_grande.pdf");
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
		byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());

		for(int i = 0; i < numeroDeNotificacions; i++){
			Integer iNotificacioId = generarRandomNoRepetit(repetits, numeroDeNotificacions);
			repetits.add(iNotificacioId);
			String notificacioId = "CARGA_MASIVA_" +  iNotificacioId;
			
			NotificacioV2 notificacio = new NotificacioV2();
			notificacio.setEmisorDir3Codi(ENTITAT_DIR3CODI);
			boolean comunicacioAdministracio = false;
			if (iNotificacioId % 2 == 0) {
				notificacio.setEnviamentTipus(EnviamentTipusEnum.COMUNICACIO);
				if (iNotificacioId % 3 == 0)
					comunicacioAdministracio = true;
			} else {
				notificacio.setEnviamentTipus(EnviamentTipusEnum.NOTIFICACIO);
			}
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
			notificacio.setCaducitat(new Date(System.currentTimeMillis() + 12 * 24 * 3600 * 1000));

			List<DocumentV2> documents = new ArrayList<>();
			if (comunicacioAdministracio)
				documents = crearArxius(random, notificacio, notificacioId.toString(), arxiuBytes);
			else
				documents = crearArxius(1, notificacio, notificacioId.toString(), arxiuBytes);
			
			
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
				if (comunicacioAdministracio) {
					titular.setInteressatTipus(InteressatTipusEnumDto.ADMINISTRACIO);
					titular.setDir3Codi(ENTITAT_DIR3CODI);
				} else {
					titular.setInteressatTipus(InteressatTipusEnumDto.FISICA);
				}
				enviament.setTitular(titular);
				Persona destinatari = new Persona();
				destinatari.setNom("melcior");
				destinatari.setLlinatge1("Andreu");
				destinatari.setLlinatge2("Nadal");
				destinatari.setNif("18225486x");
				destinatari.setTelefon("666020202");
				destinatari.setEmail("sandreu@limit.es");
				if (comunicacioAdministracio) {
					destinatari.setInteressatTipus(InteressatTipusEnumDto.ADMINISTRACIO);
					destinatari.setDir3Codi(ENTITAT_DIR3CODI);
				} else {
					destinatari.setInteressatTipus(InteressatTipusEnumDto.FISICA);
				}
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

	protected NotificacioV2 generarNotificacio(
			int numDestinataris,
			boolean ambEnviamentPostal) throws DatatypeConfigurationException, IOException, DecoderException {

		List<NotificacioV2> notificacions = new ArrayList();
		byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());

		Long iNotificacioId = System.currentTimeMillis();
		String notificacioId = "CARGA_MASIVA_" +  iNotificacioId;

		NotificacioV2 notificacio = new NotificacioV2();
		notificacio.setEmisorDir3Codi(ENTITAT_DIR3CODI);
		boolean comunicacioAdministracio = false;
		if (iNotificacioId % 2 == 0) {
			notificacio.setEnviamentTipus(EnviamentTipusEnum.COMUNICACIO);
			if (iNotificacioId % 3 == 0)
				comunicacioAdministracio = true;
		} else {
			notificacio.setEnviamentTipus(EnviamentTipusEnum.NOTIFICACIO);
		}
		notificacio.setUsuariCodi(USUARI_CODI);
//			notificacio.setComunicacioTipus(ComunicacioTipusEnum.ASINCRON);
		notificacio.setOrganGestor(ORGAN_CODI);
		notificacio.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT);

		int random = new Random().nextInt(4);
		if(random == 2) {
			notificacio.setConcepte("Error registre_" + notificacioId);
		}else {
			notificacio.setConcepte("concepte_" + notificacioId);
		}

		notificacio.setDescripcio("descripcio_" + notificacioId);
		notificacio.setEnviamentDataProgramada(null);
		notificacio.setRetard(5);
		notificacio.setCaducitat(new Date(System.currentTimeMillis() + 12 * 24 * 3600 * 1000));

		List<DocumentV2> documents = new ArrayList<>();
		if (comunicacioAdministracio)
			documents = crearArxius(random, notificacio, notificacioId.toString(), arxiuBytes);
		else
			documents = crearArxius(0, notificacio, notificacioId.toString(), arxiuBytes);


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
			if (comunicacioAdministracio) {
				titular.setInteressatTipus(InteressatTipusEnumDto.ADMINISTRACIO);
				titular.setDir3Codi(ENTITAT_DIR3CODI);
			} else {
				titular.setInteressatTipus(InteressatTipusEnumDto.FISICA);
			}
			enviament.setTitular(titular);
			Persona destinatari = new Persona();
			destinatari.setNom("melcior");
			destinatari.setLlinatge1("Andreu");
			destinatari.setLlinatge2("Nadal");
			destinatari.setNif("18225486x");
			destinatari.setTelefon("666020202");
			destinatari.setEmail("sandreu@limit.es");
			if (comunicacioAdministracio) {
				destinatari.setInteressatTipus(InteressatTipusEnumDto.ADMINISTRACIO);
				destinatari.setDir3Codi(ENTITAT_DIR3CODI);
			} else {
				destinatari.setInteressatTipus(InteressatTipusEnumDto.FISICA);
			}
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

		return notificacio;
	}
	
	private Integer generarRandomNoRepetit(List<Integer> repetits, Integer numeroDeNotificacions) {
		 Integer index = new Random().nextInt(numeroDeNotificacions);
	      while(repetits.contains(index)) {
	    	  index = new Random().nextInt(numeroDeNotificacions);
	      }
		return index; 
	}


	protected NotificacioV2 generaNotificacio(
			String concepte,
			int numDestinataris,
			int numEnviaments,
			boolean ambEnviamentPostal,
			NotificaDomiciliConcretTipusEnumDto tipusEnviamentPostal,
			boolean ambEnviamentDEH,
			boolean ambEnviamentDEHObligat,
			boolean ambRetard) throws IOException, DecoderException, DatatypeConfigurationException {

		String notificacioId = Long.toString(System.currentTimeMillis());
		NotificacioV2 notificacioV2 = generarNotificacio(
				concepte,
				notificacioId,
				numDestinataris,
				numEnviaments,
				ambEnviamentPostal,
				tipusEnviamentPostal,
				ambEnviamentDEH,
				ambEnviamentDEHObligat,
				true);

		if (!ambRetard)
			notificacioV2.setRetard(0);


		return notificacioV2;

	}

	protected NotificacioV2 generarNotificacio(
			String concepte,
			String notificacioId,
			int numDestinataris,
			int numEnviaments,
			boolean ambEnviamentPostal,
			NotificaDomiciliConcretTipusEnumDto tipusEnviamentPostal,
			boolean ambEnviamentDEH,
			boolean enviamentDEHObligat,
			boolean ambTipusInteressat) throws IOException, DecoderException, DatatypeConfigurationException {

		byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
		NotificacioV2 notificacio = new NotificacioV2();
		notificacio.setEmisorDir3Codi(ENTITAT_DIR3CODI);
		notificacio.setEnviamentTipus(EnviamentTipusEnum.COMUNICACIO);
		notificacio.setUsuariCodi(USUARI_CODI);
//		notificacio.setComunicacioTipus(ComunicacioTipusEnum.ASINCRON);
		notificacio.setOrganGestor(ambEnviamentPostal ? ORGAN_CODI_CIE : ORGAN_CODI);
		notificacio.setConcepte(concepte);
		notificacio.setDescripcio("descripcio_" + notificacioId);
		notificacio.setEnviamentDataProgramada(null);
		notificacio.setRetard(5);
		notificacio.setCaducitat(new Date(System.currentTimeMillis() + 12 * 24 * 3600 * 1000));
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

		notificacio.setProcedimentCodi(ambEnviamentPostal ? IDENTIFICADOR_PROCEDIMENT_CIE : IDENTIFICADOR_PROCEDIMENT);

		for (int i = 0; i < numEnviaments; i++) {
			Enviament enviament = new Enviament();
			Persona titular = new Persona();
			if (ambTipusInteressat) {
				titular.setInteressatTipus(InteressatTipusEnumDto.FISICA);
			} else {
				titular.setInteressatTipus(null);
			}
			titular.setNom("titularNom" + i);
			titular.setLlinatge1("titLlinatge1_" + i);
			titular.setLlinatge2("titLlinatge2_" + i);
			titular.setNif("43120476F");
			titular.setTelefon("666010101");
			titular.setEmail("titular@gmail.com");
			enviament.setTitular(titular);

			List<Persona> destinataris = new ArrayList<Persona>();

			for (int k = 0; k < numDestinataris; k++) {
				Persona destinatari = new Persona();
				if (ambTipusInteressat) {
					destinatari.setInteressatTipus(InteressatTipusEnumDto.FISICA);
				} else {
					destinatari.setInteressatTipus(null);
				}
				destinatari.setNom("destinatariNom" + k);
				destinatari.setLlinatge1("destLlinatge1_" + k);
				destinatari.setLlinatge2("destLlinatge2_" + k);
				destinatari.setNif("12345678Z");
				destinatari.setTelefon("666020202");
				destinatari.setEmail("destinatari@gmail.com");
				destinataris.add(destinatari);
			}
			enviament.getDestinataris().addAll(destinataris);
			if (ambEnviamentPostal) {
				enviament.setEntregaPostalActiva(true);
				EntregaPostal entregaPostal = new EntregaPostal();
				entregaPostal.setTipus(tipusEnviamentPostal);
				if (tipusEnviamentPostal.equals(NotificaDomiciliConcretTipusEnumDto.ESTRANGER)) {
					entregaPostal.setPaisCodi("FR");
					entregaPostal.setViaNom("Prime Minister's Office, 10 Downing Street");
					entregaPostal.setPoblacio("London");
					entregaPostal.setCodiPostal("00000");
				} else {
					entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.CALLE);
					entregaPostal.setViaNom("Bas");
					entregaPostal.setNumeroCasa("25");
					entregaPostal.setNumeroQualificador("bis");
//					entregaPostal.setPuntKm("pk01");
//					entregaPostal.setApartatCorreus("0228");
					entregaPostal.setPortal("P" + i);
					entregaPostal.setEscala("E" + i);
					entregaPostal.setPlanta("PL" + i);
					entregaPostal.setPorta("PT" + i);
					entregaPostal.setBloc("B" + i);
					entregaPostal.setComplement("complement" + i);
					entregaPostal.setCodiPostal("07500");
					entregaPostal.setPoblacio("poblacio" + i);
					entregaPostal.setMunicipiCodi("070337");
					entregaPostal.setProvincia("07");
					entregaPostal.setPaisCodi("ES");
					entregaPostal.setLinea1("linea1_" + i);
					entregaPostal.setLinea2("linea2_" + i);
					if (tipusEnviamentPostal.equals(NotificaDomiciliConcretTipusEnumDto.APARTAT_CORREUS))
						entregaPostal.setApartatCorreus("0228");
				}

				//entregaPostal.setCie(new Integer(8));
				enviament.setEntregaPostal(entregaPostal);
			}
			if (ambEnviamentDEH) {
				enviament.setEntregaDehActiva(true);
				EntregaDeh entregaDeh = new EntregaDeh();
				entregaDeh.setObligat(enviamentDEHObligat);
				entregaDeh.setProcedimentCodi(IDENTIFICADOR_PROCEDIMENT);
				enviament.setEntregaDeh(entregaDeh);
			}
			enviament.setServeiTipus(NotificaServeiTipusEnumDto.URGENT);
			notificacio.getEnviaments().add(enviament);
		}

		return notificacio;
	}

//	public NotificacioTest generarNotificacio(
//			String notificacioId,
//			int numDestinataris,
//			boolean ambEnviamentPostal,
//			EntregaPostalTipusEnum tipusEnviamentPostal,
//			boolean ambEnviamentDEH,
//			boolean enviamentDEHObligat) throws IOException, DecoderException {
//
//		byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
//		String documentGesdocId = pluginHelper.gestioDocumentalCreate(
//				PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
//				new ByteArrayInputStream(arxiuBytes));
//
//		NotificacioTest notificacio = new NotificacioTest();
//
//		notificacio.setEmisorDir3Codi(ENTITAT_DIR3CODI);
//		notificacio.setEnviamentTipus(NotificaEnviamentTipusEnumDto.COMUNICACIO);
//		notificacio.setEnviamentDataProgramada(null);
//		notificacio.setConcepte("concepte_" + notificacioId);
//		notificacio.setDocumentArxiuNom("documentArxiuNom_" + notificacioId + ".pdf");
//		notificacio.setDocumentArxiuId(documentGesdocId);
//		notificacio.setDocumentHash(Base64.encodeBase64String(
//				Hex.decodeHex(
//						DigestUtils.sha256Hex(arxiuBytes).toCharArray())));
//		notificacio.setDocumentNormalitzat(false);
//		notificacio.setDocumentGenerarCsv(false);
//		notificacio.setEstat(NotificacioEstatEnumDto.PENDENT);
//		notificacio.setDescripcio("descripcio_" + notificacioId);
//		notificacio.setCaducitat(new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000));
//		notificacio.setRetardPostal(5);
//		notificacio.setProcedimentCodiSia("0000");
//
//		if (ambEnviamentPostal) {
//			notificacio.setPagadorCorreusCodiDir3("A04013511");
//			notificacio.setPagadorCorreusContracteNum("pccNum_" + notificacioId);
//			notificacio.setPagadorCorreusCodiClientFacturacio("ccFac_" + notificacioId);
//			notificacio.setPagadorCorreusDataVigencia(new Date(0));
//			notificacio.setPagadorCieCodiDir3("A04013511");
//			notificacio.setPagadorCieDataVigencia(new Date(0));
//		}
//
//
//		List<NotificacioEnviamentEntity> enviaments = new ArrayList<NotificacioEnviamentEntity>();
//		for (int i = 0; i < numDestinataris; i++) {
//
//			NotificacioEnviamentTest enviament = new NotificacioEnviamentTest();
//			enviament.setTitularNif("00000000T");
//			enviament.setServeiTipus(NotificaServeiTipusEnumDto.URGENT);
//			enviament.setNotificacio(notificacio);
//			enviament.setNotificaEstat(NotificacioDestinatariEstatEnumDto.NOTIB_PENDENT);
//			enviament.setTitularNom("titularNom" + i);
//			enviament.setTitularLlinatge1("titLlinatge1_" + i);
//			enviament.setTitularLlinatge2("titLlinatge2_" + i);
//			enviament.setTitularTelefon("666010101");
//			enviament.setTitularEmail("titular@gmail.com");
//
//			enviament.setDestinatariNif("12345678Z");
//			enviament.setDestinatariNom("destinatariNom" + i);
//			enviament.setDestinatariLlinatge1("destLlinatge1_" + i);
//			enviament.setDestinatariLlinatge2("destLlinatge2_" + i);
//			enviament.setDestinatariTelefon("666020202");
//			enviament.setDestinatariEmail("destinatari@gmail.com");
//
//			if (ambEnviamentPostal) {
//
//				NotificaDomiciliTipusEnumDto tipus = null;
//				NotificaDomiciliConcretTipusEnumDto tipusConcret = null;
//
//				switch (tipusEnviamentPostal) {
//					case APARTAT_CORREUS:
//						tipusConcret = NotificaDomiciliConcretTipusEnumDto.APARTAT_CORREUS;
//						break;
//					case ESTRANGER:
//						tipusConcret = NotificaDomiciliConcretTipusEnumDto.ESTRANGER;
//						break;
//					case NACIONAL:
//						tipusConcret = NotificaDomiciliConcretTipusEnumDto.NACIONAL;
//						break;
//					case SENSE_NORMALITZAR:
//						tipusConcret = NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR;
//						break;
//				}
//				tipus = NotificaDomiciliTipusEnumDto.CONCRETO;
//
//				enviament.setDomiciliTipus(tipus);
//				enviament.setDomiciliConcretTipus(tipusConcret);
//
//				if (tipusEnviamentPostal.equals(EntregaPostalTipusEnum.ESTRANGER)) {
//					enviament.setDomiciliPaisCodiIso("UK");
//					enviament.setDomiciliViaNom("Prime Minister's Office, 10 Downing Street");
//					enviament.setDomiciliPoblacio("London");
//					enviament.setDomiciliCodiPostal("00000");
//					enviament.setDomiciliNumeracioTipus(NotificaDomiciliNumeracioTipusEnumDto.SENSE_NUMERO);
//				} else {
//					enviament.setDomiciliViaTipus(NotificaDomiciliViaTipusEnumDto.CALLE);
//					enviament.setDomiciliViaNom("Bas");
//					enviament.setDomiciliNumeracioNumero("25");
//					enviament.setDomiciliBloc("B" + i);
//					enviament.setDomiciliPortal("P" + i);
//					enviament.setDomiciliEscala("E" + i);
//					enviament.setDomiciliPlanta("PL" + i);
//					enviament.setDomiciliPorta("PT" + i);
//					enviament.setDomiciliComplement("complement" + i);
//					enviament.setDomiciliCodiPostal("07500");
//					enviament.setDomiciliPoblacio("poblacio" + i);
//					enviament.setDomiciliMunicipiCodiIne("070337");
//					enviament.setDomiciliProvinciaCodi("07");
//					enviament.setDomiciliPaisCodiIso("ES");
//					enviament.setDomiciliLinea1("linea1_" + i);
//					enviament.setDomiciliLinea2("linea2_" + i);
//					enviament.setDomiciliNumeracioPuntKm(domiciliNumeracioPuntKm);
//					if (tipusEnviamentPostal.equals(EntregaPostalTipusEnum.APARTAT_CORREUS)) {
//						enviament.setDomiciliNumeracioTipus(NotificaDomiciliNumeracioTipusEnumDto.APARTAT_CORREUS);
//						enviament.setDomiciliApartatCorreus("0228");
//					} else {
//						enviament.setDomiciliNumeracioTipus(NotificaDomiciliNumeracioTipusEnumDto.NUMERO);
//					}
//				}
//				enviament.setDomiciliCie(new Integer(8));
//			}
//
//			if (ambEnviamentDEH) {
//				enviament.setDehObligat(enviamentDEHObligat);
//				enviament.setDehNif("00000000T");
//				enviament.setDehProcedimentCodi("0000");
//			}
//
//			enviament.setNotificaReferencia(notificacioId + "_" + i);
//			enviaments.add(enviament);
//		}
//		notificacio.setEnviaments(enviaments);
//		return notificacio;
//	}


}
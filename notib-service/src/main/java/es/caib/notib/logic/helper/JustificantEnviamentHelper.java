package es.caib.notib.logic.helper;

import com.google.common.base.Strings;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.logic.intf.dto.DocumentDto;
import es.caib.notib.logic.intf.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.logic.intf.dto.PersonaDto;
import es.caib.notib.logic.intf.dto.ProgresDescarregaDto;
import es.caib.notib.logic.intf.dto.ProgresDescarregaDto.TipusInfo;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDtoV2;
import es.caib.notib.logic.intf.exception.JustificantException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

/**
 * Helper per generar el justificant de la notificació electrònica
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Slf4j
@Component
public class JustificantEnviamentHelper extends JustificantHelper<NotificacioDtoV2> {

	private static final String ENVIAMENT_TIPUS_TEXT = "es.caib.notib.logic.intf.dto.NotificaEnviamentTipusEnumDto.";

	public byte[] generarJustificant(NotificacioDtoV2 notificacio, ProgresDescarregaDto progres) throws JustificantException {

		log.debug("Generant el justificant d'enviament de la notificacio [notificacioId=" + notificacio.getId() + "]");
		var out = new ByteArrayOutputStream();
		try {
			var justificant = inicialitzaDocument(out, progres);
			progres.setProgres(30);
			progres.addInfo(ProgresDescarregaDto.TipusInfo.INFO, messageHelper.getMessage("es.caib.notib.justificant.proces.generant.titol"));
			crearTitolAndIntroduccio(justificant, notificacio, progres);
			progres.setProgres(40);
			crearIntroduccioEnviaments(justificant, notificacio, progres);
			var numEnviament = 1;
			String msg;
			for (var enviament : notificacio.getEnviaments()) {
				msg = messageHelper.getMessage("es.caib.notib.justificant.proces.generant.taula.enviament", new Object[]{numEnviament});
				progres.addInfo(ProgresDescarregaDto.TipusInfo.INFO, msg);
				crearTaulaEnviaments(justificant, notificacio, enviament, numEnviament, progres);
				justificant.add(Chunk.NEXTPAGE);
				numEnviament++;
			}
			progres.setProgres(70);
			var taulaAnnexos = new PdfPTable(1);
			taulaAnnexos.setWidthPercentage(100f);
			crearIntroduccioAnnexos(justificant, notificacio, progres);
			if (notificacio.getDocument() != null) {
//				## [TÍTOL]
				progres.addInfo(ProgresDescarregaDto.TipusInfo.INFO, messageHelper.getMessage("es.caib.notib.justificant.proces.generant.taula.annexos"));
				var titolCell = getTitolAnnexos(notificacio.getId());
				taulaAnnexos.addCell(titolCell);
				crearTaulaAnnexos(taulaAnnexos, notificacio, progres);
				justificant.add(taulaAnnexos);
			}
			justificant.close();
		} catch (DocumentException ex) {
			var errorMessage = messageHelper.getMessage("es.caib.notib.justificant.proces.generant.error", new Object[] {ex.getMessage()});
			progres.setProgres(100);
			progres.addInfo(ProgresDescarregaDto.TipusInfo.ERROR, errorMessage);
			log.error(errorMessage, ex);
		}
		return out.toByteArray();
	}

	protected void crearTitolAndIntroduccio(Document justificant, NotificacioDtoV2 notificacio, ProgresDescarregaDto progres) throws JustificantException {

		log.debug("Creant el títol i la introducció del justificant d'enviament de la notificacio [notificacioId=" + notificacio.getId() + "]");
		try {
//			## [TAULA QUE CONTÉ TÍTOL I INTRODUCCIÓ]
			var titolIntroduccioTable = new PdfPTable(1);
			titolIntroduccioTable.setWidthPercentage(100);
			var titolIntroduccioCell = new PdfPCell();
			titolIntroduccioCell.setBorder(Rectangle.NO_BORDER);

//			## [TITOL JUSTIFICANT]
			var titolMessage = messageHelper.getMessage("es.caib.notib.justificant.titol", new Object[] {
					messageHelper.getMessage(ENVIAMENT_TIPUS_TEXT + notificacio.getEnviamentTipus().name()).toUpperCase(Locale.ROOT)
			});
			var justificantTitol = new Paragraph(titolMessage, frutigerTitolBold);
			justificantTitol.setAlignment(Element.ALIGN_CENTER);
			justificantTitol.add(Chunk.NEWLINE);
			justificantTitol.add(Chunk.NEWLINE);

//			## [INTRODUCCIÓ JUSTIFICANT]
			var introduccio = messageHelper.getMessage("es.caib.notib.justificant.introduccio", new Object[] {
							messageHelper.getMessage(ENVIAMENT_TIPUS_TEXT + notificacio.getEnviamentTipus().name()).toLowerCase(Locale.ROOT),
							notificacio.getConcepte(),
							getDateTimeFormatted(notificacio.getNotificaEnviamentNotificaData() != null ? notificacio.getNotificaEnviamentNotificaData() : notificacio.getNotificaEnviamentData())});
			var justificantIntroduccio = new Paragraph();
			setParametersBold(justificantIntroduccio, introduccio);
			justificantIntroduccio.add(Chunk.NEWLINE);

//			## [DESCIPCIÓ NOFICACIÓ JUSTIFICANT]
			var justificantDescripcio = new Paragraph();
			if (notificacio.getDescripcio() != null && !notificacio.getDescripcio().isEmpty()) {
				var descripcio = messageHelper.getMessage("es.caib.notib.justificant.descripcio", new Object[] {
								messageHelper.getMessage(ENVIAMENT_TIPUS_TEXT + notificacio.getEnviamentTipus().name()).toLowerCase(),
								notificacio.getDescripcio()});

				setParametersBold(justificantDescripcio, descripcio);
				justificantDescripcio.setSpacingBefore(10f);
			}
			titolIntroduccioCell.addElement(justificantTitol);
			titolIntroduccioCell.addElement(justificantIntroduccio);
			if (notificacio.getDescripcio() != null && !notificacio.getDescripcio().isEmpty()) {
				titolIntroduccioCell.addElement(justificantDescripcio);
			}
			titolIntroduccioTable.addCell(titolIntroduccioCell);
			titolIntroduccioTable.setSpacingAfter(10f);
			justificant.add(titolIntroduccioTable);
		} catch (DocumentException ex) {
			var errorMessage = "Hi ha hagut un error generant la introducció del justificant";
			progres.setProgres(100);
			progres.addInfo(ProgresDescarregaDto.TipusInfo.INFO, errorMessage);
			log.debug(errorMessage, ex);
		}
	}

	private void crearIntroduccioEnviaments(Document justificant, NotificacioDtoV2 notificacio, ProgresDescarregaDto progres) throws JustificantException {

		log.debug("Creant la introducció de les enviaments del justificant d'enviament de la notificacio [notificacioId=" + notificacio.getId() + "]");
		try {
//			## [TAULA QUE CONTÉ INTRODUCCIÓ DE LES ENVIAMENTS]
			var titolIntroduccioTableEnviaments = new PdfPTable(1);
			titolIntroduccioTableEnviaments.setWidthPercentage(100);
			var titolIntroduccioEnviamentsCell = new PdfPCell();
			titolIntroduccioEnviamentsCell.setBorder(Rectangle.NO_BORDER);
//			## [INTRODUCCIÓ ENVIAMENTS JUSTIFICANT]
			var introduccioEnviaments = messageHelper.getMessage("es.caib.notib.justificant.enviaments.titol", new Object[] {
							messageHelper.getMessage(ENVIAMENT_TIPUS_TEXT + notificacio.getEnviamentTipus().name()).toLowerCase(),
							notificacio.getEnviaments().size()});

			var justificantIntroduccioEnviaments = new Paragraph();
			setParametersBold(justificantIntroduccioEnviaments, introduccioEnviaments);
			justificantIntroduccioEnviaments.setSpacingBefore(10f);
			titolIntroduccioEnviamentsCell.addElement(justificantIntroduccioEnviaments);
			titolIntroduccioTableEnviaments.addCell(titolIntroduccioEnviamentsCell);
			titolIntroduccioTableEnviaments.setSpacingAfter(10f);
			justificant.add(titolIntroduccioTableEnviaments);
		} catch (DocumentException ex) {
			var errorMessage = "Hi ha hagut un error generant la introducció de les enviaments del justificant";
			progres.setProgres(100);
			progres.addInfo(TipusInfo.INFO, errorMessage);
			log.debug(errorMessage, ex);
		}
	}
	private void crearIntroduccioAnnexos(Document justificant, NotificacioDtoV2 notificacio, ProgresDescarregaDto progres) throws JustificantException {

		log.debug("Creant la introducció dels documents del justificant d'enviament de la notificacio [notificacioId=" + notificacio.getId() + "]");
		try {
			// [TAULA QUE CONTÉ INTRODUCCIÓ DE LES ENVIAMENTS]
			var titolIntroduccioTableEnviaments = new PdfPTable(1);
			titolIntroduccioTableEnviaments.setWidthPercentage(100);
			var titolIntroduccioEnviamentsCell = new PdfPCell();
			titolIntroduccioEnviamentsCell.setBorder(Rectangle.NO_BORDER);

			// [INTRODUCCIÓ ENVIAMENTS JUSTIFICANT]
			var introduccioEnviaments = messageHelper.getMessage("es.caib.notib.justificant.documents", new Object[]{
					messageHelper.getMessage(ENVIAMENT_TIPUS_TEXT + notificacio.getEnviamentTipus().name()).toLowerCase(Locale.ROOT).toLowerCase()});

			var justificantIntroduccioEnviaments = new Paragraph();
			setParametersBold(justificantIntroduccioEnviaments, introduccioEnviaments);
			justificantIntroduccioEnviaments.setSpacingBefore(10f);
			titolIntroduccioEnviamentsCell.addElement(justificantIntroduccioEnviaments);
			titolIntroduccioTableEnviaments.addCell(titolIntroduccioEnviamentsCell);
			titolIntroduccioTableEnviaments.setSpacingAfter(10f);
			justificant.add(titolIntroduccioTableEnviaments);

		} catch (DocumentException ex) {
			var errorMessage = "Hi ha hagut un error generant la introducció de les enviaments del justificant";
			progres.setProgres(100);
			progres.addInfo(TipusInfo.INFO, errorMessage);
			log.debug(errorMessage, ex);
		}
	}

	private void crearTaulaEnviaments(Document justificant, NotificacioDtoV2 notificacio, NotificacioEnviamentDtoV2 enviament, int numEnviament, ProgresDescarregaDto progres) throws JustificantException {

		log.debug("Generant la taula d'enviament del justificant d'enviament de l'enviament [enviamentId=" + enviament.getId() + "]");
		try {
			var taulaEnviaments = new PdfPTable(1);
			taulaEnviaments.setWidthPercentage(99f);
//			## [TÍTOL]
			var titolCell = getTitolEnviament(numEnviament, notificacio, enviament);
//			## [CONTINGUT]
			var contingut = getContingutEnviament(enviament, notificacio);
			taulaEnviaments.addCell(titolCell);
			taulaEnviaments.addCell(contingut);
			justificant.add(taulaEnviaments);
		} catch (Exception ex) {
			var errorMessage = messageHelper.getMessage("es.caib.notib.justificant.proces.generant.taula.enviament.error");
			progres.setProgres(100);
			progres.addInfo(TipusInfo.INFO, errorMessage);
			log.debug(errorMessage, ex);
		}
	}
	
	private PdfPCell getTitolEnviament(int numEnviament, NotificacioDtoV2 notificacio, NotificacioEnviamentDtoV2 enviament) {

		log.debug("Generant el títol de la taula d'enviament del justificant d'enviament de la notificació [notificacioId=" + notificacio.getId() + "]");
		var titolCell = new PdfPCell();
		var titolEnviamentMessage = messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.titol",
				new Object[] {
						(EnviamentTipus.NOTIFICACIO.equals(notificacio.getEnviamentTipus()) && enviament.isPerEmail() ?  messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.titol.notificacio.email") + " " : "") + messageHelper.getMessage(ENVIAMENT_TIPUS_TEXT + notificacio.getEnviamentTipus().name()).toLowerCase(),
						numEnviament, 
						notificacio.getEnviaments().size()});

		var titolParagraph = new Paragraph(titolEnviamentMessage, calibriWhiteBold);
		titolParagraph.setAlignment(Element.ALIGN_CENTER);
		titolCell.addElement(titolParagraph);
		titolCell.setPaddingBottom(6f);
		titolCell.setBackgroundColor(new BaseColor(166, 166, 166));
		titolCell.setBorderWidth((float) 0.5);
		titolCell.setBorderColor(new BaseColor(166, 166, 166));
		return titolCell;
	}
	
	private PdfPCell getTitolAnnexos(Long notificacioId) {

		log.debug("Generant el títol de la taula d'annexos del justificant d'enviament de la notificació [notificacioId=" + notificacioId + "]");
		var titolCell = new PdfPCell();
		var titolDocumentMessage = messageHelper.getMessage("es.caib.notib.justificant.annexos.taula.titol");
		var titolParagraph = new Paragraph(titolDocumentMessage, calibriWhiteBold);
		titolParagraph.setAlignment(Element.ALIGN_CENTER);
		titolCell.addElement(titolParagraph);
		titolCell.setPaddingBottom(6f);
		titolCell.setBackgroundColor(new BaseColor(166, 166, 166));
		titolCell.setBorderWidth((float) 0.5);
		titolCell.setBorderColor(new BaseColor(166, 166, 166));
		return titolCell;
	}
	
	private PdfPCell getContingutEnviament(NotificacioEnviamentDtoV2 enviament, NotificacioDtoV2 notificacio) throws DocumentException {

		log.debug("Generant el contingut de la taula d'enviament del justificant d'enviament de l'enviament [enviamentId=" + enviament.getId() + "]");
		int[] headingTablewidths = {45, 55};
		var contingutCell = new PdfPCell();
//		######## INICI DADES REGISTRE #########
//		## [DADES REGISTRE - TÍTOL]
		var dadesRegistreTitol = new Paragraph(messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.dades.registre"), calibri10Bold);
		dadesRegistreTitol.setAlignment(Element.ALIGN_LEFT);

//		## [DADES REGISTRE - CONTINGUT]
		var dadesRegistre = new Paragraph();
		var dadesRegistreTable = new PdfPTable(2);
		dadesRegistreTable.setWidthPercentage(95f);
		dadesRegistreTable.setWidths(headingTablewidths);
		
//		## [DADES REGISTRE - NÚMERO]
		var numRegistreMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.dades.registre.numero");
		var dadesRegistreNumeroTitleChunk = new Chunk(numRegistreMessage, calibri10);
		var dadesRegistreNumeroContentChunk = new Chunk(enviament.getRegistreNumeroFormatat(), calibri10);
		createNewTableContent(dadesRegistreTable, dadesRegistreNumeroTitleChunk, dadesRegistreNumeroContentChunk);
		
//		## [DADES REGISTRE - DATA]
		var dataRegistreMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.dades.registre.data");
		var dadesRegistreDataTitleChunk = new Chunk(dataRegistreMessage, calibri10);
		var dadesRegistreDataContentChunk = new Chunk(getDateTimeFormatted(enviament.getRegistreData()), calibri10);
		createNewTableContent(dadesRegistreTable, dadesRegistreDataTitleChunk, dadesRegistreDataContentChunk);
		
		dadesRegistre.setSpacingBefore(5f);
		dadesRegistre.setSpacingAfter(5f);
        dadesRegistre.add(dadesRegistreTable);
//		######## FI DADES REGISTRE #########
        Paragraph dadesNotificaTitol = null;
        Paragraph dadesNotifica = null;
        if (enviament.getNotificaIdentificador() != null) {
//		######## INICI DADES NOTIFICA #########
//      	## [DADES NOTIFICA]
	        var dadesNotificaMessage = messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.dades.notifica");
	     	dadesNotificaTitol = new Paragraph(dadesNotificaMessage, calibri10Bold);
	     	dadesNotificaTitol.setAlignment(Element.ALIGN_LEFT);
	     	
//	     	## [DADES NOTIFICA - CONTINGUT]
	     	dadesNotifica = new Paragraph();
	     	var dadesNotificaTable = new PdfPTable(2);
	     	dadesNotificaTable.setWidthPercentage(95f);
	     	dadesNotificaTable.setWidths(headingTablewidths);
	     	
//	     	## [DADES NOTIFICA - IDENTIFICADOR]
	     	var dadesNotificaIdentificador = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.dades.notifica.identificador");
	     	var dadesNotificaIdentTitleChunk = new Chunk(dadesNotificaIdentificador, calibri10);
	     	var dadesNotificaIdentContentChunk = new Chunk(enviament.getNotificaIdentificador(), calibri10);
			createNewTableContent(dadesNotificaTable, dadesNotificaIdentTitleChunk, dadesNotificaIdentContentChunk);
	     
//	     	## [DADES NOTIFICA - EMISOR]
	     	var dadesNotificaEmisor = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.dades.notifica.emisor");
	     	var dadesNotificaEmisorTitleChunk = new Chunk(dadesNotificaEmisor, calibri10);
	     	if (notificacio.getOrganGestor() != null) {
				var dadesNotificaEmisorContentChunk = new Chunk("[" + notificacio.getOrganGestor() + "] " + notificacio.getOrganGestorNom(), calibri10);
				createNewTableContent(dadesNotificaTable, dadesNotificaEmisorTitleChunk, dadesNotificaEmisorContentChunk);
			}
//	     	## [DADES NOTIFICA - PROCEDIMENT]
			if (notificacio.getProcediment() != null) {
				var dadesNotificaProcediment = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.dades.notifica.procediment");
	     		var dadesNotificaProcedimentTitleChunk = new Chunk(dadesNotificaProcediment, calibri10);
	     		var dadesNotificaProcedimentContentChunk = new Chunk("[" + notificacio.getProcediment().getCodi() + "] " + notificacio.getProcediment().getNom(), calibri10);
	     		createNewTableContent(dadesNotificaTable, dadesNotificaProcedimentTitleChunk, dadesNotificaProcedimentContentChunk);
			}
			
	     	dadesNotifica.setSpacingBefore(5f);
	     	dadesNotifica.setSpacingAfter(5f);
	        dadesNotifica.add(dadesNotificaTable);
//		######## FI DADES NOTIFICA #########
        }
		if (enviament.isPerEmail()) {
//		######## INICI DADES EMAIL #########
//      	## [DADES EMAIL]
			var dadesEmailMessage = messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.dades.email");
			dadesNotificaTitol = new Paragraph(dadesEmailMessage, calibri10Bold);
			dadesNotificaTitol.setAlignment(Element.ALIGN_LEFT);

//	     	## [DADES NOTIFICA - CONTINGUT]
			dadesNotifica = new Paragraph();
			var dadesEmailTable = new PdfPTable(2);
			dadesEmailTable.setWidthPercentage(95f);
			dadesEmailTable.setWidths(headingTablewidths);

//	     	## [DADES NOTIFICA - EMISOR]
			var dadesNotificaEmisor = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.dades.notifica.emisor");
			var dadesNotificaEmisorTitleChunk = new Chunk(dadesNotificaEmisor, calibri10);
			if (notificacio.getOrganGestor() != null) {
				var dadesNotificaEmisorContentChunk = new Chunk("[" + notificacio.getOrganGestor() + "] " + notificacio.getOrganGestorNom(), calibri10);
				createNewTableContent(dadesEmailTable, dadesNotificaEmisorTitleChunk, dadesNotificaEmisorContentChunk);
			}
//	     	## [DADES NOTIFICA - PROCEDIMENT]
			if (notificacio.getProcediment() != null) {
				var dadesNotificaProcediment = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.dades.notifica.procediment");
				var dadesNotificaProcedimentTitleChunk = new Chunk(dadesNotificaProcediment, calibri10);
				var dadesNotificaProcedimentContentChunk = new Chunk("[" + notificacio.getProcediment().getCodi() + "] " + notificacio.getProcediment().getNom(), calibri10);
				createNewTableContent(dadesEmailTable, dadesNotificaProcedimentTitleChunk, dadesNotificaProcedimentContentChunk);
			}

			dadesNotifica.setSpacingBefore(5f);
			dadesNotifica.setSpacingAfter(5f);
			dadesNotifica.add(dadesEmailTable);
//		######## FI DADES NOTIFICA #########
		}
//		######## INICI DADES INTERESSATS #########
     	var dadesInteressatsTitol = new Paragraph(messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessats"), calibri10Bold);
     	dadesInteressatsTitol.setAlignment(Element.ALIGN_LEFT);
     	
//		######## INICI DADES TITULAR #########
//     	## [DADES INTERESSATS - TITULAR]
     	var dadesInteressatsTitularTitol = new Paragraph(messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.titular"), calibri10);
     	dadesInteressatsTitularTitol.setAlignment(Element.ALIGN_LEFT);
     	dadesInteressatsTitularTitol.setIndentationLeft(16f);
     	dadesInteressatsTitularTitol.setSpacingBefore(10f);
     	var titular = enviament.getTitular();
     	
     	var dadesTitular = new Paragraph();
     	var dadesTitularTable = new PdfPTable(2);
     	dadesTitularTable.setWidthPercentage(80f);
     	dadesTitularTable.setWidths(headingTablewidths);
     	
     	var titularNomMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.titular.nom");
     	var dadesTitularNomTitleChunk = new Chunk(titularNomMessage, calibri10);
     	var dadesTitularNomContentChunk = new Chunk(getNomInteressat(titular), calibri10);
     	createNewTableContent(dadesTitularTable, dadesTitularNomTitleChunk, dadesTitularNomContentChunk);
     	
     	if (titular.getInteressatTipus().equals(InteressatTipus.FISICA) || titular.getInteressatTipus().equals(InteressatTipus.FISICA_SENSE_NIF)) {
	     	var titularLlintgMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.titular.llinatges");
	     	var dadesTitularLlintgTitleChunk = new Chunk(titularLlintgMessage, calibri10);
	     	var dadesTitularLlintgContentChunk = new Chunk(titular.getLlinatges(), calibri10);
	     	createNewTableContent(dadesTitularTable, dadesTitularLlintgTitleChunk, dadesTitularLlintgContentChunk);
     	}
     	
     	if (titular.getNif() != null) {
			var titularNifMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.titular.nif");
			if (enviament.isPerEmail()) {
				if (titular.getDocumentTipus() != null) {
					titularNifMessage = "   " + messageHelper.getMessage("es.caib.notib.logic.intf.dto.DocumentTipusEnumDto." + titular.getDocumentTipus().name()) + ":";
				} else {
					titularNifMessage = "   " + messageHelper.getMessage("es.caib.notib.logic.intf.dto.DocumentTipusEnumDto.ALTRE") + ":";
				}
			}
	     	var dadesTitularNifTitleChunk = new Chunk(titularNifMessage, calibri10);
	     	var dadesTitularNifContentChunk = new Chunk(titular.getNif(), calibri10);
	     	createNewTableContent(dadesTitularTable, dadesTitularNifTitleChunk, dadesTitularNifContentChunk);
     	}
		if (enviament.isPerEmail() || !Strings.isNullOrEmpty(titular.getEmail())) {
			var emailMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.titular.email");
			var dadesTitularEmailTitleChunk = new Chunk(emailMessage, calibri10);
			var dadesTitularEmailContentChunk = new Chunk(titular.getEmail(), calibri10);
			createNewTableContent(dadesTitularTable, dadesTitularEmailTitleChunk, dadesTitularEmailContentChunk);
		}
     	if (titular.getInteressatTipus().equals(InteressatTipus.ADMINISTRACIO)) {
	     	var titularDir3Message = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.titular.dir3");
	     	var dadesTitularDir3TitleChunk = new Chunk(titularDir3Message, calibri10);
	     	var dadesTitularDir3ContentChunk = new Chunk(titular.getDir3Codi(), calibri10);
	     	createNewTableContent(dadesTitularTable, dadesTitularDir3TitleChunk, dadesTitularDir3ContentChunk);
     	}
//		######## FI DADES TITULAR #########
     	dadesTitular.add(dadesTitularTable);
        
     	contingutCell.addElement(dadesRegistreTitol);
     	contingutCell.addElement(dadesRegistre);
     	if (dadesNotificaTitol != null) {
			contingutCell.addElement(dadesNotificaTitol);
		}
     	if (dadesNotifica != null) {
			contingutCell.addElement(dadesNotifica);
		}
     	contingutCell.addElement(dadesInteressatsTitol);
     	contingutCell.addElement(dadesInteressatsTitularTitol);
     	contingutCell.addElement(dadesTitular);
     	
//		######## INICI DADES DESTINATARIS #########
        var numDestinatari = 1;
        for (PersonaDto destinatari : enviament.getDestinataris()) {
//     		## [DESTINATARI]
        	var dadesInteressatsDestinatariMessage = messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.destinatari", new Object[] {numDestinatari});
         	var dadesInteressatsDestinatariTitol = new Paragraph(dadesInteressatsDestinatariMessage, calibri10);
         	dadesInteressatsDestinatariTitol.setAlignment(Element.ALIGN_LEFT);
         	dadesInteressatsDestinatariTitol.setIndentationLeft(16f);
         	dadesInteressatsDestinatariTitol.setSpacingBefore(10f);
         	
         	var dadesDestinatari = new Paragraph();
         	PdfPTable dadesDestinatariTable = new PdfPTable(2);
         	dadesDestinatariTable.setWidthPercentage(80f);
         	dadesDestinatariTable.setWidths(headingTablewidths);

         	var destinatariNomMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.destinatari.nom");
         	var dadesDestinatariNomTitleChunk = new Chunk(destinatariNomMessage, calibri10);
         	var dadesDestinatariNomContentChunk = new Chunk(getNomInteressat(destinatari), calibri10);
         	createNewTableContent(dadesDestinatariTable, dadesDestinatariNomTitleChunk, dadesDestinatariNomContentChunk);
         	
         	if (destinatari.getInteressatTipus().equals(InteressatTipus.FISICA)) {
	         	var destinatariLlintgMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.destinatari.llinatges");
	         	var dadesDestinatariLlintgTitleChunk = new Chunk(destinatariLlintgMessage, calibri10);
	         	var dadesDestinatariLlintgContentChunk = new Chunk(destinatari.getLlinatges(), calibri10);
	         	createNewTableContent(dadesDestinatariTable, dadesDestinatariLlintgTitleChunk, dadesDestinatariLlintgContentChunk);
         	}

			if (!Strings.isNullOrEmpty(destinatari.getEmail())) {
				var emailMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.titular.email");
				var email = new Chunk(emailMessage, calibri10);
				var dadesChunk = new Chunk(destinatari.getEmail(), calibri10);
				createNewTableContent(dadesDestinatariTable, email, dadesChunk);
			}
         	
         	if (destinatari.getNif() != null) {
	         	var destinatariNifMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.destinatari.nif");
	         	var dadesDestinatariNifTitleChunk = new Chunk(destinatariNifMessage, calibri10);
	         	var dadesDestinatariNifContentChunk = new Chunk(destinatari.getNif(), calibri10);
	         	createNewTableContent(dadesDestinatariTable, dadesDestinatariNifTitleChunk, dadesDestinatariNifContentChunk);
         	}
         	if (destinatari.getInteressatTipus().equals(InteressatTipus.ADMINISTRACIO)) {
	         	var destinatariDir3Message = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.destinatari.dir3");
	         	var dadesDestinatariDir3TitleChunk = new Chunk(destinatariDir3Message, calibri10);
	         	var dadesDestinatariDir3ContentChunk = new Chunk(destinatari.getDir3Codi(), calibri10);
	         	createNewTableContent(dadesDestinatariTable, dadesDestinatariDir3TitleChunk, dadesDestinatariDir3ContentChunk);
         	}
         	
         	dadesDestinatari.setSpacingBefore(10f);
         	contingutCell.addElement(dadesInteressatsDestinatariTitol);
         	contingutCell.addElement(dadesDestinatariTable);
         	numDestinatari++;
		}
//		######## FI DADES DESTINATARIS #########
//		######## FI DADES INTERESSATS #########

        //## [CONFIGURACIÓ CELLA CONTINGUT]
     	contingutCell.setPaddingLeft(7f);
     	contingutCell.setPaddingBottom(15f);
        contingutCell.setBorderWidth((float) 0.5);
        contingutCell.setBorderColor(new BaseColor(166, 166, 166));
		return contingutCell;
	}
	
	private void createNewTableContent(PdfPTable table, Chunk... chunks) {

//		## [TÍTOL]
     	var procedimentTitleCell = new PdfPCell();
		var listProcediment = new List(List.UNORDERED);
		listProcediment.add(new ListItem(chunks[0]));
		procedimentTitleCell.addElement(listProcediment);
		procedimentTitleCell.setBorder(Rectangle.NO_BORDER);
		
//		## [VALOR]
		var procedimentContentCell = new PdfPCell();
		procedimentContentCell.addElement(chunks[1]);
		procedimentContentCell.setBorder(Rectangle.NO_BORDER);
		table.addCell(procedimentTitleCell);
		table.addCell(procedimentContentCell);
	}	
	
	private void crearTaulaAnnexos(PdfPTable taulaAnnexos, NotificacioDtoV2 notificacio, ProgresDescarregaDto progres) throws JustificantException {

		log.debug("Generant la taula de annexos del justificant d'enviament per als documents.");
		try {
//			## [CONTINGUT]
			int[] headingTablewidths = {39, 8, 20, 20, 13};
			var dadesAnnexoTable = new PdfPTable(5);
			dadesAnnexoTable.setWidthPercentage(100f);
			dadesAnnexoTable.setWidths(headingTablewidths);
			getHeadersAnnexos(dadesAnnexoTable);
			if (notificacio.getDocument() != null) {
				getContingutAnnexos(notificacio.getDocument(), dadesAnnexoTable);
			}
			if (notificacio.getDocument2() != null) {
				getContingutAnnexos(notificacio.getDocument2(), dadesAnnexoTable);
			}
			if (notificacio.getDocument3() != null) {
				getContingutAnnexos(notificacio.getDocument3(), dadesAnnexoTable);
			}
			if (notificacio.getDocument4() != null) {
				getContingutAnnexos(notificacio.getDocument4(), dadesAnnexoTable);
			}
			if (notificacio.getDocument5() != null) {
				getContingutAnnexos(notificacio.getDocument5(), dadesAnnexoTable);
			}
			var contingut = new PdfPCell();
			contingut.setBorder(Rectangle.NO_BORDER);
			contingut.addElement(dadesAnnexoTable);
			taulaAnnexos.addCell(contingut);
		} catch (Exception ex) {
			String errorMessage = messageHelper.getMessage("es.caib.notib.justificant.proces.generant.taula.annexos.error");
			progres.setProgres(100);
			progres.addInfo(TipusInfo.INFO, errorMessage);
			log.debug(errorMessage, ex);
		}
	}
	
	private void getHeadersAnnexos(PdfPTable dadesAnnexoTable) {

//		## [NOM - TÍTOL]
		var dadesAnnexoTitol = new Paragraph(messageHelper.getMessage("es.caib.notib.justificant.annexos.taula.arxiuNom"), calibri10Bold);
		createNewTableAnnexosHeader(dadesAnnexoTitol, dadesAnnexoTable);
//		## [MIDA - TÍTOL]
		dadesAnnexoTitol = new Paragraph(messageHelper.getMessage("es.caib.notib.justificant.annexos.taula.mida"), calibri10Bold);
		createNewTableAnnexosHeader(dadesAnnexoTitol, dadesAnnexoTable);
//		## [CSV - TÍTOL]
		dadesAnnexoTitol = new Paragraph(messageHelper.getMessage("es.caib.notib.justificant.annexos.taula.csv"), calibri10Bold);
		createNewTableAnnexosHeader(dadesAnnexoTitol, dadesAnnexoTable);
//		## [VALIDESA - TÍTOL]
		dadesAnnexoTitol = new Paragraph(messageHelper.getMessage("es.caib.notib.justificant.annexos.taula.validesa"), calibri10Bold);
		createNewTableAnnexosHeader(dadesAnnexoTitol, dadesAnnexoTable);
//		## [TIPODOCUMENTAL - TÍTOL]
		dadesAnnexoTitol = new Paragraph(messageHelper.getMessage("es.caib.notib.justificant.annexos.taula.tipoDocumental"), calibri10Bold);
		createNewTableAnnexosHeader(dadesAnnexoTitol, dadesAnnexoTable);
	}

	private void createNewTableAnnexosHeader(Paragraph dadesAnnexoTitol, PdfPTable dadesAnnexoTable) {

		dadesAnnexoTitol.setAlignment(Element.ALIGN_LEFT);
		var contingutCell = new PdfPCell();
		contingutCell.addElement(dadesAnnexoTitol);
		dadesAnnexoTable.addCell(contingutCell);
	}
	
	private void getContingutAnnexos(DocumentDto document, PdfPTable dadesAnnexoTable) {

//		## [CONTINGUT COLUMNES]
		var nomCsvOUuid = "";
		if (document.getArxiuNom() == null || document.getArxiuNom().isEmpty()) {
			if (document.getCsv() != null && document.getUuid() != null) {//es Uuid
				nomCsvOUuid = "uuid:" + document.getUuid();
			} else if (document.getCsv() != null && document.getUuid() == null) { //es CSV
				nomCsvOUuid = "csv:" + document.getCsv();
			}
		}
		createNewTableAnnexosContent(dadesAnnexoTable, new Chunk((document.getArxiuNom() != null && !document.getArxiuNom().isEmpty()) ? document.getArxiuNom() : nomCsvOUuid, calibri8));
		createNewTableAnnexosContent(dadesAnnexoTable, new Chunk(document.getMida() != null ? Math.round((float) document.getMida() / 1024 ) + " KB" : "", calibri8));
		createNewTableAnnexosContent(dadesAnnexoTable, new Chunk((document.getCsv() != null && !document.getCsv().isEmpty()) ? document.getCsv() : "", calibri8));
		createNewTableAnnexosContent(dadesAnnexoTable, new Chunk(document.getValidesa() != null ? document.getValidesa().toString() : "", calibri8));
		createNewTableAnnexosContent(dadesAnnexoTable, new Chunk(document.getTipoDocumental() != null ? document.getTipoDocumental().toString() : "", calibri8));
	}
	
	private void createNewTableAnnexosContent(PdfPTable table, Chunk chunk) {

		var procedimentContentCell = new PdfPCell();
		procedimentContentCell.addElement(chunk);
		procedimentContentCell.setBorderWidth(0.25f);
		table.addCell(procedimentContentCell);
	}	

	private String getNomInteressat(PersonaDto persona)  {

		switch (persona.getInteressatTipus()) {
			case FISICA:
			case FISICA_SENSE_NIF:
			case ADMINISTRACIO:
				return persona.getNom() != null ? persona.getNom() : "";
			case JURIDICA:
				return persona.getRaoSocial() != null ? persona.getRaoSocial() : persona.getNom() != null ? persona.getNom() : "";
			default:
				return persona.getRaoSocial() != null ? persona.getRaoSocial() : "";
		}
	}
}
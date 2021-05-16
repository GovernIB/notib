package es.caib.notib.core.helper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import es.caib.notib.core.api.dto.DocumentDto;
import es.caib.notib.core.api.dto.InteressatTipusEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.PersonaDto;
import es.caib.notib.core.api.dto.ProgresDescarregaDto;
import es.caib.notib.core.api.dto.ProgresDescarregaDto.TipusInfo;
import es.caib.notib.core.api.exception.JustificantException;

/**
 * Helper per generar el justificant de la notificació electrònica
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */

@Component
public class JustificantHelper {

	private Font frutiger8 = FontFactory.getFont("Frutiger", 8, new BaseColor(127, 127, 127, 1)); // #7F7F7F
	private Font frutiger9 = FontFactory.getFont("Frutiger", 8, new BaseColor(127, 127, 127, 1)); // #7F7F7F
	private Font frutigerTitolBold = FontFactory.getFont("Frutiger", 11, Font.BOLD);
	private Font calibri10 = FontFactory.getFont("Calibri", 10);
	private Font calibri10Bold = FontFactory.getFont("Calibri", 10, Font.BOLD); 
	private Font calibriWhiteBold = FontFactory.getFont("Calibri", 10, Font.BOLD, new BaseColor(255, 255, 255)); // #FFFFFF
	
	@Autowired
	MessageHelper messageHelper;

	public byte[] generarJustificant(
			NotificacioDtoV2 notificacio, 
			ProgresDescarregaDto progres) throws JustificantException {
		logger.debug("Generant el justificant d'enviament de la notificacio [notificacioId=" + notificacio.getId() + "]");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			Document justificant = inicialitzaDocument(out, progres);

			progres.setProgres(30);
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("es.caib.notib.justificant.proces.generant.titol"));
			crearTitolAndIntroduccio(
					justificant, 
					notificacio, 
					progres);

			progres.setProgres(40);
			int numEnviament = 1;
			for (NotificacioEnviamentDtoV2 enviament : notificacio.getEnviaments()) {
				progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("es.caib.notib.justificant.proces.generant.taula.enviament", new Object[] {numEnviament}));
				crearTaulaEnviaments(
						justificant, 
						notificacio, 
						enviament, 
						numEnviament,
						progres);
				numEnviament++;
			}
			
			PdfPTable taulaAnnexos = new PdfPTable(1);
			taulaAnnexos.setWidthPercentage(100f);
			if (notificacio.getDocument() != null) {
//				## [TÍTOL]
				progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("es.caib.notib.justificant.proces.generant.taula.annexos"));
				PdfPCell titolCell = getTitolAnnexos(notificacio.getId());
				taulaAnnexos.addCell(titolCell);
				crearTaulaAnnexos(
						taulaAnnexos,
						justificant,
						notificacio.getId(),
						notificacio,
						progres);
			}

			justificant.close();
		} catch (DocumentException ex) {
			String errorMessage = messageHelper.getMessage("es.caib.notib.justificant.proces.generant.error", new Object[] {ex.getMessage()});
			progres.setProgres(100);
			progres.addInfo(TipusInfo.ERROR, errorMessage);
			logger.error(
					errorMessage, 
					ex);
		}
		return out.toByteArray();
	}

	private void crearTitolAndIntroduccio(
			Document justificant, 
			NotificacioDtoV2 notificacio, 
			ProgresDescarregaDto progres) throws JustificantException {
		logger.debug("Creant el títol i la introducció del justificant d'enviament de la notificacio [notificacioId=" + notificacio.getId() + "]");
		try {
//			## [TAULA QUE CONTÉ TÍTOL I INTRODUCCIÓ]
			PdfPTable titolIntroduccioTable = new PdfPTable(1);
			titolIntroduccioTable.setWidthPercentage(100);
			PdfPCell titolIntroduccioCell = new PdfPCell();
			titolIntroduccioCell.setBorder(Rectangle.NO_BORDER);
			
//			## [TITOL JUSTIFICANT]
			String titolMessage = messageHelper.getMessage("es.caib.notib.justificant.titol");
			Paragraph justificantTitol = new Paragraph(titolMessage, frutigerTitolBold);
			justificantTitol.setAlignment(Element.ALIGN_CENTER);
			justificantTitol.add(Chunk.NEWLINE);
			justificantTitol.add(Chunk.NEWLINE);
			
//			## [INTRODUCCIÓ JUSTIFICANT]
			String introduccio = messageHelper.getMessage(
					"es.caib.notib.justificant.introduccio", 
					new Object[] {
							messageHelper.getMessage("es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto." + notificacio.getEnviamentTipus().name()),
							notificacio.getConcepte(),
							getDateTimeFormatted(notificacio.getNotificaEnviamentNotificaData() != null ? notificacio.getNotificaEnviamentNotificaData() : notificacio.getNotificaEnviamentData())});
			Paragraph justificantIntroduccio = new Paragraph();
			setParametersBold(justificantIntroduccio, introduccio);
			justificantIntroduccio.add(Chunk.NEWLINE);
			
//			## [DESCIPCIÓ NOFICACIÓ JUSTIFICANT]
			Paragraph justificantDescripcio = new Paragraph();
			if (notificacio.getDescripcio() != null && !notificacio.getDescripcio().isEmpty()) {
				String descripcio = messageHelper.getMessage("es.caib.notib.justificant.descripcio", new Object[] {notificacio.getDescripcio()});				
				setParametersBold(justificantDescripcio, descripcio);
				justificantDescripcio.setSpacingBefore(10f);
			}
			
//			## [INTRODUCCIÓ ENVIAMENTS JUSTIFICANT]
			String introduccioEnviaments = messageHelper.getMessage("es.caib.notib.justificant.enviaments.titol", new Object[] {notificacio.getEnviaments().size()});
			Paragraph justificantIntroduccioEnviaments = new Paragraph();
			setParametersBold(justificantIntroduccioEnviaments, introduccioEnviaments);
			justificantIntroduccioEnviaments.setSpacingBefore(10f);
			
			titolIntroduccioCell.addElement(justificantTitol);
			titolIntroduccioCell.addElement(justificantIntroduccio);		
			if (notificacio.getDescripcio() != null && !notificacio.getDescripcio().isEmpty())
				titolIntroduccioCell.addElement(justificantDescripcio);
			titolIntroduccioCell.addElement(justificantIntroduccioEnviaments);
			
			titolIntroduccioTable.addCell(titolIntroduccioCell);
			titolIntroduccioTable.setSpacingAfter(10f);
			justificant.add(titolIntroduccioTable);
		} catch (DocumentException ex) {
			String errorMessage = "Hi ha hagut un error generant la introducció del justificant";
			progres.setProgres(100);
			progres.addInfo(TipusInfo.INFO, errorMessage);
			logger.debug(errorMessage, ex);
		}
	}

	private void crearTaulaEnviaments(
			Document justificant, 
			NotificacioDtoV2 notificacio,
			NotificacioEnviamentDtoV2 enviament, 
			int numEnviament,
			ProgresDescarregaDto progres) throws JustificantException {
		logger.debug("Generant la taula d'enviament del justificant d'enviament de l'enviament [enviamentId=" + enviament.getId() + "]");
		try {
			PdfPTable taulaEnviaments = new PdfPTable(1);
			taulaEnviaments.setWidthPercentage(99f);
			
//			## [TÍTOL]
			PdfPCell titolCell = getTitolEnviament(numEnviament, notificacio);
			
//			## [CONTINGUT]
			PdfPCell contingut = getContingutEnviament(enviament, notificacio);
			
			taulaEnviaments.addCell(titolCell);
			taulaEnviaments.addCell(contingut);
			justificant.add(taulaEnviaments);
			justificant.add(Chunk.NEXTPAGE);
		} catch (Exception ex) {
			String errorMessage = messageHelper.getMessage("es.caib.notib.justificant.proces.generant.taula.enviament.error");
			progres.setProgres(100);
			progres.addInfo(TipusInfo.INFO, errorMessage);
			logger.debug(errorMessage, ex);
		}
	}
	
	private PdfPCell getTitolEnviament(int numEnviament, NotificacioDtoV2 notificacio) {
		logger.debug("Generant el títol de la taula d'enviament del justificant d'enviament de la notificació [notificacioId=" + notificacio.getId() + "]");
		PdfPCell titolCell = new PdfPCell();
		String titolEnviamentMessage = messageHelper.getMessage(
				"es.caib.notib.justificant.enviaments.taula.titol", 
				new Object[] {
						numEnviament, 
						notificacio.getEnviaments().size()});
		Paragraph titolParagraph = new Paragraph(titolEnviamentMessage, calibriWhiteBold);
		titolParagraph.setAlignment(Element.ALIGN_CENTER);
		titolCell.addElement(titolParagraph);
		titolCell.setPaddingBottom(6f);
		titolCell.setBackgroundColor(new BaseColor(166, 166, 166));
		titolCell.setBorderWidth((float) 0.5);
		titolCell.setBorderColor(new BaseColor(166, 166, 166));
		return titolCell;
	}
	
	private PdfPCell getTitolAnnexos(Long notificacioId) {
		logger.debug("Generant el títol de la taula d'annexos del justificant d'enviament de la notificació [notificacioId=" + notificacioId + "]");
		PdfPCell titolCell = new PdfPCell();
		String titolDocumentMessage = messageHelper.getMessage("es.caib.notib.justificant.annexos.taula.titol");
		Paragraph titolParagraph = new Paragraph(titolDocumentMessage, calibriWhiteBold);
		titolParagraph.setAlignment(Element.ALIGN_CENTER);
		titolCell.addElement(titolParagraph);
		titolCell.setPaddingBottom(6f);
		titolCell.setBackgroundColor(new BaseColor(166, 166, 166));
		titolCell.setBorderWidth((float) 0.5);
		titolCell.setBorderColor(new BaseColor(166, 166, 166));
		return titolCell;
	}
	
	private PdfPCell getContingutEnviament(
			NotificacioEnviamentDtoV2 enviament, 
			NotificacioDtoV2 notificacio) throws DocumentException {
		logger.debug("Generant el contingut de la taula d'enviament del justificant d'enviament de l'enviament [enviamentId=" + enviament.getId() + "]");
		int[] headingTablewidths = {45, 55};
		PdfPCell contingutCell = new PdfPCell();
//		######## INICI DADES REGISTRE #########
//		## [DADES REGISTRE - TÍTOL]
		Paragraph dadesRegistreTitol = new Paragraph(messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.dades.registre"), calibri10Bold);
		dadesRegistreTitol.setAlignment(Element.ALIGN_LEFT);

//		## [DADES REGISTRE - CONTINGUT]
		Paragraph dadesRegistre = new Paragraph();
		PdfPTable dadesRegistreTable = new PdfPTable(2);
		dadesRegistreTable.setWidthPercentage(95f);
		dadesRegistreTable.setWidths(headingTablewidths);
		
//		## [DADES REGISTRE - NÚMERO]
		String numRegistreMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.dades.registre.numero");
		Chunk dadesRegistreNumeroTitleChunk = new Chunk(numRegistreMessage, calibri10);
		Chunk dadesRegistreNumeroContentChunk = new Chunk(enviament.getRegistreNumeroFormatat(), calibri10);
		createNewTableContent(dadesRegistreTable, dadesRegistreNumeroTitleChunk, dadesRegistreNumeroContentChunk);
		
//		## [DADES REGISTRE - DATA]
		String dataRegistreMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.dades.registre.data");
		Chunk dadesRegistreDataTitleChunk = new Chunk(dataRegistreMessage, calibri10);
		Chunk dadesRegistreDataContentChunk = new Chunk(getDateTimeFormatted(enviament.getRegistreData()), calibri10);
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
	        String dadesNotificaMessage = messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.dades.notifica");
	     	dadesNotificaTitol = new Paragraph(dadesNotificaMessage, calibri10Bold);
	     	dadesNotificaTitol.setAlignment(Element.ALIGN_LEFT);
	     	
//	     	## [DADES NOTIFICA - CONTINGUT]
	     	dadesNotifica = new Paragraph();
	     	PdfPTable dadesNotificaTable = new PdfPTable(2);
	     	dadesNotificaTable.setWidthPercentage(95f);
	     	dadesNotificaTable.setWidths(headingTablewidths);
	     	
//	     	## [DADES NOTIFICA - IDENTIFICADOR]
	     	String dadesNotificaIdentificador = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.dades.notifica.identificador");
	     	Chunk dadesNotificaIdentTitleChunk = new Chunk(dadesNotificaIdentificador, calibri10);
	     	Chunk dadesNotificaIdentContentChunk = new Chunk(enviament.getNotificaIdentificador(), calibri10);
			createNewTableContent(dadesNotificaTable, dadesNotificaIdentTitleChunk, dadesNotificaIdentContentChunk);
	     
//	     	## [DADES NOTIFICA - EMISOR]
	     	String dadesNotificaEmisor = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.dades.notifica.emisor");
	     	Chunk dadesNotificaEmisorTitleChunk = new Chunk(dadesNotificaEmisor, calibri10);
	     	Chunk dadesNotificaEmisorContentChunk = new Chunk("[" + notificacio.getOrganGestor() + "] " + notificacio.getOrganGestorNom(), calibri10);
			createNewTableContent(dadesNotificaTable, dadesNotificaEmisorTitleChunk, dadesNotificaEmisorContentChunk);
			
//	     	## [DADES NOTIFICA - PROCEDIMENT]
			if (notificacio.getProcediment() != null) {
				String dadesNotificaProcediment = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.dades.notifica.procediment");
	     		Chunk dadesNotificaProcedimentTitleChunk = new Chunk(dadesNotificaProcediment, calibri10);
	     		Chunk dadesNotificaProcedimentContentChunk = new Chunk("[" + notificacio.getProcediment().getCodi() + "] " + notificacio.getProcediment().getNom(), calibri10);
	     		createNewTableContent(dadesNotificaTable, dadesNotificaProcedimentTitleChunk, dadesNotificaProcedimentContentChunk);
			}
			
	     	dadesNotifica.setSpacingBefore(5f);
	     	dadesNotifica.setSpacingAfter(5f);
	        dadesNotifica.add(dadesNotificaTable);
//		######## FI DADES NOTIFICA #########
        }
//		######## INICI DADES INTERESSATS #########
     	Paragraph dadesInteressatsTitol = new Paragraph(messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessats"), calibri10Bold);
     	dadesInteressatsTitol.setAlignment(Element.ALIGN_LEFT);
     	
//		######## INICI DADES TITULAR #########
//     	## [DADES INTERESSATS - TITULAR]
     	Paragraph dadesInteressatsTitularTitol = new Paragraph(messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.titular"), calibri10);
     	dadesInteressatsTitularTitol.setAlignment(Element.ALIGN_LEFT);
     	dadesInteressatsTitularTitol.setIndentationLeft(16f);
     	dadesInteressatsTitularTitol.setSpacingBefore(10f);
     	PersonaDto titular = enviament.getTitular();
     	
     	Paragraph dadesTitular = new Paragraph();
     	PdfPTable dadesTitularTable = new PdfPTable(2);
     	dadesTitularTable.setWidthPercentage(80f);
     	dadesTitularTable.setWidths(headingTablewidths);
     	
     	String titularNomMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.titular.nom");
     	Chunk dadesTitularNomTitleChunk = new Chunk(titularNomMessage, calibri10);
     	Chunk dadesTitularNomContentChunk = new Chunk(getNomInteressat(titular), calibri10);
     	createNewTableContent(dadesTitularTable, dadesTitularNomTitleChunk, dadesTitularNomContentChunk);
     	
     	if (titular.getInteressatTipus().equals(InteressatTipusEnumDto.FISICA)) {
	     	String titularLlintgMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.titular.llinatges");
	     	Chunk dadesTitularLlintgTitleChunk = new Chunk(titularLlintgMessage, calibri10);
	     	Chunk dadesTitularLlintgContentChunk = new Chunk(titular.getLlinatges(), calibri10);
	     	createNewTableContent(dadesTitularTable, dadesTitularLlintgTitleChunk, dadesTitularLlintgContentChunk);
     	}
     	
     	if (titular.getNif() != null) {
	     	String titularNifMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.titular.nif");
	     	Chunk dadesTitularNifTitleChunk = new Chunk(titularNifMessage, calibri10);
	     	Chunk dadesTitularNifContentChunk = new Chunk(titular.getNif(), calibri10);
	     	createNewTableContent(dadesTitularTable, dadesTitularNifTitleChunk, dadesTitularNifContentChunk);
     	}
     	if (titular.getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO)) {
	     	String titularDir3Message = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.titular.dir3");
	     	Chunk dadesTitularDir3TitleChunk = new Chunk(titularDir3Message, calibri10);
	     	Chunk dadesTitularDir3ContentChunk = new Chunk(titular.getDir3Codi(), calibri10);
	     	createNewTableContent(dadesTitularTable, dadesTitularDir3TitleChunk, dadesTitularDir3ContentChunk);
     	}
//		######## FI DADES TITULAR #########
     	dadesTitular.add(dadesTitularTable);
        
     	contingutCell.addElement(dadesRegistreTitol);
     	contingutCell.addElement(dadesRegistre);
     	if (dadesNotificaTitol != null)
     		contingutCell.addElement(dadesNotificaTitol);
     	if (dadesNotifica != null)
     		contingutCell.addElement(dadesNotifica);
     	contingutCell.addElement(dadesInteressatsTitol);
     	contingutCell.addElement(dadesInteressatsTitularTitol);
     	contingutCell.addElement(dadesTitular);
     	
//		######## INICI DADES DESTINATARIS #########
        int numDestinatari = 1;
        for (PersonaDto destinatari : enviament.getDestinataris()) {
//     		## [DESTINATARI]
        	String dadesInteressatsDestinatariMessage = messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.destinatari", new Object[] {numDestinatari});
         	Paragraph dadesInteressatsDestinatariTitol = new Paragraph(dadesInteressatsDestinatariMessage, calibri10);
         	dadesInteressatsDestinatariTitol.setAlignment(Element.ALIGN_LEFT);
         	dadesInteressatsDestinatariTitol.setIndentationLeft(16f);
         	dadesInteressatsDestinatariTitol.setSpacingBefore(10f);
         	
         	Paragraph dadesDestinatari = new Paragraph();
         	PdfPTable dadesDestinatariTable = new PdfPTable(2);
         	dadesDestinatariTable.setWidthPercentage(80f);
         	dadesDestinatariTable.setWidths(headingTablewidths);

         	String destinatariNomMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.destinatari.nom");
         	Chunk dadesDestinatariNomTitleChunk = new Chunk(destinatariNomMessage, calibri10);
         	Chunk dadesDestinatariNomContentChunk = new Chunk(getNomInteressat(destinatari), calibri10);
         	createNewTableContent(dadesDestinatariTable, dadesDestinatariNomTitleChunk, dadesDestinatariNomContentChunk);
         	
         	if (destinatari.getInteressatTipus().equals(InteressatTipusEnumDto.FISICA)) {
	         	String destinatariLlintgMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.destinatari.llinatges");
	         	Chunk dadesDestinatariLlintgTitleChunk = new Chunk(destinatariLlintgMessage, calibri10);
	         	Chunk dadesDestinatariLlintgContentChunk = new Chunk(destinatari.getLlinatges(), calibri10);
	         	createNewTableContent(dadesDestinatariTable, dadesDestinatariLlintgTitleChunk, dadesDestinatariLlintgContentChunk);
         	}
         	
         	if (destinatari.getNif() != null) {
	         	String destinatariNifMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.destinatari.nif");
	         	Chunk dadesDestinatariNifTitleChunk = new Chunk(destinatariNifMessage, calibri10);
	         	Chunk dadesDestinatariNifContentChunk = new Chunk(destinatari.getNif(), calibri10);
	         	createNewTableContent(dadesDestinatariTable, dadesDestinatariNifTitleChunk, dadesDestinatariNifContentChunk);
         	}
         	if (destinatari.getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO)) {
	         	String destinatariDir3Message = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.destinatari.dir3");
	         	Chunk dadesDestinatariDir3TitleChunk = new Chunk(destinatariDir3Message, calibri10);
	         	Chunk dadesDestinatariDir3ContentChunk = new Chunk(destinatari.getDir3Codi(), calibri10);
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
     	PdfPCell procedimentTitleCell = new PdfPCell();
		List listProcediment = new List(List.UNORDERED);
		listProcediment.add(new ListItem(chunks[0]));
		procedimentTitleCell.addElement(listProcediment);
		procedimentTitleCell.setBorder(Rectangle.NO_BORDER);
		
//		## [VALOR]
		PdfPCell procedimentContentCell = new PdfPCell();
		procedimentContentCell.addElement(chunks[1]);
		procedimentContentCell.setBorder(Rectangle.NO_BORDER);
		
		table.addCell(procedimentTitleCell);
		table.addCell(procedimentContentCell);
	}	
	
	private void crearTaulaAnnexos(PdfPTable taulaAnnexos,
			Document justificant,
			Long notificacioId,
			NotificacioDtoV2 notificacio,
			ProgresDescarregaDto progres) throws JustificantException {
		logger.debug("Generant la taula de annexos del justificant d'enviament per als documents.");
		try {
//			## [CONTINGUT]
			int[] headingTablewidths = {13, 8, 13, 13, 18, 14, 13, 8};
			PdfPTable dadesAnnexoTable = new PdfPTable(8);
			dadesAnnexoTable.setWidthPercentage(100f);
			dadesAnnexoTable.setWidths(headingTablewidths);
			
			getHeadersAnnexos(dadesAnnexoTable);
			if (notificacio.getDocument() != null)
				getContingutAnnexos(notificacio.getDocument(), dadesAnnexoTable);
			if (notificacio.getDocument2() != null)
				getContingutAnnexos(notificacio.getDocument2(), dadesAnnexoTable);
			if (notificacio.getDocument3() != null)
				getContingutAnnexos(notificacio.getDocument3(), dadesAnnexoTable);
			if (notificacio.getDocument4() != null)
				getContingutAnnexos(notificacio.getDocument4(), dadesAnnexoTable);
			if (notificacio.getDocument5() != null)
				getContingutAnnexos(notificacio.getDocument5(), dadesAnnexoTable);
			
			PdfPCell contingut = new PdfPCell();
			contingut.setBorder(PdfPCell.NO_BORDER);
			contingut.addElement(dadesAnnexoTable);
			
	        //## [CONFIGURACIÓ CELLA CONTINGUT]
//			contingut.setPaddingLeft(7f);
//			contingut.setPaddingBottom(15f);
//			contingut.setBorderWidth((float) 0.5);
//			contingut.setBorderColor(new BaseColor(166, 166, 166));

			taulaAnnexos.addCell(contingut);
			justificant.add(taulaAnnexos);
			justificant.add(Chunk.NEXTPAGE);
		} catch (Exception ex) {
			String errorMessage = messageHelper.getMessage("es.caib.notib.justificant.proces.generant.taula.annexos.error");
			progres.setProgres(100);
			progres.addInfo(TipusInfo.INFO, errorMessage);
			logger.debug(errorMessage, ex);
		}
	}
	
	private void getHeadersAnnexos(PdfPTable dadesAnnexoTable) throws DocumentException {
//		## [NOM - TÍTOL]
		Paragraph dadesAnnexoTitol = new Paragraph(messageHelper.getMessage("es.caib.notib.justificant.annexos.taula.arxiuNom"), calibri10Bold);
		createNewTableAnnexosHeader(dadesAnnexoTitol, dadesAnnexoTable);
//		## [MIDA - TÍTOL]
		dadesAnnexoTitol = new Paragraph(messageHelper.getMessage("es.caib.notib.justificant.annexos.taula.mida"), calibri10Bold);
		createNewTableAnnexosHeader(dadesAnnexoTitol, dadesAnnexoTable);
//		## [CSV - TÍTOL]
		dadesAnnexoTitol = new Paragraph(messageHelper.getMessage("es.caib.notib.justificant.annexos.taula.csv"), calibri10Bold);
		createNewTableAnnexosHeader(dadesAnnexoTitol, dadesAnnexoTable);
//		## [UUID - TÍTOL]
		dadesAnnexoTitol = new Paragraph(messageHelper.getMessage("es.caib.notib.justificant.annexos.taula.uuid"), calibri10Bold);
		createNewTableAnnexosHeader(dadesAnnexoTitol, dadesAnnexoTable);
//		## [ORIGEN - TÍTOL]
		dadesAnnexoTitol = new Paragraph(messageHelper.getMessage("es.caib.notib.justificant.annexos.taula.origen"), calibri10Bold);
		createNewTableAnnexosHeader(dadesAnnexoTitol, dadesAnnexoTable);
//		## [VALIDESA - TÍTOL]
		dadesAnnexoTitol = new Paragraph(messageHelper.getMessage("es.caib.notib.justificant.annexos.taula.validesa"), calibri10Bold);
		createNewTableAnnexosHeader(dadesAnnexoTitol, dadesAnnexoTable);
//		## [TIPODOCUMENTAL - TÍTOL]
		dadesAnnexoTitol = new Paragraph(messageHelper.getMessage("es.caib.notib.justificant.annexos.taula.tipoDocumental"), calibri10Bold);
		createNewTableAnnexosHeader(dadesAnnexoTitol, dadesAnnexoTable);
//		## [MODOFIRMA - TÍTOL]
		dadesAnnexoTitol = new Paragraph(messageHelper.getMessage("es.caib.notib.justificant.annexos.taula.modoFirma"), calibri10Bold);
		createNewTableAnnexosHeader(dadesAnnexoTitol, dadesAnnexoTable);
	}

	private void createNewTableAnnexosHeader(Paragraph dadesAnnexoTitol, PdfPTable dadesAnnexoTable) {
		dadesAnnexoTitol.setAlignment(Element.ALIGN_LEFT);
		PdfPCell contingutCell = new PdfPCell();
		contingutCell.addElement(dadesAnnexoTitol);
		dadesAnnexoTable.addCell(contingutCell);
	}
	
	private void getContingutAnnexos(DocumentDto document, PdfPTable dadesAnnexoTable) throws DocumentException {
//		## [CONTINGUT COLUMNES]
		createNewTableAnnexosContent(dadesAnnexoTable, new Chunk((document.getArxiuNom() != null && !document.getArxiuNom().isEmpty()) ? document.getArxiuNom() : "", calibri10));
		createNewTableAnnexosContent(dadesAnnexoTable, new Chunk(document.getMida() != null ? document.getMida().toString() : "", calibri10));
		createNewTableAnnexosContent(dadesAnnexoTable, new Chunk((document.getCsv() != null && !document.getCsv().isEmpty()) ? document.getCsv() : "", calibri10));
		createNewTableAnnexosContent(dadesAnnexoTable, new Chunk((document.getUuid() != null && !document.getUuid().isEmpty()) ? document.getUuid() : "", calibri10));
		createNewTableAnnexosContent(dadesAnnexoTable, new Chunk(document.getOrigen() != null ? document.getOrigen().toString() : "", calibri10));
		createNewTableAnnexosContent(dadesAnnexoTable, new Chunk(document.getValidesa() != null ? document.getValidesa().toString() : "", calibri10));
		createNewTableAnnexosContent(dadesAnnexoTable, new Chunk(document.getTipoDocumental() != null ? document.getTipoDocumental().toString() : "", calibri10));
		createNewTableAnnexosContent(dadesAnnexoTable, new Chunk(document.getModoFirma() != null ? document.getModoFirma().toString() : "", calibri10));
	}
	
	private void createNewTableAnnexosContent(PdfPTable table, Chunk chunk) {
		PdfPCell procedimentContentCell = new PdfPCell();
		procedimentContentCell.addElement(chunk);
		procedimentContentCell.setBorderWidth(0.25f);
		table.addCell(procedimentContentCell);
	}	
	
	private Document inicialitzaDocument(
			ByteArrayOutputStream out,
			ProgresDescarregaDto progres) throws DocumentException, JustificantException {
		logger.debug("Inicialitzant el document per la generació del justificant d'enviament");
//		## [Event per crear el header]
		HeaderPageEvent headerEvent = new HeaderPageEvent(progres);
//		## [Event per crear el footer]
		FooterPageEvent footerEvent = new FooterPageEvent(progres);
		
	    Document justificant = new Document(PageSize.A4, 36, 36, 35 + headerEvent.getTableHeight(), 36);
		PdfWriter writer = PdfWriter.getInstance(justificant, out);
//		writer.setViewerPreferences(PdfWriter.ALLOW_PRINTING);
		
		writer.setPageEvent(headerEvent);
		writer.setPageEvent(footerEvent);
		
		justificant.open();
		justificant.addAuthor("Notib");
		justificant.addCreationDate();
		justificant.addCreator("iText library");

		return justificant;
	}
	
	private class HeaderPageEvent extends PdfPageEventHelper {
		private PdfPTable header;
		private float tableHeight;
	    
		
	    public float getTableHeight() {
			return tableHeight;
		}

		public void onEndPage(PdfWriter writer, Document justificant) {
			header.writeSelectedRows(
					0, 
					-1,
					justificant.left(),
					750 + ((justificant.topMargin() + tableHeight) / 2),
                    writer.getDirectContent());
	    }
	    
		private HeaderPageEvent(ProgresDescarregaDto progres) throws JustificantException {
			String accioDescripcio = messageHelper.getMessage("es.caib.notib.justificant.proces.iniciant.header");
			logger.debug(accioDescripcio);
			progres.setProgres(15);
			progres.addInfo(TipusInfo.INFO, accioDescripcio);
			try {
				PdfPCell cellDireccio = new PdfPCell();
				header = new PdfPTable(2);
				header.setTotalWidth(523);
				header.setLockedWidth(true);
				Image logoCapsalera = null;
				
//				## [LOGO ENTITAT]
				if (getCapsaleraLogo() != null) {
					logoCapsalera = Image.getInstance(getCapsaleraLogo());
				} else {
					byte[] logoBytes = IOUtils.toByteArray(getCapsaleraDefaultLogo());
					logoCapsalera = Image.getInstance(logoBytes);
				}
				if (logoCapsalera != null) {
					logoCapsalera.scaleToFit(120f, 50f);
					PdfPCell cellLogo = new PdfPCell(logoCapsalera);
					cellLogo.setHorizontalAlignment(Element.ALIGN_LEFT);
					cellLogo.setBorder(Rectangle.NO_BORDER);
					header.addCell(cellLogo);
				}
				if (getNifDireccio() != null) {
//					## [DIRECCIO - NIF]
					Paragraph direccioNif = new Paragraph(getNifDireccio(), frutiger8);
					direccioNif.setAlignment(Element.ALIGN_RIGHT);
					direccioNif.setLeading(0, (float) 1.25);
					cellDireccio.addElement(direccioNif);
				}
				if (getCodiDireccio() != null) {
//					## [DIRECCIO - CODIDIR3]
					Paragraph direccioCodi = new Paragraph(getCodiDireccio(), frutiger8);
					direccioCodi.setAlignment(Element.ALIGN_RIGHT);
					direccioCodi.setLeading(0, (float) 1.25);
					cellDireccio.addElement(direccioCodi);
				}
				if (getDireccio() != null) {
//					## [DIRECCIO - CARRER]
					Paragraph direccio = new Paragraph(getDireccio(), frutiger8);
					direccio.setAlignment(Element.ALIGN_RIGHT);
					direccio.setLeading(0, (float) 1.25);
					cellDireccio.addElement(direccio);
				}
				if (getEmailDireccio() != null) {
//					## [DIRECCIO - EMAIL]
					Chunk direccioEmailChunk = new Chunk(getEmailDireccio(), frutiger8);
					direccioEmailChunk.setUnderline(1.5f, -1);
					Paragraph direccioEmail = new Paragraph(direccioEmailChunk);
					direccioEmail.setAlignment(Element.ALIGN_RIGHT);
					direccioEmail.setLeading(0, (float) 1.25);
					cellDireccio.addElement(direccioEmail);
				}

				cellDireccio.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cellDireccio.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cellDireccio.setBorder(Rectangle.NO_BORDER);
				header.addCell(cellDireccio);
				tableHeight = header.getTotalHeight();
			} catch (Exception ex) {
				String errorMessage = messageHelper.getMessage("es.caib.notib.justificant.proces.iniciant.header.error");
				progres.setProgres(100);
				progres.addInfo(TipusInfo.ERROR, errorMessage);
				logger.error(errorMessage, ex);
			}
		}
	}
	
	private class FooterPageEvent extends PdfPageEventHelper {
		private PdfPTable footer;

		public void onEndPage(PdfWriter writer, Document justificant) {
			footer.writeSelectedRows(
					0, 
					-1, 
					36, 
					80, 
					writer.getDirectContent());
	    }
	    
		private FooterPageEvent(ProgresDescarregaDto progres) throws JustificantException {
			String accioDescripcio = messageHelper.getMessage("es.caib.notib.justificant.proces.iniciant.footer");
			logger.debug(accioDescripcio);
			progres.setProgres(20);
			progres.addInfo(TipusInfo.INFO, accioDescripcio);
			try {
				footer = new PdfPTable(2);
				footer.setTotalWidth(523);
				footer.setLockedWidth(true);
				Image logoPeu = null;
				
				if (getPeuTitol() != null) {
//					## [PEU - TÍTOL]
					Paragraph peuTitol = new Paragraph(getPeuTitol(), frutiger9);
					peuTitol.setAlignment(Element.ALIGN_LEFT);
	
					PdfPCell cellTitolPeu = new PdfPCell();
					cellTitolPeu.addElement(peuTitol);
					cellTitolPeu.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cellTitolPeu.setBorder(Rectangle.NO_BORDER);
					footer.addCell(cellTitolPeu);
				}

//				## [LOGO ENTITAT]
				if (getPeuLogo() != null) {
					logoPeu = Image.getInstance(getPeuLogo());
				} else {
					byte[] logoBytes = IOUtils.toByteArray(getPeuDefaultLogo());
					logoPeu = Image.getInstance(logoBytes);
				}
				if (logoPeu != null) {
//					## [PEU - LOGO]
					logoPeu.setScaleToFitHeight(true);
					logoPeu.scaleToFit(100, 80);
					PdfPCell cellLogo = new PdfPCell(logoPeu);
					cellLogo.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cellLogo.setBorder(Rectangle.NO_BORDER);
					footer.addCell(cellLogo);
				}
			} catch (Exception ex) {
				String errorMessage = messageHelper.getMessage("es.caib.notib.justificant.proces.iniciant.footer.error");
				progres.setProgres(100);
				progres.addInfo(TipusInfo.ERROR, errorMessage);
				logger.error(errorMessage, ex);
			}
		}
	}
	
	private void setParametersBold(Paragraph paragraph, String content) {
		Chunk cacacter = new Chunk();
		for (int i = 0; i < content.length(); i++) {
			if (content.charAt(i) == '[') {
				while (content.charAt(i + 1) != ']') {
					cacacter = new Chunk(String.valueOf(content.charAt(i + 1)), calibri10Bold);
					paragraph.add(cacacter);
					i++;
					continue;
				}
			} else {
				if (content.charAt(i) != '[' && content.charAt(i) != ']') {
					cacacter = new Chunk(String.valueOf(content.charAt(i)), calibri10);
					paragraph.add(cacacter);
				}
			}
		}
	}
	
	private String getNomInteressat(PersonaDto persona)  {
		switch (persona.getInteressatTipus()) {
		case FISICA:
			return persona.getNom();
		case ADMINISTRACIO:
			return persona.getNom();
		case JURIDICA:
			return persona.getRaoSocial() != null ? persona.getRaoSocial() : persona.getNom();
		default:
			return persona.getRaoSocial();
		}
	}
	
	private String getDateTimeFormatted(Date date)  {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		long now = date.getTime();
		return formatter.format(now);
	}

	private String getDireccio() {
		return getNotBlankProperty(PropertiesHelper.getProperties().getProperty("es.caib.notib.justificant.capsalera.direccio"));
	}

	private String getNifDireccio() {
		return getNotBlankProperty(PropertiesHelper.getProperties().getProperty("es.caib.notib.justificant.capsalera.nif"));
	}

	private String getCodiDireccio() {
		return getNotBlankProperty(PropertiesHelper.getProperties().getProperty("es.caib.notib.justificant.capsalera.codi"));
	}

	private String getEmailDireccio() {
		return getNotBlankProperty(PropertiesHelper.getProperties().getProperty("es.caib.notib.justificant.capsalera.email"));
	}

	private String getCapsaleraLogo() {
		return getNotBlankProperty(PropertiesHelper.getProperties().getProperty("es.caib.notib.justificant.capsalera.logo"));
	}
	
	private InputStream getCapsaleraDefaultLogo() {
		return getClass().getResourceAsStream("/es/caib/notib/core/justificant/govern-logo.png");
	}
	
	private InputStream getPeuDefaultLogo() {
		return getClass().getResourceAsStream("/es/caib/notib/core/justificant/logo.png");
	}

	private String getPeuLogo() {
		return getNotBlankProperty(PropertiesHelper.getProperties().getProperty("es.caib.notib.justificant.peu.logo"));
	}

	private String getPeuTitol() {
		return getNotBlankProperty(PropertiesHelper.getProperties().getProperty("es.caib.notib.justificant.peu.titol"));
	}

	private String getNotBlankProperty(String property) {
		if (property == null || property.trim().isEmpty())
			return null;
		return property.trim();
	}

	private static final Logger logger = LoggerFactory.getLogger(JustificantHelper.class);
}

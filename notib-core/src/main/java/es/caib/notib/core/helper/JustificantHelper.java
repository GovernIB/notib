package es.caib.notib.core.helper;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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

import es.caib.notib.core.api.dto.InteressatTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioDtoV2;
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
			//## [TAULA QUE CONTÉ TÍTOL I INTRODUCCIÓ]
			PdfPTable titolIntroduccioTable = new PdfPTable(1);
			titolIntroduccioTable.setWidthPercentage(100);
			PdfPCell titolIntroduccioCell = new PdfPCell();
			titolIntroduccioCell.setBorder(Rectangle.NO_BORDER);
			
			//## [TITOL JUSTIFICANT]
			String titolMessage = messageHelper.getMessage("es.caib.notib.justificant.titol");
			Paragraph justificantTitol = new Paragraph(titolMessage, frutigerTitolBold);
			justificantTitol.setAlignment(Element.ALIGN_CENTER);
			justificantTitol.add(Chunk.NEWLINE);
			justificantTitol.add(Chunk.NEWLINE);
			
			//## [INTRODUCCIÓ JUSTIFICANT]
			String introduccio = messageHelper.getMessage(
					"es.caib.notib.justificant.introduccio", 
					new Object[] {
							messageHelper.getMessage("es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto." + notificacio.getEnviamentTipus().name()),
							notificacio.getConcepte(),
							getDateTimeFormatted(notificacio.getEnviaments().get(0).getNotificaEstatData())});
			Paragraph justificantIntroduccio = new Paragraph();
			setParametersBold(justificantIntroduccio, introduccio);
			justificantIntroduccio.add(Chunk.NEWLINE);
			
			//## [INTRODUCCIÓ ENVIAMENTS JUSTIFICANT]
			String introduccioEnviaments = messageHelper.getMessage("es.caib.notib.justificant.enviaments.titol", new Object[] {notificacio.getEnviaments().size()});
			Paragraph justificantIntroduccioEnviaments = new Paragraph();
			setParametersBold(justificantIntroduccioEnviaments, introduccioEnviaments);
			justificantIntroduccioEnviaments.setSpacingBefore(10f);
			
			titolIntroduccioCell.addElement(justificantTitol);
			titolIntroduccioCell.addElement(justificantIntroduccio);
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
			
			//## [TÍTOL]
			PdfPCell titolCell = getTitolEnviament(numEnviament, notificacio);
			
			//## [CONTINGUT]
			PdfPCell contingut = getContingutEnviament(enviament);
			
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
	
	private PdfPCell getContingutEnviament(NotificacioEnviamentDtoV2 enviament) {
		logger.debug("Generant el contingut de la taula d'enviament del justificant d'enviament de l'enviament [enviamentId=" + enviament.getId() + "]");
		PdfPCell contingutCell = new PdfPCell();
		// ## [DADES REGISTRE - TÍTOL]
		Paragraph dadesRegistreTitol = new Paragraph(messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.dades.registre"), calibri10Bold);
		dadesRegistreTitol.setAlignment(Element.ALIGN_LEFT);

		// ## [DADES REGISTRE - CONTINGUT]
		Paragraph dadesRegistre = new Paragraph();
		List listRegistre = new List(List.UNORDERED);
		// ## [DADES REGISTRE - NÚMERO]
		String numRegistreMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.dades.registre.numero", new Object[] {enviament.getRegistreNumeroFormatat()});
		Chunk dadesRegistreNumeroChunk = new Chunk(numRegistreMessage, calibri10);
		// ## [DADES REGISTRE - DATA]
		String dataRegistreMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.dades.registre.data", new Object[] {getDateTimeFormatted(enviament.getRegistreData())});
		Chunk dadesRegistreDataChunk = new Chunk(dataRegistreMessage, calibri10);
		
		//## [CREACIÓ LLISTA REGISTRE] ####
		listRegistre.add(new ListItem(dadesRegistreNumeroChunk));
		listRegistre.add(new ListItem(dadesRegistreDataChunk));
		listRegistre.setIndentationLeft(16f);
		dadesRegistre.setSpacingBefore(10f);
		dadesRegistre.setSpacingAfter(10f);
        dadesRegistre.add(listRegistre);
        
        // ## [DADES NOTIFICA]
        String dadesNotificaMessage = messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.dades.notifica");
     	Paragraph dadesNotificaTitol = new Paragraph(dadesNotificaMessage, calibri10Bold);
     	dadesNotificaTitol.setAlignment(Element.ALIGN_LEFT);
     	
     	// ## [DADES NOTIFICA - CONTINGUT]
     	Paragraph dadesNotifica = new Paragraph();
     	List listNotifica = new List(List.UNORDERED);
     	// ## [DADES NOTIFICA - IDENTIFICADOR]
     	String dadesNotificaIdentificador = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.dades.notifica.identificador", new Object[] {enviament.getNotificaIdentificador()});
     	Chunk dadesNotificaIdentChunk = new Chunk(dadesNotificaIdentificador, calibri10);
     	// ## [DADES NOTIFICA - ESTAT]
     	String dadesNotificaEstat = "   " + messageHelper.getMessage(
     			"es.caib.notib.justificant.enviaments.taula.dades.notifica.estat", 
     			new Object[] {
     					messageHelper.getMessage("es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto." + enviament.getNotificaEstat().name())});
     	Chunk dadesNotificaEstatChunk = new Chunk(dadesNotificaEstat, calibri10);
     	// ## [DADES NOTIFICA - DATA]
     	String dadesNotificaData = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.dades.notifica.data", new Object[] {getDateTimeFormatted(enviament.getNotificaEstatData())});
     	Chunk dadesNotificaDataChunk = new Chunk(dadesNotificaData, calibri10);
     	
     	//## [CREACIÓ LLISTA NOTIFIC@] ####
     	listNotifica.add(new ListItem(dadesNotificaIdentChunk));
     	listNotifica.add(new ListItem(dadesNotificaEstatChunk));
     	listNotifica.add(new ListItem(dadesNotificaDataChunk));
     	listNotifica.setIndentationLeft(16f);
     	dadesNotifica.setSpacingBefore(10f);
     	dadesNotifica.setSpacingAfter(10f);
        dadesNotifica.add(listNotifica);
            
        // ## [DADES INTERESSATS]
     	Paragraph dadesInteressatsTitol = new Paragraph(messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessats"), calibri10Bold);
     	dadesInteressatsTitol.setAlignment(Element.ALIGN_LEFT);
     	
     	// ## [DADES INTERESSATS - TITULAR]
     	Paragraph dadesInteressatsTitularTitol = new Paragraph(messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.titular"), calibri10);
     	dadesInteressatsTitularTitol.setAlignment(Element.ALIGN_LEFT);
     	dadesInteressatsTitularTitol.setIndentationLeft(16f);
     	dadesInteressatsTitularTitol.setSpacingBefore(10f);
     	PersonaDto titular = enviament.getTitular();
     	
     	Paragraph dadesTitular = new Paragraph();
     	List listTitular = new List(List.UNORDERED);
     	String titularNomMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.titular.nom", new Object[] {getNomInteressat(titular)});
     	Chunk dadesTitularNomChunk = new Chunk(titularNomMessage, calibri10);
     	String titularLlintgMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.titular.llinatges", new Object[] {titular.getLlinatges()});
     	Chunk dadesTitularLlintgChunk = new Chunk(titularLlintgMessage, calibri10);
     	String titularNifMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.titular.nif", new Object[] {titular.getNif()});
     	Chunk dadesTitularNifChunk = new Chunk(titularNifMessage, calibri10);
     	String titularDir3Message = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.titular.dir3", new Object[] {titular.getDir3Codi()});
     	Chunk dadesTitularDir3Chunk = new Chunk(titularDir3Message, calibri10);
     	
     	//## [CREACIÓ LLISTA TITULAR] ####
     	listTitular.add(new ListItem(dadesTitularNomChunk));
     	if (titular.getInteressatTipus().equals(InteressatTipusEnumDto.FISICA))
     		listTitular.add(new ListItem(dadesTitularLlintgChunk));
     	listTitular.add(new ListItem(dadesTitularNifChunk));
     	if (titular.getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO))
     		listTitular.add(new ListItem(dadesTitularDir3Chunk));
     	listTitular.setIndentationLeft(35f);
     	dadesTitular.setSpacingBefore(10f);
     	dadesTitular.add(listTitular);
        
     	contingutCell.addElement(dadesRegistreTitol);
     	contingutCell.addElement(dadesRegistre);
     	contingutCell.addElement(dadesNotificaTitol);
     	contingutCell.addElement(dadesNotifica);
     	contingutCell.addElement(dadesInteressatsTitol);
     	contingutCell.addElement(dadesInteressatsTitularTitol);
     	contingutCell.addElement(dadesTitular);
     	
        //## [DADES INTERESSATS - DESTINATARIS]
        int numDestinatari = 1;
        for (PersonaDto destinatari : enviament.getDestinataris()) {
     		// ## [DADES INTERESSATS - DESTINATARI]
        	String dadesInteressatsDestinatariMessage = messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.destinatari", new Object[] {numDestinatari});
         	Paragraph dadesInteressatsDestinatariTitol = new Paragraph(dadesInteressatsDestinatariMessage, calibri10);
         	dadesInteressatsDestinatariTitol.setAlignment(Element.ALIGN_LEFT);
         	dadesInteressatsDestinatariTitol.setIndentationLeft(16f);
         	dadesInteressatsDestinatariTitol.setSpacingBefore(10f);
         	
         	Paragraph dadesDestinatari = new Paragraph();
         	List listDestinatari = new List(List.UNORDERED);
         	String destinatariNomMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.destinatari.nom", new Object[] {getNomInteressat(destinatari)});
         	Chunk dadesDestinatariNomChunk = new Chunk(destinatariNomMessage, calibri10);
         	String destinatariLlintgMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.destinatari.llinatges", new Object[] {destinatari.getLlinatges()});
         	Chunk dadesDestinatariLlintgChunk = new Chunk(destinatariLlintgMessage, calibri10);
         	String destinatariNifMessage = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.destinatari.nif", new Object[] {destinatari.getNif()});
         	Chunk dadesDestinatariNifChunk = new Chunk(destinatariNifMessage, calibri10);
         	String destinatariDir3Message = "   " + messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.destinatari.dir3", new Object[] {destinatari.getDir3Codi()});
         	Chunk dadesDestinatariDir3Chunk = new Chunk(destinatariDir3Message, calibri10);
         	
         	//## [CREACIÓ LLISTA DESTINATARI] ####
         	listDestinatari.add(new ListItem(dadesDestinatariNomChunk));
         	if (destinatari.getInteressatTipus().equals(InteressatTipusEnumDto.FISICA))
         		listDestinatari.add(new ListItem(dadesDestinatariLlintgChunk));
         	listDestinatari.add(new ListItem(dadesDestinatariNifChunk));
         	if (destinatari.getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO))
         		listDestinatari.add(new ListItem(dadesDestinatariDir3Chunk));
         	listDestinatari.setIndentationLeft(35f);
         	dadesDestinatari.setSpacingBefore(10f);
         	dadesDestinatari.add(listDestinatari);
         	
         	contingutCell.addElement(dadesInteressatsDestinatariTitol);
         	contingutCell.addElement(dadesDestinatari);
         	numDestinatari++;
		}

        //## [CONFIGURACIÓ CELLA CONTINGUT]
     	contingutCell.setPaddingLeft(7f);
     	contingutCell.setPaddingBottom(15f);
        contingutCell.setBorderWidth((float) 0.5);
        contingutCell.setBorderColor(new BaseColor(166, 166, 166));
		return contingutCell;
	}

	private Document inicialitzaDocument(
			ByteArrayOutputStream out,
			ProgresDescarregaDto progres) throws DocumentException, JustificantException {
		logger.debug("Inicialitzant el document per la generació del justificant d'enviament");
		//## [Event per crear el header]
		HeaderPageEvent headerEvent = new HeaderPageEvent(progres);
		//## [Event per crear el footer]
		FooterPageEvent footerEvent = new FooterPageEvent(progres);
		
	    Document justificant = new Document(PageSize.A4, 36, 36, 35 + headerEvent.getTableHeight(), 36);
		PdfWriter writer = PdfWriter.getInstance(justificant, out);
		writer.setViewerPreferences(PdfWriter.ALLOW_PRINTING);
		
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
				
				if (getCapsaleraLogo() != null) {
	 				// ## [LOGO ENTITAT]
					Image logoEntitat = Image.getInstance(getCapsaleraLogo());
	//				logoEntitat.setScaleToFitHeight(true);
	//				logoEntitat.scaleToFit(200, 80);
					PdfPCell cellLogo = new PdfPCell(logoEntitat);
					cellLogo.setHorizontalAlignment(Element.ALIGN_LEFT);
					cellLogo.setBorder(Rectangle.NO_BORDER);
					header.addCell(cellLogo);
				}
				if (getNifDireccio() != null) {
					// ## [DIRECCIO - NIF]
					Paragraph direccioNif = new Paragraph(getNifDireccio(), frutiger8);
					direccioNif.setAlignment(Element.ALIGN_RIGHT);
					direccioNif.setLeading(0, (float) 1.25);
					cellDireccio.addElement(direccioNif);
				}
				if (getCodiDireccio() != null) {
					// ## [DIRECCIO - CODIDIR3]
					Paragraph direccioCodi = new Paragraph(getCodiDireccio(), frutiger8);
					direccioCodi.setAlignment(Element.ALIGN_RIGHT);
					direccioCodi.setLeading(0, (float) 1.25);
					cellDireccio.addElement(direccioCodi);
				}
				if (getDireccio() != null) {
					// ## [DIRECCIO - CARRER]
					Paragraph direccio = new Paragraph(getDireccio(), frutiger8);
					direccio.setAlignment(Element.ALIGN_RIGHT);
					direccio.setLeading(0, (float) 1.25);
					cellDireccio.addElement(direccio);
				}
				if (getEmailDireccio() != null) {
					// ## [DIRECCIO - EMAIL]
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

				if (getPeuTitol() != null) {
					// ## [PEU - TÍTOL]
					Paragraph direccioNif = new Paragraph(getPeuTitol(), frutiger9);
					direccioNif.setAlignment(Element.ALIGN_LEFT);
	
					PdfPCell cellDireccio = new PdfPCell();
					cellDireccio.addElement(direccioNif);
					cellDireccio.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cellDireccio.setBorder(Rectangle.NO_BORDER);
					footer.addCell(cellDireccio);
				}
				if (getPeuLogo() != null) {
					// ## [PEU - LOGO]
					Image logoPeu = Image.getInstance(getPeuLogo());
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
	
	//TODO millorar
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
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		long now = date.getTime();
		return formatter.format(now);
	}

	private String getDireccio() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.justificant.capsalera.direccio");
	}

	private String getNifDireccio() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.justificant.capsalera.direccio.nif");
	}

	private String getCodiDireccio() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.justificant.capsalera.direccio.codi");
	}

	private String getEmailDireccio() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.justificant.capsalera.direccio.email");
	}

	private String getCapsaleraLogo() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.justificant.capsalera.logo");
	}

	private String getPeuLogo() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.justificant.peu.logo");
	}

	private String getPeuTitol() {
		return PropertiesHelper.getProperties().getProperty("es.caib.notib.justificant.peu.titol");
	}
	
	private static final Logger logger = LoggerFactory.getLogger(JustificantHelper.class);
}

package es.caib.notib.core.helper;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.BreakClear;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFAbstractNum;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableCell.XWPFVertAlign;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumbering;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.NotificacioDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.PersonaDto;

/**
 * Helper per generar el justificant de la notificació electrònica
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */

@Component
public class JustificantHelper {

	@Autowired
	MessageHelper messageHelper;

	public byte[] generarJustificant(NotificacioDtoV2 notificacio) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {

			XWPFDocument justificant = getDocumentFormatted();

			if (justificant != null) {

				createCapsaleraPagina(justificant);

				createTitle(justificant);

				createIntroduction(justificant, notificacio);

				int numEnviament = 1;
				for (NotificacioEnviamentDtoV2 enviament: notificacio.getEnviaments()) {
					createTableEnviaments(
							justificant, 
							notificacio, 
							enviament, 
							numEnviament);
					numEnviament++;
				}

				createPeuPagina(justificant);
				
				justificant.write(out);
				out.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	private void createCapsaleraPagina(XWPFDocument justificant) {
		try {
			int twipsPerInch = 1440;
//			Capsalera per defecte
			XWPFHeaderFooterPolicy headerFooterPolicy = justificant.getHeaderFooterPolicy();
			XWPFHeader header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);
//			## [CAPSALERA] ##
//			XWPFParagraph paragraph = header.createParagraph();
//			XmlCursor cursor = paragraph.getCTP().newCursor();
//			XWPFTable tableCapsalera = header.insertNewTbl(cursor);
			XWPFTable tableCapsalera = header.createTable(1, 2);
			tableCapsalera.setStyleID("Tablaconcuadrcula");
			tableCapsalera.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf((long) (3.84 * twipsPerInch)));
			tableCapsalera.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf((long) (3.35 * twipsPerInch)));
//			## [NEW_ROW] ## crea la primera fila d'una taula
//			XWPFTableRow firstRow = createFirstTableRow(tableCapsalera);
//			## [LOGO COL] ## first column
			addInfoToHeader(tableCapsalera, 0);
//			## [DIRECCIÓ COL] ## second column
			addInfoToHeader(tableCapsalera, 1);
			
			removeTableBorder(tableCapsalera);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void createPeuPagina(XWPFDocument justificant) {
		try {
			int twipsPerInch = 1725;
//			Peu per defecte
			XWPFHeaderFooterPolicy headerFooterPolicy = justificant.getHeaderFooterPolicy();
			XWPFFooter footer = headerFooterPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);
//			## [CAPSALERA] ##
//			XWPFParagraph paragraph = footer.createParagraph();
//			XmlCursor cursor = paragraph.getCTP().newCursor();
//			XWPFTable tableFooter = footer.insertNewTbl(cursor);
			XWPFTable tableFooter = footer.createTable(1, 2);
			tableFooter.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(6 * twipsPerInch));
//			## [NEW_ROW] ## crea la primera fila d'una taula
//			XWPFTableRow firstRow = createFirstTableRow(tableFooter);
//			## [TÍTOL COL] ## first column
			addInfoToFooter(tableFooter, 0);
//			## [LOGO COL] ## second column
			addInfoToFooter(tableFooter, 1);

			removeTableBorder(tableFooter);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private XWPFTableRow createFirstTableRow(XWPFTable table) {
		int twipsPerInch = 1725;
		XWPFTableRow row = table.getRow(0);
		if (row == null)
			row = table.createRow();
		table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(6 * twipsPerInch));
		return row;
	}

	private void addInfoToHeader(XWPFTable table, int numCell) {
		try {
			XWPFTableCell cell = table.getRow(0).getCell(numCell);
			//## a vegades la columna no té format(width i tipus) i dona error
			//checkAndFormatCell(cell, 3.53);

			if (numCell == 0) {
				XWPFParagraph paragraph = cell.getParagraphs().get(0);
//				## [LOGO]
				paragraph.setSpacingBefore(100);
				paragraph.setSpacingAfter(100);
				XWPFRun runLogo = paragraph.createRun();
				if (getCapsaleraLogo() != null) {
					FileInputStream pictureInputStream = new FileInputStream(getCapsaleraLogo());
					int indexOfLastBar = getCapsaleraLogo().lastIndexOf("/") != -1 ? getCapsaleraLogo().lastIndexOf("/")
							: getCapsaleraLogo().lastIndexOf("\\");
					String logoName = getCapsaleraLogo().substring(indexOfLastBar + 1, getCapsaleraLogo().length());
					runLogo.addPicture(pictureInputStream, XWPFDocument.PICTURE_TYPE_GIF, logoName, Units.toEMU(180),
							Units.toEMU(60));
					pictureInputStream.close();
				}
			} else if (numCell == 1) {
				cell.setVerticalAlignment(XWPFVertAlign.CENTER);
//				## [DIRECCIO - NIF]
				XWPFParagraph paragraphNif = cell.getParagraphs().get(0);
				XWPFRun direccioNifRun = paragraphNif.createRun();
				direccioNifRun.setText(getNifDireccio());
//				## [DIRECCIO - CODIDIR3]
				XWPFParagraph paragraphDir = cell.addParagraph();
				XWPFRun direccioCodiRun = paragraphDir.createRun();
				direccioCodiRun.setText(getCodiDireccio());
//				## [DIRECCIO - CARRER]
				XWPFParagraph paragraphCarrer = cell.addParagraph();
				XWPFRun direccioRun = paragraphCarrer.createRun();
				direccioRun.setText(getDireccio());
//				## [DIRECCIO - EMAIL]
				XWPFParagraph paragraphEmail = cell.addParagraph();
				XWPFRun direccioEmailRun = paragraphEmail.createRun();
				direccioEmailRun.setText(getEmailDireccio());
				direccioEmailRun.setUnderline(UnderlinePatterns.SINGLE);
				
				for (XWPFParagraph paragraph : cell.getParagraphs()) {
					paragraph.getRuns().get(0).setFontSize(8);
					paragraph.getRuns().get(0).setFontFamily("Frutiger");
					paragraph.getRuns().get(0).setBold(false);
					paragraph.getRuns().get(0).setColor("7f7f7f");
					paragraph.setStyle("Encabezado");
					paragraph.setAlignment(ParagraphAlignment.RIGHT);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void addInfoToFooter(XWPFTable table, int numCell) {
		try {
//			int twipsPerInch = 1750;
			XWPFTableCell cell = table.getRow(0).getCell(numCell);
			
			//## a vegades la columna no té format(width i tipus) i dona error
			checkAndFormatCell(cell, 3.53);
			
			XWPFParagraph paragraph = cell.getParagraphs().get(0);

			if (numCell == 0) {
				paragraph.setAlignment(ParagraphAlignment.LEFT);
//				## [DIRECCIO - FOOTER-TITLE]
				XWPFRun titolFooterRun = paragraph.createRun();
				if (getPeuTitol() != null) {
					titolFooterRun.addBreak();
					titolFooterRun.setText(getPeuTitol());
				}
				titolFooterRun.setFontSize(9);
				titolFooterRun.setFontFamily("Frutiger");
				titolFooterRun.setColor("7f7f7f");
			} else {
//				## [LOGO]
				paragraph.setAlignment(ParagraphAlignment.RIGHT);
				paragraph.setSpacingBefore(2);
				XWPFRun runLogo = paragraph.createRun();
				if (getPeuLogo() != null) {
					FileInputStream pictureInputStream = new FileInputStream(getPeuLogo());
					int indexOfLastBar = getCapsaleraLogo().lastIndexOf("/") != -1 ? getPeuLogo().lastIndexOf("/")
							: getCapsaleraLogo().lastIndexOf("\\");
					String logoName = getPeuLogo().substring(indexOfLastBar + 1, getPeuLogo().length());
					runLogo.addPicture(pictureInputStream, XWPFDocument.PICTURE_TYPE_JPEG, logoName, Units.toEMU(100),
							Units.toEMU(60));
//					pictureInputStream.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createTitle(XWPFDocument justificant) {
		XWPFParagraph paragraph = null;
		if (justificant.getParagraphs().get(0) != null && !justificant.getParagraphs().isEmpty())
			paragraph = justificant.createParagraph();
		else
			paragraph = justificant.createParagraph();

		paragraph.setAlignment(ParagraphAlignment.CENTER);
		// ## style
		XWPFRun run = paragraph.createRun();
		run.setText(messageHelper.getMessage("es.caib.notib.justificant.titol"));
		run.setFontSize(11);
		run.setFontFamily("Frutiger");
		run.setBold(true);
		run.addCarriageReturn();
		run.addBreak();
	}

	private void createIntroduction(XWPFDocument justificant, NotificacioDtoV2 notificacio) {
		XWPFParagraph paragraph = justificant.createParagraph();
		// ## introducció 1
		XWPFRun runIntro = paragraph.createRun();
		runIntro.setText(messageHelper.getMessage("es.caib.notib.justificant.introduccio", new Object[] {
				notificacio.getEnviamentTipus().name(), notificacio.getConcepte(), notificacio.getCreatedDate().toString() }));
		runIntro.setFontSize(10);
		runIntro.setFontFamily("Calibri");
		runIntro.addBreak();
		// ## introducció 2
		XWPFRun runIntroEnviaments = paragraph.createRun();
		runIntroEnviaments.setText(messageHelper.getMessage("es.caib.notib.justificant.enviaments.titol",
				new Object[] { notificacio.getEnviaments().size() }));
		runIntroEnviaments.setFontSize(10);
		runIntroEnviaments.setFontFamily("Calibri");
		runIntroEnviaments.addBreak();
	}

	private void createTableEnviaments(
			XWPFDocument justificant, 
			NotificacioDtoV2 notificacio, 
			NotificacioEnviamentDtoV2 enviament,
			int numEnviament) {
		XWPFTable taulaEnviaments = crearTaulaEnviaments(justificant);

		configurarTitolTaulaEnviaments(
				taulaEnviaments, 
				notificacio, 
				numEnviament);

		crearContingutTaulaEnviaments(
				justificant, 
				taulaEnviaments, 
				notificacio, 
				enviament, 
				numEnviament);
	}

	private void configurarTitolTaulaEnviaments(XWPFTable taulaEnviaments, NotificacioDtoV2 notificacio,
			int numEnviament) {
		int twipsPerInch = 1440;
//		## títol taula enviaments
		XWPFTableRow tableTitleRow = taulaEnviaments.getRow(0);
//		tableTitleRow.setHeight((int) (twipsPerInch * 3 / 10));
		XWPFTableCell tableTitleCell = tableTitleRow.getCell(0);
		tableTitleCell.setColor("a6a6a6");
//		tableTitleCell.setVerticalAlignment(XWPFVertAlign.CENTER);

//		## paràgraf cella
		XWPFParagraph tableTitleCellParagraph = tableTitleCell.getParagraphs().get(0);
		tableTitleCellParagraph.setAlignment(ParagraphAlignment.CENTER);
//		tableTitleCellParagraph.setVerticalAlignment(TextAlignment.CENTER);

//		## contingut cella
		XWPFRun tableTitleRun = tableTitleCellParagraph.createRun();
		tableTitleRun.setText(messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.titol",
				new Object[] { numEnviament, notificacio.getEnviaments().size() }));
		tableTitleRun.setFontSize(10);
		tableTitleRun.setFontFamily("Calibri");
		tableTitleRun.setBold(true);
		tableTitleRun.setColor("ffffff");
	}

	private void crearContingutTaulaEnviaments(
			XWPFDocument justificant,
			XWPFTable taulaEnviaments,
			NotificacioDtoV2 notificacio,
			NotificacioEnviamentDtoV2 enviament,
			int numEnviament) {
		try {
			BigInteger styleNormalMarginId = createNewListStyle(justificant, false, false);
			BigInteger styleDoubleMarginId = createNewListStyle(justificant, false, true);
			BigInteger styleCustomTabId = createNewListStyle(justificant, true, false);
			
	//		## contignut taula enviaments
			XWPFTableRow tableContentRow = taulaEnviaments.getRow(1);
			XWPFTableCell tableContentCell = tableContentRow.getCell(0);
	
	//		## dades registre
			XWPFParagraph tableDadesRegistreParagraph = tableContentCell.getParagraphs().get(0);
			XWPFRun tableDadesRegistreRun = tableDadesRegistreParagraph.createRun();
			tableDadesRegistreRun.setText(messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.dades.registre"));
			tableDadesRegistreRun.setFontSize(10);
			tableDadesRegistreRun.setFontFamily("Calibri");
			tableDadesRegistreRun.setBold(true);
			
	//		## dades registre - número
			XWPFParagraph tableDadesRegistreNumParagraph = tableContentCell.addParagraph();
			tableDadesRegistreNumParagraph.setNumID(styleNormalMarginId);

			XWPFRun tableDadesRegistreNumRun = tableDadesRegistreNumParagraph.createRun();
			tableDadesRegistreNumRun.setText(
					messageHelper.getMessage(
							"es.caib.notib.justificant.enviaments.taula.dades.registre.numero",
							new Object[] { enviament.getRegistreNumero() }));
			tableDadesRegistreNumRun.setFontSize(10);
			tableDadesRegistreNumRun.setFontFamily("Calibri");
			
	//		## dades registre - data
			XWPFParagraph tableDadesRegistreDataParagraph = tableContentCell.addParagraph();
			tableDadesRegistreDataParagraph.setNumID(styleNormalMarginId);
			
			XWPFRun tableDadesRegistreDataRun = tableDadesRegistreDataParagraph.createRun();
			tableDadesRegistreDataRun.setText(
					messageHelper.getMessage(
							"es.caib.notib.justificant.enviaments.taula.dades.registre.data",
							new Object[] { enviament.getRegistreData().toString() }));
			tableDadesRegistreDataRun.setFontSize(10);
			tableDadesRegistreDataRun.setFontFamily("Calibri");
			tableDadesRegistreDataRun.addBreak();
			
	//		## dades notific@
			XWPFParagraph tableDadesNotificaParagraph = tableContentCell.addParagraph();
			XWPFRun tableDadesNotificaRun = tableDadesNotificaParagraph.createRun();
			tableDadesNotificaRun.setText(messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.dades.notifica"));
			tableDadesNotificaRun.setFontSize(10);
			tableDadesNotificaRun.setFontFamily("Calibri");
			tableDadesNotificaRun.setBold(true);
			tableDadesNotificaRun.addBreak();
			
//			## dades notific@ - identificador
			XWPFParagraph tableDadesNotificaIdentParagraph = tableContentCell.addParagraph();
			tableDadesNotificaIdentParagraph.setNumID(styleNormalMarginId);
			
			XWPFRun tableDadesNotificaIdentRun = tableDadesNotificaIdentParagraph.createRun();
			tableDadesNotificaIdentRun.setText(
					messageHelper.getMessage(
							"es.caib.notib.justificant.enviaments.taula.dades.notifica.identificador",
							new Object[] { enviament.getNotificaIdentificador() }));
			tableDadesNotificaIdentRun.setFontSize(10);
			tableDadesNotificaIdentRun.setFontFamily("Calibri");
			
//			## dades notific@ - estat
			XWPFParagraph tableDadesNotificaEstatParagraph = tableContentCell.addParagraph();
			tableDadesNotificaEstatParagraph.setNumID(styleNormalMarginId);
			
			XWPFRun tableDadesNotificaEstatRun = tableDadesNotificaEstatParagraph.createRun();
			tableDadesNotificaEstatRun.setText(
					messageHelper.getMessage(
							"es.caib.notib.justificant.enviaments.taula.dades.notifica.estat",
							new Object[] { notificacio.getEstat().name() }));
			tableDadesNotificaEstatRun.setFontSize(10);
			tableDadesNotificaEstatRun.setFontFamily("Calibri");
			
//			## dades notific@ - data
			XWPFParagraph tableDadesNotificaDataParagraph = tableContentCell.addParagraph();
			tableDadesNotificaDataParagraph.setNumID(styleNormalMarginId);
			
			XWPFRun tableDadesNotificaDataRun = tableDadesNotificaDataParagraph.createRun();
			tableDadesNotificaDataRun.setText(
					messageHelper.getMessage(
							"es.caib.notib.justificant.enviaments.taula.dades.notifica.data",
							new Object[] { enviament.getNotificaEstatData().toString() }));
			tableDadesNotificaDataRun.setFontSize(10);
			tableDadesNotificaDataRun.setFontFamily("Calibri");
			tableDadesNotificaDataRun.addBreak();
			
	//		## interessats
			XWPFParagraph tableDadesInteressatsParagraph = tableContentCell.addParagraph();
			XWPFRun tableDadesInteressatsRun = tableDadesInteressatsParagraph.createRun();
			tableDadesInteressatsRun.setText(messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessats"));
			tableDadesInteressatsRun.setFontSize(10);
			tableDadesInteressatsRun.setFontFamily("Calibri");
			tableDadesInteressatsRun.setBold(true);
			tableDadesInteressatsRun.addBreak();
			
	//		## interessats - titular
			XWPFParagraph tableDadesTitularParagraph = tableContentCell.addParagraph();
			tableDadesTitularParagraph.setNumID(styleCustomTabId);
			
			XWPFRun tableDadesTitularRun = tableDadesTitularParagraph.createRun();
			tableDadesTitularRun.addTab();
			tableDadesTitularRun.setText(messageHelper.getMessage("es.caib.notib.justificant.enviaments.taula.interessat.titular"));
			tableDadesTitularRun.setFontSize(10);
			tableDadesTitularRun.setFontFamily("Calibri");
			tableDadesTitularRun.addBreak();
			
//			## interessats - titular - nom
			XWPFParagraph tableDadesTitularNomParagraph = tableContentCell.addParagraph();
			tableDadesTitularNomParagraph.setNumID(styleDoubleMarginId);
			
			XWPFRun tableDadesTitularNomRun = tableDadesTitularNomParagraph.createRun();
			tableDadesTitularNomRun.setText(
					messageHelper.getMessage(
							"es.caib.notib.justificant.enviaments.taula.interessat.titular.nom",
							new Object[] { enviament.getTitular().getNom() != null ? enviament.getTitular().getNom() : enviament.getTitular().getRaoSocial() }));
			tableDadesTitularNomRun.setFontSize(10);
			tableDadesTitularNomRun.setFontFamily("Calibri");
			
//			## interessats - titular - llinatges
			XWPFParagraph tableDadesTitularLlintgParagraph = tableContentCell.addParagraph();
			tableDadesTitularLlintgParagraph.setNumID(styleDoubleMarginId);
			
			XWPFRun tableDadesTitularLlintgRun = tableDadesTitularLlintgParagraph.createRun();
			tableDadesTitularLlintgRun.setText(
					messageHelper.getMessage(
							"es.caib.notib.justificant.enviaments.taula.interessat.titular.llinatges",
							new Object[] { enviament.getTitular().getLlinatges() }));
			tableDadesTitularLlintgRun.setFontSize(10);
			tableDadesTitularLlintgRun.setFontFamily("Calibri");
			
//			## interessats - titular - nif
			XWPFParagraph tableDadesTitularNifParagraph = tableContentCell.addParagraph();
			tableDadesTitularNifParagraph.setNumID(styleDoubleMarginId);
			
			XWPFRun tableDadesTitularNifRun = tableDadesTitularNifParagraph.createRun();
			tableDadesTitularNifRun.setText(
					messageHelper.getMessage(
							"es.caib.notib.justificant.enviaments.taula.interessat.titular.nif",
							new Object[] { enviament.getTitular().getNif() }));
			tableDadesTitularNifRun.setFontSize(10);
			tableDadesTitularNifRun.setFontFamily("Calibri");
			
//			## interessats - titular - dir3
			XWPFParagraph tableDadesTitularDirParagraph = tableContentCell.addParagraph();
			tableDadesTitularDirParagraph.setNumID(styleDoubleMarginId);
			
			XWPFRun tableDadesTitularDirRun = tableDadesTitularDirParagraph.createRun();
			tableDadesTitularDirRun.setText(
					messageHelper.getMessage(
							"es.caib.notib.justificant.enviaments.taula.interessat.titular.dir3",
							new Object[] { enviament.getTitular().getDir3Codi() }));
			tableDadesTitularDirRun.setFontSize(10);
			tableDadesTitularDirRun.setFontFamily("Calibri");
			tableDadesTitularDirRun.addBreak();
			
	//		## interessats - destinataris
			int destinatariNum = 1;
			for (PersonaDto destinatari : enviament.getDestinataris()) {
				//		## interessats - destinatari
				XWPFParagraph tableDadesDestinatariParagraph = tableContentCell.addParagraph();
				tableDadesDestinatariParagraph.setNumID(styleCustomTabId);
				
				XWPFRun tableDadesDestinatariRun = tableDadesDestinatariParagraph.createRun();
				tableDadesDestinatariRun.addTab();
				tableDadesDestinatariRun.setText(
						messageHelper.getMessage(
								"es.caib.notib.justificant.enviaments.taula.interessat.destinatari",
								new Object[] {destinatariNum}));
				tableDadesDestinatariRun.setFontSize(10);
				tableDadesDestinatariRun.setFontFamily("Calibri");
				
//				## interessats - destinatari - nom
				XWPFParagraph tableDadesDestinatariNomParagraph = tableContentCell.addParagraph();
				tableDadesDestinatariNomParagraph.setNumID(styleDoubleMarginId);
				
				XWPFRun tableDadesDestinatariNomRun = tableDadesDestinatariNomParagraph.createRun();
				tableDadesDestinatariNomRun.setText(
						messageHelper.getMessage(
								"es.caib.notib.justificant.enviaments.taula.interessat.destinatari.nom",
								new Object[] { destinatari.getNom() != null ? destinatari.getNom() : destinatari.getRaoSocial() }));
				tableDadesDestinatariNomRun.setFontSize(10);
				tableDadesDestinatariNomRun.setFontFamily("Calibri");
				
//				## interessats - destinatari - llinatges
				XWPFParagraph tableDadesDestinatariLlintgParagraph = tableContentCell.addParagraph();
				tableDadesDestinatariLlintgParagraph.setNumID(styleDoubleMarginId);
				
				XWPFRun tableDadesDestinatariLlintgRun = tableDadesDestinatariLlintgParagraph.createRun();
				tableDadesDestinatariLlintgRun.setText(
						messageHelper.getMessage(
								"es.caib.notib.justificant.enviaments.taula.interessat.destinatari.llinatges",
								new Object[] { destinatari.getLlinatges() }));
				tableDadesDestinatariLlintgRun.setFontSize(10);
				tableDadesDestinatariLlintgRun.setFontFamily("Calibri");
				
//				## interessats - destinatari - nif
				XWPFParagraph tableDadesDestinatariNifParagraph = tableContentCell.addParagraph();
				tableDadesDestinatariNifParagraph.setNumID(styleDoubleMarginId);
				
				XWPFRun tableDadesDestinatariNifRun = tableDadesDestinatariNifParagraph.createRun();
				tableDadesDestinatariNifRun.setText(
						messageHelper.getMessage(
								"es.caib.notib.justificant.enviaments.taula.interessat.destinatari.nif",
								new Object[] { destinatari.getNif() }));
				tableDadesDestinatariNifRun.setFontSize(10);
				tableDadesDestinatariNifRun.setFontFamily("Calibri");
				
//				## interessats - destinatari - dir3
				XWPFParagraph tableDadesDestinatariDirParagraph = tableContentCell.addParagraph();
				tableDadesDestinatariDirParagraph.setNumID(styleDoubleMarginId);
				
				XWPFRun tableDadesDestinatariDirRun = tableDadesDestinatariDirParagraph.createRun();
				tableDadesDestinatariDirRun.setText(
						messageHelper.getMessage(
								"es.caib.notib.justificant.enviaments.taula.interessat.destinatari.dir3",
								new Object[] { destinatari.getDir3Codi() }));
				tableDadesDestinatariDirRun.setFontSize(10);
				tableDadesDestinatariDirRun.setFontFamily("Calibri");
				tableDadesDestinatariRun.addBreak();
				destinatariNum++;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private BigInteger createNewListStyle(
			XWPFDocument doc,
			boolean isTab,
			boolean doubleTab) {
        try {
        	String listStyle = null;
        	if (isTab) {
        		listStyle = getListCustomTab();
        	} else if(!isTab && !doubleTab) {
        		listStyle = getNormalListStyle('-');
        	} else if(!isTab && doubleTab) {
        		listStyle = getDoubleListStyle('-');
        	}
        	
        	CTNumbering cTNumbering = CTNumbering.Factory.parse(listStyle);
        	CTAbstractNum cTAbstractNum = cTNumbering.getAbstractNumArray(0);
        	XWPFAbstractNum abstractNum1 = new XWPFAbstractNum(cTAbstractNum);
        	  
        	XWPFNumbering numbering = doc.createNumbering();
        	
        	BigInteger abstractNumID1 = numbering.addAbstractNum(abstractNum1);
        	BigInteger numID = numbering.addNum(abstractNumID1);
            return numID;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
	private static String getNormalListStyle(char simbol) {
		String style = "<w:abstractNum xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" w:abstractNumId=\"0\">\r\n" + 
				"		<w:nsid w:val=\"732069B6\"/>\r\n" + 
				"		<w:multiLevelType w:val=\"singleLevel\"/>\r\n" + 
				"		<w:tmpl w:val=\"033EE1DA\"/>\r\n" + 
				"		<w:lvl w:ilvl=\"0\" w:tplc=\"F7342CAA\">\r\n" + 
				"			<w:numFmt w:val=\"bullet\"/>\r\n" + 
				"			<w:lvlText w:val=\"" + simbol + "\"/>\r\n" + 
				"			<w:lvlJc w:val=\"left\"/>\r\n" + 
				"			<w:pPr>\r\n" + 
				"				<w:ind w:left=\"720\" w:hanging=\"360\"/>\r\n" + 
				"			</w:pPr>\r\n" + 
				"			<w:rPr>\r\n" + 
				"				<w:rFonts w:ascii=\"Calibri\" w:eastAsiaTheme=\"minorHAnsi\" w:hAnsi=\"Calibri\" w:cs=\"Calibri\" w:hint=\"default\"/>\r\n" + 
				"			</w:rPr>\r\n" + 
				"		</w:lvl>\r\n" + 
				"	</w:abstractNum>";
		return style;
	}
	
	private static String getDoubleListStyle(char simbol) {
		String style = "<w:abstractNum xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" w:abstractNumId=\"1\">\r\n" + 
				"		<w:nsid w:val=\"732069B6\"/>\r\n" + 
				"		<w:multiLevelType w:val=\"singleLevel\"/>\r\n" + 
				"		<w:tmpl w:val=\"033EE1DA\"/>\r\n" + 
				"		<w:lvl w:ilvl=\"0\" w:tplc=\"F7342CAA\">\r\n" + 
				"			<w:numFmt w:val=\"bullet\"/>\r\n" + 
				"			<w:lvlText w:val=\"" + simbol + "\"/>\r\n" + 
				"			<w:lvlJc w:val=\"left\"/>\r\n" + 
				"			<w:pPr>\r\n" + 
				"				<w:ind w:left=\"1100\" w:hanging=\"360\"/>\r\n" + 
				"			</w:pPr>\r\n" + 
				"			<w:rPr>\r\n" + 
				"				<w:rFonts w:ascii=\"Calibri\" w:eastAsiaTheme=\"minorHAnsi\" w:hAnsi=\"Calibri\" w:cs=\"Calibri\" w:hint=\"default\"/>\r\n" + 
				"			</w:rPr>\r\n" + 
				"		</w:lvl>\r\n" + 
				"	</w:abstractNum>";
		return style;
	}
	
	private static String getListCustomTab() {
		String style = "<w:abstractNum xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" w:abstractNumId=\"2\">\r\n" + 
				"		<w:nsid w:val=\"732069B6\"/>\r\n" + 
				"		<w:multiLevelType w:val=\"singleLevel\"/>\r\n" + 
				"		<w:tmpl w:val=\"033EE1DA\"/>\r\n" + 
				"		<w:lvl w:ilvl=\"0\" w:tplc=\"F7342CAA\">\r\n" + 
				"			<w:numFmt w:val=\"bullet\"/>\r\n" + 
				"			<w:lvlJc w:val=\"left\"/>\r\n" + 
				"			<w:lvlText w:val=\"\"/>\r\n" + 
				"			<w:pPr>\r\n" + 
				"				<w:ind w:left=\"310\" w:hanging=\"360\"/>\r\n" + 
				"			</w:pPr>\r\n" + 
				"			<w:rPr>\r\n" + 
				"				<w:rFonts w:ascii=\"Calibri\" w:eastAsiaTheme=\"minorHAnsi\" w:hAnsi=\"Calibri\" w:cs=\"Calibri\" w:hint=\"default\"/>\r\n" + 
				"			</w:rPr>\r\n" + 
				"		</w:lvl>\r\n" + 
				"	</w:abstractNum>";
		return style;
	}

	private XWPFDocument getDocumentFormatted() throws XmlException {
		XWPFDocument justificant = null;
		try {
			justificant = recuperarPlantilla();
			double twipsPerInch = 1440;
			CTSectPr sectPr = justificant.getDocument().getBody().getSectPr();
			CTPageMar pageMargin = sectPr.getPgMar();

			pageMargin.setHeader(BigInteger.valueOf((long) (0.472441 * twipsPerInch))); // 1.00cm
			pageMargin.setLeft(BigInteger.valueOf((long) (0.590551 * twipsPerInch))); // 1.50cm
			pageMargin.setRight(BigInteger.valueOf((long) (0.393701 * twipsPerInch))); // 1.00cm
			pageMargin.setFooter(BigInteger.valueOf((long) (0.1732283 * twipsPerInch))); // 0.44cm

			CTStyles ctStyle = CTStyles.Factory.parse(recuperarEstils());
		    XWPFStyles styles = justificant.createStyles();
		    styles.setStyles(ctStyle);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return justificant;
	}

	private static XWPFTable crearTaulaEnviaments(XWPFDocument justificant) {
		// ## configuració inicial taula
		int twipsPerInch = 1440;
		double inches = 7.08661; // 18.00cm
		XWPFTable tableEnviaments = justificant.createTable(2, 1);
		tableEnviaments.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf((long) (inches * twipsPerInch)));
		tableEnviaments.setCellMargins(110, 110, 110, 110);
		setTableBorderColor(tableEnviaments, "a6a6a6");
		return tableEnviaments;
	}

	private static void setTableBorderColor(XWPFTable table, String color) {

		table.getCTTbl().getTblPr().getTblBorders().getBottom().setColor(color);
		table.getCTTbl().getTblPr().getTblBorders().getTop().setColor(color);
		table.getCTTbl().getTblPr().getTblBorders().getLeft().setColor(color);
		table.getCTTbl().getTblPr().getTblBorders().getRight().setColor(color);
		table.getCTTbl().getTblPr().getTblBorders().getInsideH().setColor(color);
		table.getCTTbl().getTblPr().getTblBorders().getInsideV().setColor(color);

		table.getCTTbl().getTblPr().getTblBorders().getRight().setSz(BigInteger.valueOf(6));
		table.getCTTbl().getTblPr().getTblBorders().getTop().setSz(BigInteger.valueOf(6));
		table.getCTTbl().getTblPr().getTblBorders().getLeft().setSz(BigInteger.valueOf(6));
		table.getCTTbl().getTblPr().getTblBorders().getBottom().setSz(BigInteger.valueOf(6));
		table.getCTTbl().getTblPr().getTblBorders().getInsideH().setSz(BigInteger.valueOf(6));
		table.getCTTbl().getTblPr().getTblBorders().getInsideV().setSz(BigInteger.valueOf(6));
	}
	
	private static void removeTableBorder(XWPFTable table) {
		table.getCTTbl().getTblPr().getTblBorders().getRight().setSz(BigInteger.valueOf(0));
		table.getCTTbl().getTblPr().getTblBorders().getTop().setSz(BigInteger.valueOf(0));
		table.getCTTbl().getTblPr().getTblBorders().getLeft().setSz(BigInteger.valueOf(0));
		table.getCTTbl().getTblPr().getTblBorders().getBottom().setSz(BigInteger.valueOf(0));
		table.getCTTbl().getTblPr().getTblBorders().getInsideH().setSz(BigInteger.valueOf(0));
		table.getCTTbl().getTblPr().getTblBorders().getInsideV().setSz(BigInteger.valueOf(0));
	}
	
	private void checkAndFormatCell(XWPFTableCell cell, double d) {
		int twipsPerInch = 1440;
		if (cell.getCTTc().getTcPr() == null)
			cell.getCTTc().addNewTcPr();
		if (cell.getCTTc().getTcPr().getTcW() == null)
			cell.getCTTc().getTcPr().addNewTcW();
		cell.getCTTc().getTcPr().getTcW().setType(STTblWidth.DXA);
		cell.getCTTc().getTcPr().getTcW().setW(BigInteger.valueOf((long) (d * twipsPerInch)));
	}
	
	private InputStream recuperarEstils() throws IOException {
		return getClass().getResourceAsStream("/es/caib/notib/core/plantilles/styles.xml");
	}
	
	private XWPFDocument recuperarPlantilla() throws IOException {
		return new XWPFDocument(
				getClass().getResourceAsStream("/es/caib/notib/core/plantilles/plantilla_justificante.docx"));
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
}

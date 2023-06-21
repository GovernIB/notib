/**
 * 
 */
package es.caib.notib.plugin.conversio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BarcodePDF417;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import es.caib.notib.plugin.SistemaExternException;
import fr.opensagres.xdocreport.converter.ConverterRegistry;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.IConverter;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.document.DocumentKind;

/**
 * Implementació del plugin de conversió de documents
 * emprant XDocReport.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConversioPluginXdocreport implements ConversioPlugin {

	private static final int BARCODE_POSITION_TOP = 0;
	private static final int BARCODE_POSITION_BOTTOM = 1;
	private static final int BARCODE_POSITION_LEFT = 2;
	private static final int BARCODE_POSITION_RIGHT = 3;

	@Override
	public ConversioArxiu convertirPdf(ConversioArxiu arxiu) throws SistemaExternException {

		try {
			return convertirIEstampar(arxiu, null);
		} catch (Exception ex) {
			throw new SistemaExternException("No s'ha pogut convertir l'arxiu a format PDF (arxiuNom=" + arxiu.getArxiuNom()
					+ ", arxiuTamany=" + arxiu.getArxiuContingut().length + ")", ex);
		}
	}

	@Override
	public ConversioArxiu convertirPdfIEstamparUrl(ConversioArxiu arxiu, String url) throws SistemaExternException {

		try {
			return convertirIEstampar(arxiu, url);
		} catch (Exception ex) {
			throw new SistemaExternException("No s'ha pogut convertir l'arxiu a format PDF (arxiuNom=" + arxiu.getArxiuNom() + ", " +
					"arxiuTamany=" + arxiu.getArxiuContingut().length + ")", ex);
		}
	}

	@Override
	public String getNomArxiuConvertitPdf(String nomOriginal) {
		return nomOriginal == null || nomOriginal.lastIndexOf(".") == -1 ? nomOriginal : nomOriginal.substring(0, nomOriginal.lastIndexOf(".")) + ".pdf";
	}

	private boolean isExtensioPdf(ConversioArxiu arxiu) {
		return "pdf".equalsIgnoreCase(arxiu.getArxiuExtensio());
	}

	private DocumentKind getDocumentKind(ConversioArxiu arxiu) throws SistemaExternException {

		var extensio = arxiu.getArxiuExtensio();
		if ("odt".equalsIgnoreCase(extensio)) {
			return DocumentKind.ODT;
		}
		if ("docx".equalsIgnoreCase(extensio)) {
			return DocumentKind.DOCX;
		}
		throw new SistemaExternException("Tipus de document no suportat (arxiuNom=" + arxiu.getArxiuNom() + ")");
	}

	private ConversioArxiu convertirIEstampar(ConversioArxiu arxiu, String url) throws Exception {

		var convertit = new ConversioArxiu();
		ByteArrayOutputStream baosConversio = null;
		if (!isExtensioPdf(arxiu)) {
			Options options = Options.getFrom(getDocumentKind(arxiu)).to(ConverterTypeTo.PDF);
			ByteArrayInputStream bais = new ByteArrayInputStream(arxiu.getArxiuContingut());
			baosConversio = new ByteArrayOutputStream();
			IConverter converter = ConverterRegistry.getRegistry().getConverter(options);
			converter.convert(bais, baosConversio, options);
		}
		if (url == null) {
			convertit.setArxiuContingut(baosConversio != null ? baosConversio.toByteArray() : arxiu.getArxiuContingut());
		} else {
			var pdfReader = baosConversio != null ? new PdfReader(baosConversio.toByteArray()) : new PdfReader(arxiu.getArxiuContingut());
			var baosEstampacio = new ByteArrayOutputStream();
			var pdfStamper = new PdfStamper(pdfReader, baosEstampacio);
			PdfContentByte over;
			for (var i = 0; i < pdfReader.getNumberOfPages(); i++) {
				over = pdfStamper.getOverContent(i + 1);
				estamparBarcodePdf417(over, url, BARCODE_POSITION_LEFT, 10);
			}
			pdfStamper.close();
			convertit.setArxiuContingut(baosEstampacio.toByteArray());
		}
		convertit.setArxiuNom(getNomArxiuConvertitPdf(arxiu.getArxiuNom()));
		return convertit;
	}

	private void estamparBarcodePdf417(PdfContentByte contentByte, String url, int posicio, float margin) throws Exception {

		var paddingUrl = 5;
		// Calcula les dimensions de la pàgina i la taula
		var page = contentByte.getPdfDocument().getPageSize();
		var pageWidth = page.getWidth();
		var pageHeight = page.getHeight();
		if (posicio == BARCODE_POSITION_TOP || posicio == BARCODE_POSITION_BOTTOM) {
			var ampladaTaulaMax = pageWidth - (2 * margin);
			// Crea la cel·la del codi de barres
			var pdf417 = new BarcodePDF417();
			pdf417.setText(url);
			var img = pdf417.getImage();
			var pdf417Cell = new PdfPCell(img);
			pdf417Cell.setBorder(0);
			pdf417Cell.setFixedHeight(img.getHeight());
			var imgCellWidth = img.getWidth();
			// Crea la cel·la amb la url
			var urlFont = new Font(Font.HELVETICA, 6);
			var urlChunk = new Chunk(url, urlFont);
			var urlPhrase = new Phrase(urlChunk);
			var urlCell = new PdfPCell(urlPhrase);
			urlCell.setPadding(0);
			urlCell.setBorder(0);
			urlCell.setFixedHeight(img.getHeight());
			urlCell.setUseAscender(true);
			urlCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			urlCell.setPaddingLeft(paddingUrl);
			var urlWidth = urlChunk.getWidthPoint() + 5;
			var urlCellWidth = (imgCellWidth + urlWidth > ampladaTaulaMax) ? ampladaTaulaMax - imgCellWidth : urlWidth;
			// Estampa el codi de barres en la posició elegida
			var table = new PdfPTable(2);
			table.addCell(pdf417Cell);
			table.addCell(urlCell);
			var ampladaTaula = imgCellWidth + urlCellWidth;
			table.setWidths(new float[]{img.getWidth(), ampladaTaula - img.getWidth()});
			table.setTotalWidth(ampladaTaula);
			if (posicio == BARCODE_POSITION_TOP) {
				table.writeSelectedRows(0, -1, (pageWidth / 2) - (ampladaTaula / 2), pageHeight - margin, contentByte);
			} else {
				table.writeSelectedRows(0, -1, (pageWidth / 2) - (ampladaTaula / 2), margin + img.getHeight(), contentByte);
			}
		} else if (posicio == BARCODE_POSITION_LEFT || posicio == BARCODE_POSITION_RIGHT) {
			var ampladaTaulaMax = pageHeight - (2 * margin);
			// Crea la cel·la del codi de barres
			var pdf417 = new BarcodePDF417();
			pdf417.setText(url);
			var img = pdf417.getImage();
			var pdf417Cell = new PdfPCell(img);
			pdf417Cell.setBorder(1);
			pdf417Cell.setFixedHeight(img.getWidth());
			pdf417Cell.setRotation(90);
			var imgCellWidth = img.getWidth();
			// Crea la cel·la amb la url
			var urlFont = new Font(Font.HELVETICA, 6);
			var urlChunk = new Chunk(url, urlFont);
			var urlPhrase = new Phrase(urlChunk);
			var urlCell = new PdfPCell(urlPhrase);
			urlCell.setPadding(0);
			urlCell.setBorder(0);
			urlCell.setUseAscender(true);
			urlCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			urlCell.setPaddingBottom(paddingUrl);
			urlCell.setRotation(90);
			var urlWidth = urlChunk.getWidthPoint() + 5;
			var urlCellWidth = (imgCellWidth + urlWidth > ampladaTaulaMax) ? ampladaTaulaMax - imgCellWidth : urlWidth;
			urlCell.setFixedHeight(urlCellWidth);
			// Estampa el codi de barres en la posició elegida
			var table = new PdfPTable(1);
			table.addCell(urlCell);
			table.addCell(pdf417Cell);
			table.setWidths(new float[]{img.getHeight()});
			table.setTotalWidth(img.getHeight());
			var ampladaTaula = imgCellWidth + urlCellWidth;
			if (posicio == BARCODE_POSITION_LEFT) {
				table.writeSelectedRows(0, -1, margin, pageHeight - (pageHeight / 2) + (ampladaTaula / 2), contentByte);
			} else {
				table.writeSelectedRows(0, -1, pageWidth - img.getHeight() - margin , pageHeight - (pageHeight / 2) + (ampladaTaula / 2), contentByte);
			}
		}
	}

}

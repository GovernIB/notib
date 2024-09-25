package es.caib.notib.logic.helper;

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
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.dto.ProgresDescarregaDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDtoV2;
import es.caib.notib.logic.intf.exception.JustificantException;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Helper per generar el justificant de la notificació electrònica
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Slf4j
@Component
public class JustificantRecepcioSIRHelper extends JustificantHelper<NotificacioEnviamentEntity> {

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PluginHelper pluginHelper;

	private static final float LIST_SYMBOL_INDENT = 20;
	private final SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	public ListItem buildListRow(String text, String description) {

		var paragraf = new Paragraph();
		setParametersBold(paragraf, "[" + text + "] " + description);
		return new ListItem(paragraf);
	}
	@SneakyThrows
	public byte[] generarJustificant(NotificacioEnviamentEntity enviament, ProgresDescarregaDto progres) throws JustificantException {

		log.debug("Generant el justificant de recepció SIR de l'enviament [enviamentId=" + enviament.getId() + "]");
		var resposta = pluginHelper.obtenerAsientoRegistral(enviament.getNotificacio().getEntitat().getDir3Codi(), enviament.getRegistreNumeroFormatat(), 2L, false);
		progres.setProgres(30);
		var paragrafContingut = new Paragraph();
		if (NotificacioRegistreEstatEnumDto.OFICI_ACCEPTAT.equals(resposta.getEstat())) {
			setParametersBold(paragrafContingut, messageHelper.getMessage("es.caib.notib.justificant.sir.llista.acceptat"));
			var list = new List(false, LIST_SYMBOL_INDENT);
			list.add(buildListRow(messageHelper.getMessage("es.caib.notib.justificant.sir.llista.acceptat.item1"), resposta.getNumeroRegistroDestino()));
			String dataRegistre = resposta.getSirRegistreDestiData() == null ? "" : dt.format(resposta.getSirRegistreDestiData());
			list.add(buildListRow(messageHelper.getMessage("es.caib.notib.justificant.sir.llista.acceptat.item2"), dataRegistre));
			String nomOficina = resposta.getDecodificacionEntidadRegistralProcesado() + " (" + resposta.getCodigoEntidadRegistralProcesado() + ")";
			list.add(buildListRow(messageHelper.getMessage("es.caib.notib.justificant.sir.llista.acceptat.item3"), nomOficina));
			paragrafContingut.add(list);
			paragrafContingut.add(Chunk.NEWLINE);

		} else if (NotificacioRegistreEstatEnumDto.REBUTJAT.equals(resposta.getEstat())) {
			setParametersBold(paragrafContingut, messageHelper.getMessage("es.caib.notib.justificant.sir.llista.rebutjat"));
			paragrafContingut.add(Chunk.NEWLINE);
			var list = new List(false, LIST_SYMBOL_INDENT);
			list.add(buildListRow(messageHelper.getMessage("es.caib.notib.justificant.sir.llista.rebutjat.item1"), resposta.getMotivo()));
			var nomOficina = resposta.getDecodificacionEntidadRegistralProcesado() + " (" + resposta.getCodigoEntidadRegistralProcesado() + ")";
			list.add(buildListRow(messageHelper.getMessage("es.caib.notib.justificant.sir.llista.rebutjat.item2"), nomOficina));
			paragrafContingut.add(list);
			paragrafContingut.add(Chunk.NEWLINE);

		} else {
			throw new JustificantException("No es pot generar un justificant de recepció SIR d'un enviament que no està en un estat final");

		}
		var out = new ByteArrayOutputStream();
		try {
			var justificant = inicialitzaDocument(out, progres);
			progres.setProgres(50);
			progres.addInfo(ProgresDescarregaDto.TipusInfo.INFO, messageHelper.getMessage("es.caib.notib.justificant.proces.generant.titol"));
			crearTitolAndIntroduccio(justificant, conversioTipusHelper.convertir(enviament.getNotificacio(), NotificacioDtoV2.class), progres);
			progres.setProgres(80);
			justificant.add(paragrafContingut);
			justificant.close();
		} catch (DocumentException ex) {
			var errorMessage = messageHelper.getMessage("es.caib.notib.justificant.proces.generant.error", new Object[] {ex.getMessage()});
			progres.setProgres(100);
			progres.addInfo(ProgresDescarregaDto.TipusInfo.ERROR, errorMessage);
			log.error(errorMessage, ex);
		}
		try {
			out.close();
		} catch (IOException ex) {
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
			String titolMessage = messageHelper.getMessage("es.caib.notib.justificant.sir.titol");
			var justificantTitol = new Paragraph(titolMessage, frutigerTitolBold);
			justificantTitol.setAlignment(Element.ALIGN_CENTER);
			justificantTitol.add(Chunk.NEWLINE);
			justificantTitol.add(Chunk.NEWLINE);

//			## [INTRODUCCIÓ JUSTIFICANT]
			var introduccio = messageHelper.getMessage("es.caib.notib.justificant.sir.introduccio",
					new Object[] {
							messageHelper.getMessage("es.caib.notib.logic.intf.dto.NotificaEnviamentTipusEnumDto." + notificacio.getEnviamentTipus().name()),
							notificacio.getConcepte(),
							getDateTimeFormatted(notificacio.getNotificaEnviamentNotificaData() != null ? notificacio.getNotificaEnviamentNotificaData() : notificacio.getNotificaEnviamentData())});
			var justificantIntroduccio = new Paragraph();
			setParametersBold(justificantIntroduccio, introduccio);
			justificantIntroduccio.add(Chunk.NEWLINE);
			titolIntroduccioCell.addElement(justificantTitol);
			titolIntroduccioCell.addElement(justificantIntroduccio);
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

}

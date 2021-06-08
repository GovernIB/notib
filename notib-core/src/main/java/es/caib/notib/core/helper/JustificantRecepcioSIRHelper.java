package es.caib.notib.core.helper;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import es.caib.notib.core.api.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.core.api.dto.ProgresDescarregaDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioDtoV2;
import es.caib.notib.core.api.exception.JustificantException;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

/**
 * Helper per generar el justificant de la notificació electrònica
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Slf4j
@Component
public class JustificantRecepcioSIRHelper extends JustificantHelper<NotificacioEnviamentEntity> {
	private final float LIST_SYMBOL_INDENT = 20;

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PluginHelper pluginHelper;
	public ListItem buildListRow(String text, String description) {
		Paragraph paragraf = new Paragraph();
		setParametersBold(paragraf, text);
		paragraf.add(" " + description);
		return new ListItem(paragraf);
	}
	@SneakyThrows
	public byte[] generarJustificant(
			NotificacioEnviamentEntity enviament,
			ProgresDescarregaDto progres) throws JustificantException {
		log.debug("Generant el justificant de recepció SIR de l'enviament [enviamentId=" + enviament.getId() + "]");
		RespostaConsultaRegistre resposta = pluginHelper.obtenerAsientoRegistral(
				enviament.getNotificacio().getEntitat().getDir3Codi(),
				enviament.getRegistreNumeroFormatat(),
				2L,
				false);
		progres.setProgres(30);

		Paragraph paragrafContingut = new Paragraph();
		if (NotificacioRegistreEstatEnumDto.OFICI_ACCEPTAT.equals(resposta.getEstat())) {
			setParametersBold(paragrafContingut, messageHelper.getMessage("es.caib.notib.justificant.sir.llista.acceptat"));
			List list = new List(false, LIST_SYMBOL_INDENT);
			list.add(buildListRow(messageHelper.getMessage("es.caib.notib.justificant.sir.llista.acceptat.item1"), resposta.getNumeroRegistroDestino()));
			list.add(buildListRow(messageHelper.getMessage("es.caib.notib.justificant.sir.llista.acceptat.item2"), resposta.getSirRegistreDestiData().toString()));
			paragrafContingut.add(list);
			paragrafContingut.add(Chunk.NEWLINE);

		} else if (NotificacioRegistreEstatEnumDto.REBUTJAT.equals(resposta.getEstat())) {
			setParametersBold(paragrafContingut, messageHelper.getMessage("es.caib.notib.justificant.sir.llista.rebutjat"));
			paragrafContingut.add(Chunk.NEWLINE);
			List list = new List(false, LIST_SYMBOL_INDENT);
			list.add(buildListRow(messageHelper.getMessage("es.caib.notib.justificant.sir.llista.rebutjat.item1"), resposta.getMotivo()));
			paragrafContingut.add(list);
			paragrafContingut.add(Chunk.NEWLINE);

		} else {
			throw new JustificantException("No es pot generar un justificant de recepció SIR d'un enviament que no està en un estat final");

		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			Document justificant = inicialitzaDocument(out, progres);

			progres.setProgres(50);
			progres.addInfo(ProgresDescarregaDto.TipusInfo.INFO, messageHelper.getMessage("es.caib.notib.justificant.proces.generant.titol"));
			crearTitolAndIntroduccio(
					justificant,
					conversioTipusHelper.convertir(
						enviament.getNotificacio(),
						NotificacioDtoV2.class
					),
					progres);

			progres.setProgres(80);
			justificant.add(paragrafContingut);
			justificant.close();
		} catch (DocumentException ex) {
			String errorMessage = messageHelper.getMessage("es.caib.notib.justificant.proces.generant.error", new Object[] {ex.getMessage()});
			progres.setProgres(100);
			progres.addInfo(ProgresDescarregaDto.TipusInfo.ERROR, errorMessage);
			log.error(
					errorMessage,
					ex);
		}
		return out.toByteArray();
	}


	protected void crearTitolAndIntroduccio(
			Document justificant,
			NotificacioDtoV2 notificacio,
			ProgresDescarregaDto progres) throws JustificantException {
		log.debug("Creant el títol i la introducció del justificant d'enviament de la notificacio [notificacioId=" + notificacio.getId() + "]");
		try {
//			## [TAULA QUE CONTÉ TÍTOL I INTRODUCCIÓ]
			PdfPTable titolIntroduccioTable = new PdfPTable(1);
			titolIntroduccioTable.setWidthPercentage(100);
			PdfPCell titolIntroduccioCell = new PdfPCell();
			titolIntroduccioCell.setBorder(Rectangle.NO_BORDER);

//			## [TITOL JUSTIFICANT]
			String titolMessage = messageHelper.getMessage("es.caib.notib.justificant.sir.titol");
			Paragraph justificantTitol = new Paragraph(titolMessage, frutigerTitolBold);
			justificantTitol.setAlignment(Element.ALIGN_CENTER);
			justificantTitol.add(Chunk.NEWLINE);
			justificantTitol.add(Chunk.NEWLINE);

//			## [INTRODUCCIÓ JUSTIFICANT]
			String introduccio = messageHelper.getMessage(
					"es.caib.notib.justificant.sir.introduccio",
					new Object[] {
							messageHelper.getMessage("es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto." + notificacio.getEnviamentTipus().name()),
							notificacio.getConcepte(),
							getDateTimeFormatted(notificacio.getNotificaEnviamentNotificaData() != null ? notificacio.getNotificaEnviamentNotificaData() : notificacio.getNotificaEnviamentData())});
			Paragraph justificantIntroduccio = new Paragraph();
			setParametersBold(justificantIntroduccio, introduccio);
			justificantIntroduccio.add(Chunk.NEWLINE);

//			## [DESCIPCIÓ NOFICACIÓ JUSTIFICANT]
//			Paragraph justificantDescripcio = new Paragraph();
//			if (notificacio.getDescripcio() != null && !notificacio.getDescripcio().isEmpty()) {
//				String descripcio = messageHelper.getMessage("es.caib.notib.justificant.sir.descripcio", new Object[] {notificacio.getDescripcio()});
//				setParametersBold(justificantDescripcio, descripcio);
//				justificantDescripcio.setSpacingBefore(10f);
//			}
//			## [INTRODUCCIÓ ANNEXOS JUSTIFICANT]
//			Paragraph justificantAnnexosIntroduccion = new Paragraph();
//			if (notificacio.getDocument() != null) {
//				String introAnnexos = messageHelper.getMessage("es.caib.notib.justificant.sir.documents");
//				setParametersBold(justificantAnnexosIntroduccion, introAnnexos);
//				justificantAnnexosIntroduccion.setSpacingBefore(10f);
//			}
//
			titolIntroduccioCell.addElement(justificantTitol);
			titolIntroduccioCell.addElement(justificantIntroduccio);
//			if (notificacio.getDescripcio() != null && !notificacio.getDescripcio().isEmpty())
//				titolIntroduccioCell.addElement(justificantDescripcio);
//			titolIntroduccioCell.addElement(justificantAnnexosIntroduccion);

			titolIntroduccioTable.addCell(titolIntroduccioCell);
			titolIntroduccioTable.setSpacingAfter(10f);
			justificant.add(titolIntroduccioTable);
		} catch (DocumentException ex) {
			String errorMessage = "Hi ha hagut un error generant la introducció del justificant";
			progres.setProgres(100);
			progres.addInfo(ProgresDescarregaDto.TipusInfo.INFO, errorMessage);
			log.debug(errorMessage, ex);
		}
	}

}

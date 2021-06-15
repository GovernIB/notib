package es.caib.notib.core.helper;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import es.caib.notib.core.api.dto.ProgresDescarregaDto;
import es.caib.notib.core.api.exception.JustificantException;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
public abstract class JustificantHelper<T> {
    protected final Font frutiger8 = FontFactory.getFont("Frutiger", 8, new BaseColor(127, 127, 127, 1)); // #7F7F7F
    protected final Font frutiger9 = FontFactory.getFont("Frutiger", 8, new BaseColor(127, 127, 127, 1)); // #7F7F7F
    protected final Font frutigerTitolBold = FontFactory.getFont("Frutiger", 11, Font.BOLD);
    protected final Font calibri10 = FontFactory.getFont("Calibri", 10);
    protected final Font calibri8 = FontFactory.getFont("Calibri", 8);
    protected final Font calibri10Bold = FontFactory.getFont("Calibri", 10, Font.BOLD);
    protected final Font calibriWhiteBold = FontFactory.getFont("Calibri", 10, Font.BOLD, new BaseColor(255, 255, 255)); // #FFFFFF

    @Autowired
    MessageHelper messageHelper;

    public abstract byte[] generarJustificant(
            T notificacio,
            ProgresDescarregaDto progres) throws JustificantException;

    @Builder
    protected static class JustificantTextKeys {
        String keyTitol;
        String keyTitolIntroduccio;
        String keyTitolDescripcio;
        String keyTitolIntroAnnexos;
    }

    protected void setParametersBold(Paragraph paragraph, String content) {
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

    protected String getDateTimeFormatted(Date date)  {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        long now = date.getTime();
        return formatter.format(now);
    }

    protected Document inicialitzaDocument(
            ByteArrayOutputStream out,
            ProgresDescarregaDto progres) throws DocumentException, JustificantException {
        log.debug("Inicialitzant el document per la generació del justificant d'enviament");
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
    protected class FooterPageEvent extends PdfPageEventHelper {
        private PdfPTable footer;

        public void onEndPage(PdfWriter writer, Document justificant) {
            footer.writeSelectedRows(
                    0,
                    -1,
                    36,
                    80,
                    writer.getDirectContent());
        }

        protected FooterPageEvent(ProgresDescarregaDto progres) throws JustificantException {
            String accioDescripcio = messageHelper.getMessage("es.caib.notib.justificant.proces.iniciant.footer");
            log.debug(accioDescripcio);
            progres.setProgres(25);
            progres.addInfo(ProgresDescarregaDto.TipusInfo.INFO, accioDescripcio);
            try {
                footer = new PdfPTable(2);
                footer.setTotalWidth(523);
                footer.setLockedWidth(true);
                Image logoPeu = null;
                PdfPCell cellTitolPeu = new PdfPCell();
                if (getPeuTitol() != null) {
//					## [PEU - TÍTOL]
                    Paragraph peuTitol = new Paragraph(getPeuTitol(), frutiger9);
                    peuTitol.setAlignment(Element.ALIGN_LEFT);


                    cellTitolPeu.addElement(peuTitol);
                    cellTitolPeu.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellTitolPeu.setBorder(Rectangle.NO_BORDER);
                }
                footer.addCell(cellTitolPeu);

//				## [LOGO ENTITAT]
                if (getPeuLogo() != null) {
                    logoPeu = Image.getInstance(getPeuLogo());
                } else {
                    byte[] logoBytes = IOUtils.toByteArray(getPeuDefaultLogo());
                    logoPeu = Image.getInstance(logoBytes);
                }

//				## [PEU - LOGO]
                logoPeu.setScaleToFitHeight(true);
                logoPeu.scaleToFit(100, 80);
                PdfPCell cellLogo = new PdfPCell(logoPeu);
                cellLogo.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellLogo.setBorder(Rectangle.NO_BORDER);
                footer.addCell(cellLogo);
            } catch (Exception ex) {
                String errorMessage = messageHelper.getMessage("es.caib.notib.justificant.proces.iniciant.footer.error");
                progres.setProgres(100);
                progres.addInfo(ProgresDescarregaDto.TipusInfo.ERROR, errorMessage);
                log.error(errorMessage, ex);
            }
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
    }

    protected class HeaderPageEvent extends PdfPageEventHelper {
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

        protected HeaderPageEvent(ProgresDescarregaDto progres) throws JustificantException {
            String accioDescripcio = messageHelper.getMessage("es.caib.notib.justificant.proces.iniciant.header");
            log.debug(accioDescripcio);
            progres.setProgres(15);
            progres.addInfo(ProgresDescarregaDto.TipusInfo.INFO, accioDescripcio);
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
                progres.addInfo(ProgresDescarregaDto.TipusInfo.ERROR, errorMessage);
                log.error(errorMessage, ex);
            }
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
    }

    private String getNotBlankProperty(String property) {
        if (property == null || property.trim().isEmpty())
            return null;
        return property.trim();
    }
}

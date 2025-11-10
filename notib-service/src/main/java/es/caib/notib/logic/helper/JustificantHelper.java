package es.caib.notib.logic.helper;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import es.caib.notib.logic.intf.dto.ProgresDescarregaDto;
import es.caib.notib.logic.intf.exception.JustificantException;
import joptsimple.internal.Strings;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
public abstract class JustificantHelper<T> {

    private static final String FRUTIGER = "Frutiger";
    private static final String CALIBRI = "Calibri";
    protected final Font frutiger8 = FontFactory.getFont(FRUTIGER, 8, new BaseColor(127, 127, 127, 1)); // #7F7F7F
    protected final Font frutiger9 = FontFactory.getFont(FRUTIGER, 8, new BaseColor(127, 127, 127, 1)); // #7F7F7F
    protected final Font frutigerTitolBold = FontFactory.getFont(FRUTIGER, 11, Font.BOLD);
    protected final Font calibri10 = FontFactory.getFont(CALIBRI, 10);
    protected final Font calibri8 = FontFactory.getFont(CALIBRI, 8);
    protected final Font calibri10Bold = FontFactory.getFont(CALIBRI, 10, Font.BOLD);
    protected final Font calibriWhiteBold = FontFactory.getFont(CALIBRI, 10, Font.BOLD, new BaseColor(255, 255, 255)); // #FFFFFF


    @Autowired
    protected MessageHelper messageHelper;
    @Autowired
    protected ConfigHelper configHelper;

    public abstract byte[] generarJustificant(T notificacio, ProgresDescarregaDto progres) throws JustificantException, IOException;

    @Builder
    protected static class JustificantTextKeys {

        String keyTitol;
        String keyTitolIntroduccio;
        String keyTitolDescripcio;
        String keyTitolIntroAnnexos;
    }

    protected void setParametersBold(Paragraph paragraph, String content) {

        Chunk caracter;
        for (var i = 0; i < content.length(); i++) {
            if (content.charAt(i) == '[') {
                while (content.charAt(i + 1) != ']') {
                    caracter = new Chunk(String.valueOf(content.charAt(i + 1)), calibri10Bold);
                    paragraph.add(caracter);
                    i++;
                }
                continue;
            }
            if (content.charAt(i) != '[' && content.charAt(i) != ']') {
                caracter = new Chunk(String.valueOf(content.charAt(i)), calibri10);
                paragraph.add(caracter);
            }
        }
    }

    protected String getDateTimeFormatted(Date date)  {

        var formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        var now = date.getTime();
        return formatter.format(now);
    }

    protected Document inicialitzaDocument(ByteArrayOutputStream out, ProgresDescarregaDto progres, String entitatCodi) throws DocumentException, JustificantException {

        log.debug("Inicialitzant el document per la generació del justificant d'enviament");
//		## [Event per crear el header]
        var headerEvent = new HeaderPageEvent(progres, entitatCodi);
//		## [Event per crear el footer]
        var footerEvent = new FooterPageEvent(progres);
        var justificant = new Document(PageSize.A4, 36, 36, 35 + headerEvent.getTableHeight(), 36);
        var writer = PdfWriter.getInstance(justificant, out);
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

        @Override
        public void onEndPage(PdfWriter writer, Document justificant) {
            footer.writeSelectedRows(0, -1, 36, 80, writer.getDirectContent());
        }

        protected FooterPageEvent(ProgresDescarregaDto progres) throws JustificantException {

            var accioDescripcio = messageHelper.getMessage("es.caib.notib.justificant.proces.iniciant.footer");
            log.debug(accioDescripcio);
            progres.setProgres(25);
            progres.addInfo(ProgresDescarregaDto.TipusInfo.INFO, accioDescripcio);
            try {
                footer = new PdfPTable(2);
                footer.setTotalWidth(523);
                footer.setLockedWidth(true);
                var cellTitolPeu = new PdfPCell();
                if (getPeuTitol() != null) {
//					## [PEU - TÍTOL]
                    var peuTitol = new Paragraph(getPeuTitol(), frutiger9);
                    peuTitol.setAlignment(Element.ALIGN_LEFT);
                    cellTitolPeu.addElement(peuTitol);
                    cellTitolPeu.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellTitolPeu.setBorder(Rectangle.NO_BORDER);
                }
                footer.addCell(cellTitolPeu);

                var cellDireccio = new PdfPCell();
                if (getNifDireccio() != null) {
//					## [DIRECCIO - NIF]
                    var direccioNif = new Paragraph(getNifDireccio(), frutiger8);
                    direccioNif.setAlignment(Element.ALIGN_RIGHT);
                    direccioNif.setLeading(0, (float) 1.25);
                    cellDireccio.addElement(direccioNif);
                }
                if (getCodiDireccio() != null) {
//					## [DIRECCIO - CODIDIR3]
                    var direccioCodi = new Paragraph(getCodiDireccio(), frutiger8);
                    direccioCodi.setAlignment(Element.ALIGN_RIGHT);
                    direccioCodi.setLeading(0, (float) 1.25);
                    cellDireccio.addElement(direccioCodi);
                }
                if (getDireccio() != null) {
//					## [DIRECCIO - CARRER]
                    var direccio = new Paragraph(getDireccio(), frutiger8);
                    direccio.setAlignment(Element.ALIGN_RIGHT);
                    direccio.setLeading(0, (float) 1.25);
                    cellDireccio.addElement(direccio);
                }
                if (getEmailDireccio() != null) {
//					## [DIRECCIO - EMAIL]
                    Chunk direccioEmailChunk = new Chunk(getEmailDireccio(), frutiger8);
                    direccioEmailChunk.setUnderline(1.5f, -1);
                    var direccioEmail = new Paragraph(direccioEmailChunk);
                    direccioEmail.setAlignment(Element.ALIGN_RIGHT);
                    direccioEmail.setLeading(0, (float) 1.25);
                    cellDireccio.addElement(direccioEmail);
                }
                cellDireccio.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellDireccio.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cellDireccio.setBorder(Rectangle.NO_BORDER);
                footer.addCell(cellDireccio);
            } catch (Exception ex) {
                var errorMessage = messageHelper.getMessage("es.caib.notib.justificant.proces.iniciant.footer.error");
                progres.setProgres(100);
                progres.addInfo(ProgresDescarregaDto.TipusInfo.ERROR, errorMessage);
                log.error(errorMessage, ex);
            }
        }

        private String getDireccio() {
            return getNotBlankProperty(configHelper.getConfig("es.caib.notib.justificant.capsalera.direccio"));
        }

        private String getNifDireccio() {
            return getNotBlankProperty(configHelper.getConfig("es.caib.notib.justificant.capsalera.nif"));
        }

        private String getCodiDireccio() {
            return getNotBlankProperty(configHelper.getConfig("es.caib.notib.justificant.capsalera.codi"));
        }

        private String getEmailDireccio() {
            return getNotBlankProperty(configHelper.getConfig("es.caib.notib.justificant.capsalera.email"));
        }

        private String getPeuTitol() {
            return getNotBlankProperty(configHelper.getConfig("es.caib.notib.justificant.peu.titol"));
        }
    }

    protected class HeaderPageEvent extends PdfPageEventHelper {

        private PdfPTable header;
        private float tableHeight;

        public float getTableHeight() {
            return tableHeight;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document justificant) {

            var yPos = 750 + ((justificant.topMargin() + tableHeight) / 2);
            header.writeSelectedRows(0, -1, justificant.left(), yPos, writer.getDirectContent());
        }

        protected HeaderPageEvent(ProgresDescarregaDto progres, String entitatCodi) throws JustificantException {

            var accioDescripcio = messageHelper.getMessage("es.caib.notib.justificant.proces.iniciant.header");
            log.debug(accioDescripcio);
            progres.setProgres(15);
            progres.addInfo(ProgresDescarregaDto.TipusInfo.INFO, accioDescripcio);
            try {
                header = new PdfPTable(2);
                header.setTotalWidth(523);
                header.setLockedWidth(true);

//				## [LOGO ENTITAT]
                Image logoCapsalera;
                var logo = getCapsaleraLogo(entitatCodi);
                if (!Strings.isNullOrEmpty(logo)) {
                    logoCapsalera = Image.getInstance(logo);
                } else {
                    byte[] logoBytes = IOUtils.toByteArray(getCapsaleraDefaultLogo());
                    logoCapsalera = Image.getInstance(logoBytes);
                }
                if (logoCapsalera != null) {
                    logoCapsalera.scaleToFit(120f, 50f);
                    var cellLogo = new PdfPCell(logoCapsalera);
                    cellLogo.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellLogo.setBorder(Rectangle.NO_BORDER);
                    header.addCell(cellLogo);
                }

//				## [LOGO ENTITAT]
                Image logoNotibImage;
//                logo = getPeuLogo(entitatCodi);
                var logoNotib = getLogoNotib();
                if (logoNotib != null) {
                    byte[] logoBytes = IOUtils.toByteArray(logoNotib);
                    logoNotibImage = Image.getInstance(logoBytes);
                    logoNotibImage.setScaleToFitHeight(true);
                    logoNotibImage.scaleToFit(100, 80);
                    var cellLogo = new PdfPCell(logoNotibImage);
                    cellLogo.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cellLogo.setBorder(Rectangle.NO_BORDER);
                    header.addCell(cellLogo);
                    tableHeight = header.getTotalHeight();
                }
//                else {
//                    byte[] logoBytes = IOUtils.toByteArray(getPeuDefaultLogo());
//                    logoPeu = Image.getInstance(logoBytes);
//                }

//				## [PEU - LOGO]

            } catch (Exception ex) {
                String errorMessage = messageHelper.getMessage("es.caib.notib.justificant.proces.iniciant.header.error");
                progres.setProgres(100);
                progres.addInfo(ProgresDescarregaDto.TipusInfo.ERROR, errorMessage);
                log.error(errorMessage, ex);
            }
        }
        private InputStream getPeuDefaultLogo() {
            return getClass().getResourceAsStream("/es/caib/notib/logic/justificant/logo.png");
        }

        private InputStream getLogoNotib() {
            return getClass().getResourceAsStream("/es/caib/notib/logic/justificant/logo.png");
        }

        private String getPeuLogo(String entitatCodi) {

            var logoEntitat = configHelper.getConfigByEntitat(entitatCodi, "es.caib.notib.peu.logo");
            return getNotBlankProperty(logoEntitat);
        }

        private String getCapsaleraLogo(String entitatCodi) {

            var logoEntitat = configHelper.getConfigByEntitat(entitatCodi, "es.caib.notib.capsalera.logo");
            return getNotBlankProperty(logoEntitat);
        }

        private InputStream getCapsaleraDefaultLogo() {
            return getClass().getResourceAsStream("/es/caib/notib/logic/justificant/govern-logo.png");
        }
    }

    private String getNotBlankProperty(String property) {
        return property != null && !property.trim().isEmpty() ? property.trim() : null;
    }
}

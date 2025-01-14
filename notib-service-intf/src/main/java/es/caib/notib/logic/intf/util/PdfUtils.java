package es.caib.notib.logic.intf.util;

import com.google.common.base.Strings;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class PdfUtils extends PdfReader {

    @Getter
    private boolean dinA4;
    private boolean transparency;
    private boolean externalLinks;
    private boolean multimedia;
    private boolean nonePrintableAnnotations;
    private boolean noneEmbeddedImages;
    private boolean noneEmbeddedFonts;
    @Getter
    private boolean maxRightMarginOk;

    public PdfUtils(byte [] bytes) throws IOException {

        super(bytes);
        float requiredRightMargin = 56.7f; // Define the required right margin in points (2 cm = 56.7 points)
        for (var pageNumber = 1; pageNumber <= getNumberOfPages(); pageNumber++) {
            dinA4 = dinA4 || checkDinA4(pageNumber);
            maxRightMarginOk = maxRightMarginOk || checkRightMargin(pageNumber, requiredRightMargin);
            loopAnnotations(pageNumber);
        }
    }

    public boolean versionGreaterThan(String version) {
        return version != null && version.compareTo(this.getPdfVersion() + "") < 0;
    }

    private boolean checkDinA4(int pageNumber) {

        var rect = this.getPageSizeWithRotation(pageNumber);
        var width = rect.getWidth();
        var height = rect.getHeight();
        return (int) width == 595 && (int) height == 841;
    }

    private boolean checkRightMargin(int pageNumber, float maxMargin) {

        var pageDict = getPageN(pageNumber);
        var mediaBox = pageDict.getAsArray(PdfName.MEDIABOX);
//        PdfArray cropBox = pageDict.getAsArray(PdfName.CROPBOX);
        var llx = mediaBox.getAsNumber(0).floatValue();
        var lly = mediaBox.getAsNumber(1).floatValue();
        var urx = mediaBox.getAsNumber(2).floatValue();
        var ury = mediaBox.getAsNumber(3).floatValue();
        var pageSize = new Rectangle(llx, lly, urx, ury);
        var pageWidth = pageSize.getWidth();
        // Assuming the content width is the width of the page minus the right margin
        var contentWidth = pageWidth - maxMargin;
//        System.out.println("Right margin is " + (pageWidth - contentWidth >= maxMargin ? "sufficient" : "insufficient"));
        return  pageWidth - contentWidth >= maxMargin;
    }

    private void loopAnnotations(int pageNumber) {

        var parser = new PdfReaderContentParser(this);
        checkNoneEmbeddedImages(parser, pageNumber);
        noneEmbeddedFonts = noneEmbeddedFonts || checkNoneEmbeddedFonts(pageNumber);

        var annots = getPageN(pageNumber).getAsArray(PdfName.ANNOTS);
        if (annots == null) {
            return;
        }
        for (var annot = 0; annot < annots.size(); annot++) {
            var annotation = annots.getAsDict(annot);
            transparency = transparency || checkTransparency(annotation);
            externalLinks = externalLinks || checkExternalLinks(annotation);
            multimedia = multimedia || checkMultimedia(annotation);
            nonePrintableAnnotations = nonePrintableAnnotations || checkNonePrintableAnnotations(annotation);
        }
    }

    private boolean checkNoneEmbeddedFonts(int pageNumber) {

        var pageDict = getPageN(pageNumber);
        var resources = pageDict.getAsDict(PdfName.RESOURCES);
        var fonts = resources.getAsDict(PdfName.FONT);
        if (fonts == null) {
            return false;
        }
        for (PdfName fontName : fonts.getKeys()) {
            PdfDictionary fontDict = fonts.getAsDict(fontName);
            PdfDictionary fontDescriptor = fontDict.getAsDict(PdfName.FONTDESCRIPTOR);
            if (fontDescriptor == null) {
                return true;
            }
            PdfObject fontFile = fontDescriptor.getDirectObject(PdfName.FONTFILE);
            PdfObject fontFile2 = fontDescriptor.getDirectObject(PdfName.FONTFILE2);
            PdfObject fontFile3 = fontDescriptor.getDirectObject(PdfName.FONTFILE3);
            if (fontFile == null && fontFile2 == null && fontFile3 == null) {
                return true;
            }
        }
        return false;
    }

    private void checkNoneEmbeddedImages(PdfReaderContentParser parser, int pageNumber) {

        try {
            parser.processContent(pageNumber, new RenderListener() {
                @Override
                public void beginTextBlock() {
                }

                @Override
                public void renderText(TextRenderInfo renderInfo) {
                }

                @Override
                public void endTextBlock() {
                }

                @Override
                public void renderImage(ImageRenderInfo renderInfo) {
                    try {
                        PdfImageObject image = renderInfo.getImage();
                        if (image == null) {
                            noneEmbeddedImages = true;
//                        System.out.println("Non-embedded image found on page " + renderInfo.getRef().getPageNumber());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            log.error("Error parsejant les imatges del document " + getInfo().get("Title"));
        }
    }

    private boolean checkTransparency(PdfDictionary annotation) {

        var resources = annotation.getAsDict(PdfName.RESOURCES);
        if (resources == null) {
            return false;
        }
        var extGState = resources.getAsDict(PdfName.EXTGSTATE);
        if (extGState == null) {
            return false;
        }
        PdfDictionary gs;
        PdfObject ca;
        PdfObject caFill;
        for (var key : extGState.getKeys()) {
            gs = extGState.getAsDict(key);
            ca = gs.get(PdfName.CA); // Stroke opacity
            caFill = gs.get(PdfName.ca); // Fill opacity
            if (ca != null || caFill != null) {
                return true;
            }
        }
        return false;
    }

    private boolean checkExternalLinks(PdfDictionary annotation) {

        if (!PdfName.LINK.equals(annotation.getAsName(PdfName.SUBTYPE))) {
            return false;
        }
        var action = annotation.getAsDict(PdfName.A);
        return action != null && PdfName.URI.equals(action.getAsName(PdfName.S));
    }

    private boolean checkMultimedia(PdfDictionary annotation) {
        return PdfName.RICHMEDIA.equals(annotation.getAsName(PdfName.SUBTYPE)) || PdfName.SCREEN.equals(annotation.getAsName(PdfName.SUBTYPE));
    }

    private boolean checkNonePrintableAnnotations(PdfDictionary annotation) {
        return PdfName.PRINT.equals(annotation.getAsName(PdfName.F));
    }

    public boolean maxPages(String tipusImpresio) {

        var nPages = this.getNumberOfPages();
        var isDuplex = false;
        if (!Strings.isNullOrEmpty(tipusImpresio) && tipusImpresio.equals("DUPLEX")) {
            nPages = nPages*2;
            isDuplex = true;
        }
        return !isDuplex ? nPages <= 79 : nPages <= 158;
    }

    public boolean isPrintingAllowed() {
        return (getPermissions() & PdfWriter.ALLOW_PRINTING) != 0;
    }

    public boolean isModifyAllowed() {
        return (getPermissions() & PdfWriter.ALLOW_MODIFY_CONTENTS) != 0;
    }

    public boolean isEditBlocked() {
        return this.isEncrypted();
    }

    public boolean hasNoneEmbeddedFonts() {
        return noneEmbeddedFonts;
    }

    public boolean hasTransparency() {
        return transparency;
    }

    public boolean hasExternalLinks() {
        return externalLinks;
    }

    public boolean hasMultimedia() {
        return multimedia;
    }

    public boolean hasNonPrintableAnnotations() {
        return nonePrintableAnnotations;
    }

    public boolean hasForms() {

        var acroFields = getAcroFields();
        if (acroFields == null) {
            return false;
        }
        var fields = acroFields.getFields();
        return fields != null && !fields.isEmpty();
    }

    public boolean hasNoneEmbeddedImages() {
        return noneEmbeddedImages;
    }

    public boolean  hasAttachedFiles() {

        var catalog = getCatalog();
        if (catalog == null) {
            return false;
        }
        var names = catalog.getAsDict(PdfName.NAMES);
        if (names == null) {
            return false;
        }
        var embeddedFiles = catalog.getAsDict(PdfName.EMBEDDEDFILES);
        if (embeddedFiles == null) {
            return false;
        }
        var files = embeddedFiles.getAsArray(PdfName.NAMES);
        return files != null && !files.isEmpty();
//        return !getCatalog().getAsDict(PdfName.NAMES).getAsDict(PdfName.EMBEDDEDFILES).getAsArray(PdfName.NAMES).isEmpty();
    }
}

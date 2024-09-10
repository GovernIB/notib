package es.caib.notib.logic.intf.util;

import com.google.common.base.Strings;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import lombok.Getter;

import java.io.IOException;

public class PdfUtils extends PdfReader {

    @Getter
    private boolean dinA4;
    private boolean transparency;
    private boolean externalLinks;
    private boolean multimedia;
    private boolean nonePrintableAnnotations;
    private boolean noneEmbeddedImages;

    public PdfUtils(byte [] bytes) throws IOException {

        super(bytes);
        for (var pageNumber = 1; pageNumber <= getNumberOfPages(); pageNumber++) {
            dinA4 = dinA4 || checkDinA4(pageNumber);
            loopAnnotations(pageNumber);
        }
    }

    public boolean versionGreaterThan(String version) {
        return version != null && version.compareTo(this.getPdfVersion() + "") > 0;
    }

    private boolean checkDinA4(int pageNumber) {

        var rect = this.getPageSizeWithRotation(pageNumber);
        var width = rect.getWidth();
        var height = rect.getHeight();
        return (int) width == 595 && (int) height == 841;
    }

    private void loopAnnotations(int pageNumber) {

        var annots = getPageN(pageNumber).getAsArray(PdfName.ANNOTS);
        if (annots == null) {
            return;
        }
        for (var annot = 0; annot < annots.size(); annot++) {
            var annotation = annots.getAsDict(annot);
            transparency = transparency || checkTransparency(annotation);
            externalLinks = externalLinks || checkExternalLinks(annotation);
            multimedia = multimedia || checkMultimedia(annotation);
            nonePrintableAnnotations = nonePrintableAnnotations || checkNonPrintableAnnotations(annotation);
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

    private boolean checkNonPrintableAnnotations(PdfDictionary annotation) {
        return PdfName.PRINT.equals(annotation.getAsName(PdfName.F));
    }

//    public boolean isDinA4() {
//
//        Rectangle rect;
//        float width, height;
//        for (int i = 1; i <= this.getNumberOfPages(); i++) {
//            rect = this.getPageSizeWithRotation(i);
//            width = rect.getWidth();
//            height = rect.getHeight();
//            if ((int) width != 595 || (int) height != 841) {
//                return false;
//            }
//        }
//        return true;
//    }

    public boolean maxPages(String tipusImpresio) {

        var nPages = this.getNumberOfPages();
        var isDuplex = false;
        if (!Strings.isNullOrEmpty(tipusImpresio) && tipusImpresio.equals("DUPLEX")) {
            nPages = nPages*2;
            isDuplex = true;
        }
        return !isDuplex ? nPages <= 79 : nPages <= 158;
    }

    public boolean isEditBlocked() {
        return this.isEncrypted();
    }

    public boolean checkFontsEmbeded() {
        return true; // TODO FALTA FER
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
        return !getAcroFields().getFields().isEmpty();
    }

    public boolean hasNoneEmbeddedImages() {
        return noneEmbeddedImages;
    }

//    public boolean hasExternalLinks() {
//
//        for (var pageN = 1; pageN < this.getNumberOfPages(); pageN++) {
//            PdfArray annots = getPageN(pageN).getAsArray(PdfName.ANNOTS);
//            if (annots == null) {
//                continue;
//            }
//            for (int j = 0; j < annots.size(); j++) {
//                var annotDict = annots.getAsDict(j);
//                if (!PdfName.LINK.equals(annotDict.getAsName(PdfName.SUBTYPE))) {
//                    continue;
//                }
//                var action = annotDict.getAsDict(PdfName.A);
//                if (action != null && PdfName.URI.equals(action.getAsName(PdfName.S))) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    public boolean hasTransparency() {
//
//        for (int i = 1; i <= getNumberOfPages(); i++) {
//            PdfDictionary pageDict = getPageN(i);
//            PdfDictionary resources = pageDict.getAsDict(PdfName.RESOURCES);
//            if (resources != null) {
//                return false;
//            }
//                PdfDictionary extGState = resources.getAsDict(PdfName.EXTGSTATE);
//                if (extGState != null) {
//                    return false;
//                }
//                for (PdfName key : extGState.getKeys()) {
//                    PdfDictionary gs = extGState.getAsDict(key);
//                    PdfObject ca = gs.get(PdfName.CA); // Stroke opacity
//                    PdfObject caFill = gs.get(PdfName.ca); // Fill opacity
//                    if (ca != null || caFill != null) {
//                        return true;
//                    }
//                }
//        }
//        return false;
//    }

    public boolean hasAttachedFiles() {
        return !getCatalog().getAsDict(PdfName.NAMES).getAsDict(PdfName.EMBEDDEDFILES).getAsArray(PdfName.NAMES).isEmpty();
    }
}

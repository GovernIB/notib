package es.caib.notib.logic.intf.util;

import com.google.common.base.Strings;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;

import java.io.IOException;

public class PdfUtils extends PdfReader {

    public PdfUtils(byte [] bytes) throws IOException {
        super(bytes);
    }

    public boolean versionGreaterThan(String version) {
        return version != null && version.compareTo(this.getPdfVersion() + "") > 0;
    }

    public boolean isDinA4() {

        Rectangle rect;
        float width, height;
        for (int i = 1; i <= this.getNumberOfPages(); i++) {
            rect = this.getPageSizeWithRotation(1);
            width = rect.getWidth();
            height = rect.getHeight();
            if ((int) width != 595 || (int) height != 841) {
                return false;
            }
        }
        return true;
    }

    public boolean maxPages(String tipusImpresio) {

        var nPages = this.getNumberOfPages();
        var isDuplex = false;
        if (!Strings.isNullOrEmpty(tipusImpresio) && tipusImpresio.equals("DUPLEX")) {
            nPages = nPages*2;
            isDuplex = true;
        }
        return !isDuplex ? nPages <= 80 : nPages <= 160;
    }

    public boolean isEditBlocked() {
        return this.isEncrypted();
    }

    public boolean checkFontsEmbeded() {
        return true;
    }
}

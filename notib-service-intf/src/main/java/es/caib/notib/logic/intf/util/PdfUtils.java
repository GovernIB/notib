package es.caib.notib.logic.intf.util;

import com.google.common.base.Strings;
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
}
